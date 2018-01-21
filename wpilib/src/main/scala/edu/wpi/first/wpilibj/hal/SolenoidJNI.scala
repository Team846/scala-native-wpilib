/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object SolenoidJNI extends JNIWrapper {
  def initializeSolenoidPort(halPortHandle: Int): Int = jni

  def checkSolenoidModule(module: Int): Boolean = jni

  def checkSolenoidChannel(channel: Int): Boolean = jni

  def freeSolenoidPort(portHandle: Int): Unit = jni

  def setSolenoid(portHandle: Int, on: Boolean): Unit = jni

  def getSolenoid(portHandle: Int): Boolean = jni

  def getAllSolenoids(module: Int): Int = jni

  def getPCMSolenoidBlackList(module: Int): Int = jni

  def getPCMSolenoidVoltageStickyFault(module: Int): Boolean = jni

  def getPCMSolenoidVoltageFault(module: Int): Boolean = jni

  def clearAllPCMStickyFaults(module: Int): Unit = jni

  def setOneShotDuration(portHandle: Int, durationMS: Long): Unit = jni

  def fireOneShot(portHandle: Int): Unit = jni
}
