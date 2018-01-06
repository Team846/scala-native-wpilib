organization in ThisBuild := "com.lynbrookrobotics"

scalaVersion in ThisBuild := "2.11.12"

lazy val scalaNativeJNINativeLib = project

lazy val scalaNativeJNI = project.dependsOn(scalaNativeJNINativeLib)

lazy val wpilib = project.dependsOn(scalaNativeJNI)

lazy val example = project.dependsOn(wpilib).settings(
  unmanagedJars in Compile += (Keys.`package` in Compile in scalaNativeJNINativeLib).value
)
