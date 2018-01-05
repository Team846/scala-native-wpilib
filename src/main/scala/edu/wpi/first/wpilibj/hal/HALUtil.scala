/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

import scala.scalanative.native.{Ptr, extern, link, name}

@jnilib("wpilibJavaJNI")
object HALUtil {
  JNILoad.JNI_OnLoad(vm, null)

  val NULL_PARAMETER: Int = -1005
  val SAMPLE_RATE_TOO_HIGH = 1001
  val VOLTAGE_OUT_OF_RANGE = 1002
  val LOOP_TIMING_ERROR = 1004
  val INCOMPATIBLE_STATE = 1015
  val ANALOG_TRIGGER_PULSE_OUTPUT_ERROR: Int = -1011
  val NO_AVAILABLE_RESOURCES: Int = -104
  val PARAMETER_OUT_OF_RANGE: Int = -1028

  def getFPGAVersion: Short = jni

  def getFPGARevision: Int = jni

  def getFPGATime: Long = jni

  def getHALRuntimeType: Int = jni

  def getFPGAButton: Boolean = jni

  def getHALErrorMessage(code: Int): String = jni

  def getHALErrno: Int = jni

  def getHALstrerror(errno: Int): String = jni

  def getHALstrerror: String = getHALstrerror(getHALErrno)
}

@extern @link("wpilibJavaJNI")
object JNILoad {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: VM, reserved: Ptr[Unit]): Int = extern
}
