package edu.wpi.first.wpilibj.hal

import scala.scalanative.native._

@extern @link("wpilibJavaJNI")
object HAL {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: Ptr[Unit], reserved: Ptr[Unit]): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_initialize")
  def initialize(env: Ptr[Unit], cls: Ptr[Unit], mode: Int): Int = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramStarting")
  def observeUserProgramStarting(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_observeUserProgramDisabled")
  def observeUserProgramDisabled(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern

  @name("Java_edu_wpi_first_wpilibj_hal_HAL_waitForDSData")
  def waitForDSData(env: Ptr[Unit], cls: Ptr[Unit]): Unit = extern
}
