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

package views

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.implicits.RichRadiosSupport
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.Radios

object ViewUtils {

  implicit class RadioImplicits(radios: Radios)(implicit messages: Messages) extends RichRadiosSupport {

    def withHeadingAndCaption(heading: String, caption: Option[String]): Radios = {

      caption match {
        case Some(value) => radios.withHeadingAndSectionCaption(Text(heading), Text(value))
        case None => radios.withHeading(Text(heading))
      }
    }

  }

}
