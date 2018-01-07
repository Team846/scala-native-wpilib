/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object PortsJNI extends JNIWrapper {
  def getNumAccumulators: Int = jni

  def getNumAnalogTriggers: Int = jni

  def getNumAnalogInputs: Int = jni

  def getNumAnalogOutputs: Int = jni

  def getNumCounters: Int = jni

  def getNumDigitalHeaders: Int = jni

  def getNumPWMHeaders: Int = jni

  def getNumDigitalChannels: Int = jni

  def getNumPWMChannels: Int = jni

  def getNumDigitalPWMOutputs: Int = jni

  def getNumEncoders: Int = jni

  def getNumInterrupts: Int = jni

  def getNumRelayChannels: Int = jni

  def getNumRelayHeaders: Int = jni

  def getNumPCMModules: Int = jni

  def getNumSolenoidChannels: Int = jni

  def getNumPDPModules: Int = jni

  def getNumPDPChannels: Int = jni
}
