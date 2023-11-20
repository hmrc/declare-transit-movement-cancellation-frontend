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

package models

import base.SpecBase
import play.api.libs.json.Json

import java.time.Instant

class UserAnswersSpec extends SpecBase {

  private val epochMilli = 946684800000L

  "must read new date format" in {

    val json = Json.parse(s"""
        |{
        |    "_id" : ${departureId.index},
        |    "eoriNumber" : "${eoriNumber.value}",
        |    "data" : {},
        |    "lastUpdated" : {
        |        "$$date" : {
        |            "$$numberLong" : "$epochMilli"
        |        }
        |    }
        |}""".stripMargin)

    val result = json.as[UserAnswers]

    result mustBe UserAnswers(departureId, eoriNumber, Json.obj(), Instant.ofEpochMilli(epochMilli))
  }

}
