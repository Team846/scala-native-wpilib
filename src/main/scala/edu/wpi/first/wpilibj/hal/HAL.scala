package edu.wpi.first.wpilibj.hal

import edu.wpi.first.wpilibj.JString

import scala.scalanative.native._

@extern @link("wpilibJavaJNI")
object HAL {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: Ptr[Unit], reserved: Ptr[Unit]): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_waitForDSData")
  def waitForDSData(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_initialize")
  def initialize(env: Ptr[Unit], cls: Ptr[Unit], mode: Int): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramStarting")
  def observeUserProgramStarting(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramDisabled")
  def observeUserProgramDisabled(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramAutonomous")
  def observeUserProgramAutonomous(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramTeleop")
  def observeUserProgramTeleop(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramTest")
  def observeUserProgramTest(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_report")
  def report(env: Ptr[Unit], cls: Ptr[Unit], resource: Int, instanceNumber: Int, context: Int, feature: JString): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_getJoystickIsXbox")
  def getJoystickIsXbox(env: Ptr[Unit], cls: Ptr[Unit], joystickNum: Byte): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_getJoystickType")
  def getJoystickType(env: Ptr[Unit], cls: Ptr[Unit], joystickNum: Byte): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_getJoystickName")
  def getJoystickName(env: Ptr[Unit], cls: Ptr[Unit], joystickNum: Byte): JString = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_getJoystickAxisType")
  def getJoystickAxisType(env: Ptr[Unit], cls: Ptr[Unit], joystickNum: Byte, axis: Byte): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_sendError")
  def sendError(env: Ptr[Unit], cls: Ptr[Unit], isError: Boolean, errorCode: Int, isLVCode: Boolean, details: JString, location: JString, callStack: JString, printMsg: Boolean): Int = extern
}

object HALStatics {
  val kMaxJoystickAxes = 12
  val kMaxJoystickPOVs = 12
}
