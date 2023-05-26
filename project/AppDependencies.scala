import sbt._
import play.core.PlayVersion

object AppDependencies {

  private val bootstrapVersion = "7.12.0"
  private val mongoVersion = "0.74.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo"    %% "hmrc-mongo-play-28"            % mongoVersion,
    "uk.gov.hmrc"          %% "play-conditional-form-mapping" % "1.12.0-play-28",
    "uk.gov.hmrc"          %% "bootstrap-frontend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc"          %% "play-language"                 % "6.1.0-play-28",
    "uk.gov.hmrc"          %% "play-nunjucks"                 % "0.43.0-play-28",
    "uk.gov.hmrc"          %% "play-nunjucks-viewmodel"       % "0.18.0-play-28",
    "org.webjars.npm"      %  "govuk-frontend"                % "4.6.0",
    "uk.gov.hmrc.webjars"  %  "hmrc-frontend"                 % "5.34.0",
    "com.lucidchart"       %% "xtract"                        % "2.2.1"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-28"  % mongoVersion,
    "org.scalatest"               %% "scalatest"                % "3.2.15",
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.jsoup"                   %  "jsoup"                    % "1.15.3",
    "com.typesafe.play"           %% "play-test"                % PlayVersion.current,
    "io.github.wolfendale"        %% "scalacheck-gen-regexp"    % "1.1.0",
    "org.mockito"                 %  "mockito-core"             % "4.11.0",
    "org.scalatestplus"           %% "mockito-4-6"              % "3.2.15.0",
    "org.scalacheck"              %% "scalacheck"               % "1.17.0",
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.15.0",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.27.2",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
