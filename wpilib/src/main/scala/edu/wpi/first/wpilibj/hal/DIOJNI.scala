/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@SuppressWarnings(Array("AbbreviationAsWordInName"))
@jnilib("wpilibJNI")
object DIOJNI extends JNIWrapper {
  def initializeDIOPort(halPortHandle: Int, input: Boolean): Int = jni

  def checkDIOChannel(channel: Int): Boolean = jni

  def freeDIOPort(dioPortHandle: Int): Unit = jni

  def setDIO(dioPortHandle: Int, value: Short): Unit = jni

  def getDIO(dioPortHandle: Int): Boolean = jni

  def getDIODirection(dioPortHandle: Int): Boolean = jni

  def pulse(dioPortHandle: Int, pulseLength: Double): Unit = jni

  def isPulsing(dioPortHandle: Int): Boolean = jni

  def isAnyPulsing: Boolean = jni

  def getLoopTiming: Short = jni

  def allocateDigitalPWM: Int = jni

  def freeDigitalPWM(pwmGenerator: Int): Unit = jni

  def setDigitalPWMRate(rate: Double): Unit = jni

  def setDigitalPWMDutyCycle(pwmGenerator: Int, dutyCycle: Double): Unit = jni

  def setDigitalPWMOutputChannel(pwmGenerator: Int, channel: Int): Unit = jni
}

