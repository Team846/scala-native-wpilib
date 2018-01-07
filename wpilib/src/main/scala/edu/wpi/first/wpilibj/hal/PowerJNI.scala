/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object PowerJNI extends JNIWrapper {
  def getVinVoltage: Double = jni

  def getVinCurrent: Double = jni

  def getUserVoltage6V: Double = jni

  def getUserCurrent6V: Double = jni

  def getUserActive6V: Boolean = jni

  def getUserCurrentFaults6V: Int = jni

  def getUserVoltage5V: Double = jni

  def getUserCurrent5V: Double = jni

  def getUserActive5V: Boolean = jni

  def getUserCurrentFaults5V: Int = jni

  def getUserVoltage3V3: Double = jni

  def getUserCurrent3V3: Double = jni

  def getUserActive3V3: Boolean = jni

  def getUserCurrentFaults3V3: Int = jni
}
