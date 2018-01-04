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
//  final val NULL_PARAMETER = -1005
//  final val SAMPLE_RATE_TOO_HIGH = 1001
//  final val VOLTAGE_OUT_OF_RANGE = 1002
//  final val LOOP_TIMING_ERROR = 1004
//  final val INCOMPATIBLE_STATE = 1015
//  final val ANALOG_TRIGGER_PULSE_OUTPUT_ERROR = -1011
//  final val NO_AVAILABLE_RESOURCES = -104
//  final val PARAMETER_OUT_OF_RANGE = -1028

//  @scala.scalanative.native.extern() @scala.scalanative.native.link("wpilibJavaJNI") object linker {
//    @scala.scalanative.native.name("Java_edu_wpi_first_wpilibj_hal_HALUtil_getHALErrorMessage")
//    def native(env: com.lynbrookrobotics.scalanativejni.Env, cls: com.lynbrookrobotics.scalanativejni.Cls, code: Int): com.lynbrookrobotics.scalanativejni.JString = scala.scalanative.native.extern
//  }


  def getHALErrorMessage(code: Int): String = jni
}

@extern @link("wpilibJavaJNI")
object JNILoad {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: VM, reserved: Ptr[Unit]): Int = extern
}
