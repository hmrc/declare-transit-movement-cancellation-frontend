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

package utils

import controllers.routes
import models.{DepartureId, UserAnswers}
import pages._
import uk.gov.hmrc.viewmodels.SummaryList._
import uk.gov.hmrc.viewmodels._

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def cancellationReason(departureId: DepartureId): Option[Row] = userAnswers.get(CancellationReasonPage(departureId)) map {
    answer =>
      Row(
        key   = Key(msg"cancellationReason.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(lit"$answer"),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.CancellationReasonController.onPageLoad(departureId).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"cancellationReason.checkYourAnswersLabel"))
          )
        )
      )
  }

  def confirmCancellation(departureId: DepartureId): Option[Row] = userAnswers.get(ConfirmCancellationPage(departureId)) map {
    answer =>
      Row(
        key   = Key(msg"confirmCancellation.checkYourAnswersLabel", classes = Seq("govuk-!-width-one-half")),
        value = Value(yesOrNo(answer)),
        actions = List(
          Action(
            content            = msg"site.edit",
            href               = routes.ConfirmCancellationController.onPageLoad(departureId).url,
            visuallyHiddenText = Some(msg"site.edit.hidden".withArgs(msg"confirmCancellation.checkYourAnswersLabel"))
          )
        )
      )
  }

  private def yesOrNo(answer: Boolean): Content =
    if (answer) {
      msg"site.yes"
    } else {
      msg"site.no"
    }
}
