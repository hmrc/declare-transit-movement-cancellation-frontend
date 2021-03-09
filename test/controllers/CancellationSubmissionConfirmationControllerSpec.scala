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

package controllers

import base.{MockNunjucksRendererApp, SpecBase}
import connectors.DepartureMovementConnector
import matchers.JsonMatchers
import models.LocalReferenceNumber
import models.response.ResponseDeparture
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html

import scala.concurrent.Future

class CancellationSubmissionConfirmationControllerSpec extends SpecBase with MockitoSugar with JsonMatchers with MockNunjucksRendererApp {

  private val mockDepartureResponse: ResponseDeparture = {
    ResponseDeparture(
      LocalReferenceNumber("lrn"),
      "Submitted"
    )
  }
  def onwardRoute = Call("GET", "/foo")
  lazy val cancellationSubmissionRoute = routes.CancellationSubmissionConfirmationController.onPageLoad(departureId).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator])toInstance(new FakeNavigator(onwardRoute)))


  "CancellationSubmissionConfirmation Controller" - {

    "return OK and the correct view for a GET" in {

      val mockConnector = mock[DepartureMovementConnector]

      when(mockConnector.getDeparture(any())(any()))
        .thenReturn(Future.successful(Some(mockDepartureResponse)))

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      dataRetrievalWithData(emptyUserAnswers)

      val request = FakeRequest(GET,cancellationSubmissionRoute)

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(app, request).value

      status(result) mustEqual OK

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj()

      templateCaptor.getValue mustEqual "cancellationSubmissionConfirmation.njk"
      jsonCaptor.getValue must containJson(expectedJson)



    }
  }
}