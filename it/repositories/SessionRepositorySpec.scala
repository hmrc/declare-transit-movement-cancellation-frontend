/*
 * Copyright 2023 HM Revenue & Customs
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

package repositories

import config.FrontendAppConfig
import models.{DepartureId, EoriNumber, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import scala.concurrent.ExecutionContext.Implicits.global

class SessionRepositorySpec extends AnyFreeSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with BeforeAndAfterEach
  with GuiceOneAppPerSuite
  with OptionValues
  with DefaultPlayMongoRepositorySupport[UserAnswers] {

  private val frontendAppConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  override protected val repository: SessionRepository = new SessionRepository(mongoComponent, frontendAppConfig)

  private val userAnswer1 = UserAnswers(DepartureId(0), EoriNumber("EoriNumber1"), Json.obj("foo" -> "bar"))
  private val userAnswer2 = UserAnswers(DepartureId(1), EoriNumber("EoriNumber2"), Json.obj("bar" -> "foo"))

  override def beforeEach(): Unit = {
    super.beforeEach()
    insert(userAnswer1).futureValue
    insert(userAnswer2).futureValue
  }

  "SessionRepository" - {

    "get" - {

      "must return UserAnswers when given a DepartureId" in {

        val result = repository.get(DepartureId(0)).futureValue

        result.value.id mustBe userAnswer1.id
        result.value.eoriNumber mustBe userAnswer1.eoriNumber
        result.value.data mustBe userAnswer1.data
      }

      "must return None when no UserAnswers match DepartureId" in {

        val result = repository.get(DepartureId(3)).futureValue

        result mustBe None
      }
    }

    "set" - {

      "must create new document when given valid UserAnswers" in {

        val userAnswer = UserAnswers(DepartureId(3), EoriNumber("EoriNumber3"), Json.obj("foo" -> "bar"))

        val setResult = repository.set(userAnswer).futureValue

        val getResult = repository.get(DepartureId(3)).futureValue.value

        setResult mustBe true
        getResult.id mustBe userAnswer.id
        getResult.eoriNumber mustBe userAnswer.eoriNumber
        getResult.data mustBe userAnswer.data
      }
    }
  }
}
