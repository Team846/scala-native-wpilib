/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

//import edu.wpi.first.wpilibj.PWMConfigDataResult

@SuppressWarnings(Array("AbbreviationAsWordInName"))
@jnilib("wpilibJavaJNI")
object PWMJNI {
  def initializePWMPort(halPortHandle: Int): Int = jni

  def checkPWMChannel(channel: Int): Boolean = jni

  def freePWMPort(pwmPortHandle: Int): Unit = jni

  def setPWMConfigRaw(pwmPortHandle: Int, maxPwm: Int, deadbandMaxPwm: Int, centerPwm: Int, deadbandMinPwm: Int, minPwm: Int): Unit = jni

  def setPWMConfig(pwmPortHandle: Int, maxPwm: Double, deadbandMaxPwm: Double, centerPwm: Double, deadbandMinPwm: Double, minPwm: Double): Unit = jni

  //def getPWMConfigRaw(pwmPortHandle: Int): PWMConfigDataResult = jni

  def setPWMEliminateDeadband(pwmPortHandle: Int, eliminateDeadband: Boolean): Unit = jni

  def getPWMEliminateDeadband(pwmPortHandle: Int): Boolean = jni

  def setPWMRaw(pwmPortHandle: Int, value: Short): Unit = jni

  def setPWMSpeed(pwmPortHandle: Int, speed: Double): Unit = jni

  def setPWMPosition(pwmPortHandle: Int, position: Double): Unit = jni

  def getPWMRaw(pwmPortHandle: Int): Short = jni

  def getPWMSpeed(pwmPortHandle: Int): Double = jni

  def getPWMPosition(pwmPortHandle: Int): Double = jni

  def setPWMDisabled(pwmPortHandle: Int): Unit = jni

  def latchPWMZero(pwmPortHandle: Int): Unit = jni

  def setPWMPeriodScale(pwmPortHandle: Int, squelchMask: Int): Unit = jni
}
