/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.actions
import connectors.DepartureMovementConnector
import controllers.routes
import models.DepartureId
import models.requests.{IdentifierRequest}
import models.response.ResponseDeparture
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionFilter, Result}
import uk.gov.hmrc.play.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckCancellationStatusProvider @Inject()(departureMovementConnector: DepartureMovementConnector)(implicit ec: ExecutionContext) {
  def apply(departureId: DepartureId): ActionFilter[IdentifierRequest] = new CancellationStatusAction(departureId, departureMovementConnector)

}

class CancellationStatusAction(
  departureId: DepartureId,
  departureMovementConnector: DepartureMovementConnector
)(implicit protected val executionContext: ExecutionContext)
    extends ActionFilter[IdentifierRequest] {

  // TODO: Refactor
  final val validStatus: Seq[String] = Seq("DepartureSubmitted", "MrnAllocated", "PositiveAcknowledgement");

  override protected def filter[A](request: IdentifierRequest[A]): Future[Option[Result]] = {

    implicit val hc = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
    departureMovementConnector.getDeparture(departureId).flatMap {
      case Some(responseDeparture: ResponseDeparture) if (!validStatus.contains(responseDeparture.status)) =>
        Future.successful(Option(Redirect(routes.CanNotCancelController.onPageLoad())));

      case Some(responseDeparture: ResponseDeparture) if (validStatus.contains(responseDeparture.status)) =>
        Future.successful(None);

      case None =>
        Future.successful(Option(Redirect(routes.DepartureNotFoundController.onPageLoad(departureId))));
    }
  }
}