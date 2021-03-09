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

package models

import play.api.libs.json._
import play.api.mvc.PathBindable

final case class LocalReferenceNumber(value: String) {
  override def toString: String = value
}

object LocalReferenceNumber {

  val maxLength: Int    = 22
  implicit val reads: Reads[LocalReferenceNumber] = Json.reads[LocalReferenceNumber]

  implicit def writes: Writes[LocalReferenceNumber] = Writes {
    lrn =>
      JsString(lrn.value)
  }

  implicit lazy val pathBindable: PathBindable[LocalReferenceNumber] = new PathBindable[LocalReferenceNumber] {
    override def bind(key: String, value: String): Either[String, LocalReferenceNumber] =
      implicitly[PathBindable[String]].bind(key, value).right.map(LocalReferenceNumber(_))

    override def unbind(key: String, lrn: LocalReferenceNumber): String =
      lrn.value
  }
}
