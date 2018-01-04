/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import edu.wpi.first.wpilibj.JString

import scala.scalanative.native.{Ptr, extern, link, name}

@extern @link("wpilibJavaJNI")
object HALUtil {
//  final val NULL_PARAMETER = -1005
//  final val SAMPLE_RATE_TOO_HIGH = 1001
//  final val VOLTAGE_OUT_OF_RANGE = 1002
//  final val LOOP_TIMING_ERROR = 1004
//  final val INCOMPATIBLE_STATE = 1015
//  final val ANALOG_TRIGGER_PULSE_OUTPUT_ERROR = -1011
//  final val NO_AVAILABLE_RESOURCES = -104
//  final val PARAMETER_OUT_OF_RANGE = -1028

  @name("Java_edu_wpi_first_wpilibj_hal_HALUtil_getHALErrorMessage")
  def getHALErrorMessage(env: Ptr[Unit], cls: Ptr[Unit], code: Int): JString = extern
}
