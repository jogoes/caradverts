name := """caradverts"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val jOOQVersion = "3.8.5"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  evolutions,
  filters,
  "org.jooq" % "jooq" % jOOQVersion,
  "org.jooq" % "jooq-codegen-maven" % jOOQVersion,
  "org.jooq" % "jooq-meta" % jOOQVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.mockito" % "mockito-core" % "2.2.7" % Test
)

val generateJOOQ = taskKey[Seq[File]]("Generate jOOQ classes")

val generateJOOQTask = (baseDirectory, dependencyClasspath in Compile, runner in Compile, streams) map {
  (base, cp, r, s) =>
    toError(r.run(
      "org.jooq.util.GenerationTool",
      cp.files,
      Array("conf/jooq/configuration.xml"),
      s.log))
    ((base / "app" / "generated") ** "*.scala").get
}

generateJOOQ <<= generateJOOQTask