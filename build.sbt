organization in ThisBuild := "com.lynbrookrobotics"

scalaVersion in ThisBuild := "2.11.12"

publishMavenStyle in ThisBuild := true
publishTo in ThisBuild := Some(Resolver.file("gh-pages-repo", baseDirectory.value / "repo"))

resolvers in ThisBuild += "Funky-Repo" at "http://lynbrookrobotics.com/repo"

lazy val scalaNativeWpilib = project.in(file("."))
  .aggregate(
    scalaNativeJNINativeLib,
    scalaNativeJNI,
    wpilib,
    ntcore,
    phoenix
  ).settings(
  publish := {},
  publishLocal := {}
)

lazy val scalaNativeJNINativeLib = project

lazy val scalaNativeJNI = project.dependsOn(scalaNativeJNINativeLib).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val ntcore = project.dependsOn(scalaNativeJNI).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val wpilib = project.dependsOn(scalaNativeJNI, ntcore).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val phoenix = project.dependsOn(scalaNativeJNI, wpilib).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val example = project.dependsOn(wpilib, ntcore, phoenix).settings(
  unmanagedJars in Compile += (Keys.`package` in Compile in scalaNativeJNINativeLib).value
)
