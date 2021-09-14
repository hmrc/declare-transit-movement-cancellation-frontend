package controllers

import controllers.actions._
import forms.$className$FormProvider
import javax.inject.Inject
import models.{Mode, DepartureId, UserAnswers}
import navigation.Navigator
import pages.$className$Page
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.{NunjucksSupport, Radios}

import scala.concurrent.{ExecutionContext, Future}

class $className;format="cap"$Controller @Inject()(
    override val messagesApi: MessagesApi,
    sessionRepository: SessionRepository,
    navigator: Navigator,
    identify: IdentifierAction,
    getData: DataRetrievalActionProvider,
    formProvider: $className$FormProvider,
    requireData: DataRequiredAction,
    checkCancellationStatus: CheckCancellationStatusProvider,
    val controllerComponents: MessagesControllerComponents,
    renderer: Renderer
)(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with NunjucksSupport {

  private val form = formProvider()
  private val template = "$className;format="decap"$.njk"

  def onPageLoad(departureId: DepartureId, mode: Mode): Action[AnyContent] =
  (identify andThen checkCancellationStatus(departureId) andThen getData(departureId)).async {
    implicit request =>

  val json = Json.obj(
        "form"        -> form,
        "lrn"         -> request.lrn,
        "mode"        -> mode,
        "departureId" -> departureId,
        "radios"      -> Radios.yesNo(form("value"))
      )

      renderer.render(template, json).map(Ok(_))
  }

  def onSubmit(departureId: DepartureId, mode: Mode): Action[AnyContent] =
  (identify andThen checkCancellationStatus(departureId) andThen getData(departureId)).async {
    implicit request =>

      form.bindFromRequest()
        .fold(
        formWithErrors => {

          val json = Json.obj(
            "form"           -> formWithErrors,
            "lrn"            -> request.lrn,
            "mode"           -> mode,
            "departureId"    -> departureId,
            "radios"         -> Radios.yesNo(formWithErrors("value"))
          )

          renderer.render(template, json).map(BadRequest(_))
        },

      value => {
          val userAnswers = request.userAnswers match {
            case Some(value) => value
            case None => UserAnswers(departureId, request.eoriNumber)
            }
        for {
        updatedAnswers <- Future.fromTry(userAnswers.set($className$Page(departureId), value))
        _ <- sessionRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage($className$Page(departureId), mode, updatedAnswers, departureId))
       }
      )
  }
}
