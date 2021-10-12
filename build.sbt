import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "declare-transit-movement-cancellation-frontend"
val silencerVersion = "1.7.0"

lazy val root = (project in file("."))
  .enablePlugins(
    PlayScala,
    SbtAutoBuildPlugin,
    SbtDistributablesPlugin
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(itSettings): _*)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(inConfig(Test)(testSettings): _*)
  .settings(majorVersion := 0)
  .settings(scalaVersion := "2.12.12")
  .settings(headerSettings(IntegrationTest): _*)
  .settings(automateHeaderSettings(IntegrationTest))
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.play.views.html.helpers._",
      "uk.gov.hmrc.play.views.html.layouts._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 9489,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*handlers.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting  := true,
    useSuperShell in ThisBuild          := false,
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds",
      "-Ypartial-unification"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged                        := true,
    evictionWarningOptions in update :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    Concat.groups := Seq(
      "javascripts/application.js" -> group(
        Seq("lib/govuk-frontend/govuk/all.js", "lib/hmrc-frontend/hmrc/all.js", "javascripts/ctc.js")
      )
    ),
    uglifyCompressOptions          := Seq("unused=false", "dead_code=false", "warnings=false"),
    pipelineStages in Assets       := Seq(concat, uglify),
    useSuperShell in ThisBuild     := false
  )
  .settings(
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=routes",
    libraryDependencies ++= Seq(
      compilerPlugin(
        "com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full
      ),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork := true,
  unmanagedResourceDirectories ++= Seq(
    baseDirectory.value / "test" / "resources"
  ),
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf",
    "-Dconfig.logger=logback-test.xml"
  )
)

lazy val itSettings = Defaults.itSettings ++ Seq(
  unmanagedSourceDirectories := Seq(
    baseDirectory.value / "it"
  ),
  unmanagedResourceDirectories += baseDirectory.value / "it" / "resources",
  parallelExecution := false,
  fork              := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=it.application.conf",
    "-Dlogger.resource=logback-it.xml"
  )
)
