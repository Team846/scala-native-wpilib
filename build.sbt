lazy val macros = project

lazy val wpilib = project.dependsOn(macros)

lazy val example = project.dependsOn(wpilib)

scalaVersion in ThisBuild := "2.11.12"

