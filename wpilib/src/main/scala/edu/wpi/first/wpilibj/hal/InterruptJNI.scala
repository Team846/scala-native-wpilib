/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object InterruptJNI extends JNIWrapper {
  val HalInvalidHandle = 0

  trait InterruptJNIHandlerFunction {
    def apply(interruptAssertedMask: Int, param: Any): Unit
  }

  def initializeInterrupts(watcher: Boolean): Int = jni

  def cleanInterrupts(interruptHandle: Int): Unit = jni

  def waitForInterrupt(interruptHandle: Int, timeout: Double, ignorePrevious: Boolean): Int = jni

  def enableInterrupts(interruptHandle: Int): Unit = jni

  def disableInterrupts(interruptHandle: Int): Unit = jni

  def readInterruptRisingTimestamp(interruptHandle: Int): Double = jni

  def readInterruptFallingTimestamp(interruptHandle: Int): Double = jni

  def requestInterrupts(interruptHandle: Int, digitalSourceHandle: Int, analogTriggerType: Int): Unit = jni

  def attachInterruptHandler(interruptHandle: Int, handler: Object, param: Object): Unit = jni

  def attachInterruptHandler(interruptHandle: Int, handler: InterruptJNIHandlerFunction, param: Object): Unit = attachInterruptHandler(interruptHandle, handler, param)

  def setInterruptUpSourceEdge(interruptHandle: Int, risingEdge: Boolean, fallingEdge: Boolean): Unit = jni
}
