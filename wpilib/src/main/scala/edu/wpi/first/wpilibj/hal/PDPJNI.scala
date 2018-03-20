/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object PDPJNI extends JNIWrapper {
  def initializePDP(module: Int): Unit = jni

  def checkPDPModule(module: Int): Boolean = jni

  def checkPDPChannel(channel: Int): Boolean = jni

  def getPDPTemperature(module: Int): Double = jni

  def getPDPVoltage(module: Int): Double = jni

  def getPDPChannelCurrent(channel: Byte, module: Int): Double = jni

  def getPDPTotalCurrent(module: Int): Double = jni

  def getPDPTotalPower(module: Int): Double = jni

  def getPDPTotalEnergy(module: Int): Double = jni

  def resetPDPTotalEnergy(module: Int): Unit = jni

  def clearPDPStickyFaults(module: Int): Unit = jni
}
