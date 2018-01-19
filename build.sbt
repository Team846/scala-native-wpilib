organization in ThisBuild := "com.lynbrookrobotics"

scalaVersion in ThisBuild := "2.11.12"

lazy val scalaNativeJNINativeLib = project

lazy val scalaNativeJNI = project.dependsOn(scalaNativeJNINativeLib).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val wpilib = project.dependsOn(scalaNativeJNI).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val phoenix = project.dependsOn(scalaNativeJNI, wpilib).settings(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val example = project.dependsOn(wpilib, phoenix).settings(
  unmanagedJars in Compile += (Keys.`package` in Compile in scalaNativeJNINativeLib).value
)
