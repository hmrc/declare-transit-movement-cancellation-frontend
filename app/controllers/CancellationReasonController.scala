/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.FrontendAppConfig
import controllers.actions._
import forms.CancellationReasonFormProvider
import models.{DepartureId, Mode}
import navigation.Navigator
import pages.CancellationReasonPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.CancellationSubmissionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import views.html.{CancellationReason, ConfirmCancellation}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CancellationReasonController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  checkCancellationStatus: CheckCancellationStatusProvider,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  navigator: Navigator,
  formProvider: CancellationReasonFormProvider,
  cancellationSubmissionService: CancellationSubmissionService,
  val controllerComponents: MessagesControllerComponents,
  renderer: Renderer,
  appConfig: FrontendAppConfig,
  view: CancellationReason
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private val form     = formProvider()
  private val template = "cancellationReason.njk"

  def onPageLoad(departureId: DepartureId): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId) andThen requireData).async {
      implicit request =>
        Future.successful(Ok(view(form, departureId, request.lrn)))
    }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] =
    (identify andThen checkCancellationStatus(departureId) andThen getData(departureId) andThen requireData).async {

      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => {
              Future.successful(BadRequest(view(formWithErrors, departureId, request.lrn)))
            },
            value => {
              Future
                .fromTry(request.userAnswers.set(CancellationReasonPage(departureId), value))
                .flatMap(updatedAnswers =>
                  cancellationSubmissionService.submitCancellation(updatedAnswers).flatMap {
                    case Right(_) => Future.successful(Redirect(navigator.nextPage(CancellationReasonPage(departureId), mode, updatedAnswers, departureId)))
                    case Left(_) => {

                      // TODO twirl techdiff template

                      val json = Json.obj(
                        "contactUrl" -> appConfig.nctsEnquiriesUrl
                      )
                      renderer.render("technicalDifficulties.njk", json).map(content => InternalServerError(content))
                    }
                })
            }
          )

    }
}
