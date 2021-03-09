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

package connectors

import config.FrontendAppConfig
import uk.gov.hmrc.http.HttpReads.Implicits._
import models.DepartureId
import models.response.ResponseDeparture
import play.api.Logger
import play.api.http.HeaderNames
import uk.gov.hmrc.http.HttpReads.is2xx
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class DepartureMovementConnector @Inject()(val appConfig: FrontendAppConfig, http: HttpClient)(implicit ec: ExecutionContext) {
  implicit val hc: HeaderCarrier = HeaderCarrier(Some(Authorization("BearerToken")))

  val logger: Logger          = Logger(getClass)
  private val channel: String = "web"

  def getDeparture(departureId: DepartureId)(implicit hc: HeaderCarrier): Future[Option[ResponseDeparture]] = {
    val serviceUrl = s"${appConfig.departureUrl}/movements/departures/${departureId.index}"
    val header     = hc.withExtraHeaders(ChannelHeader(channel))

    http.GET[HttpResponse](serviceUrl)(httpReads, header, ec) map {
      case responseMessage if is2xx(responseMessage.status) =>
          Option(responseMessage.json.as[ResponseDeparture])
      case _ =>
        logger.error("getDeparture failed to return data")
        None
    }
  }

  object ChannelHeader {
    def apply(value: String): (String, String) = ("Channel", value)
  }

  object ContentTypeHeader {
    def apply(value: String): (String, String) = (HeaderNames.CONTENT_TYPE, value)
  }

  object AuthorizationHeader {
    def apply(value: String): (String, String) = (HeaderNames.AUTHORIZATION, value)
  }
}