/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import java.nio.IntBuffer

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object CounterJNI extends JNIWrapper {
  def initializeCounter(mode: Int, index: IntBuffer): Int = jni

  def freeCounter(counterHandle: Int): Unit = jni

  def setCounterAverageSize(counterHandle: Int, size: Int): Unit = jni

  def setCounterUpSource(counterHandle: Int, digitalSourceHandle: Int, analogTriggerType: Int): Unit = jni

  def setCounterUpSourceEdge(counterHandle: Int, risingEdge: Boolean, fallingEdge: Boolean): Unit = jni

  def clearCounterUpSource(counterHandle: Int): Unit = jni

  def setCounterDownSource(counterHandle: Int, digitalSourceHandle: Int, analogTriggerType: Int): Unit = jni

  def setCounterDownSourceEdge(counterHandle: Int, risingEdge: Boolean, fallingEdge: Boolean): Unit = jni

  def clearCounterDownSource(counterHandle: Int): Unit = jni

  def setCounterUpDownMode(counterHandle: Int): Unit = jni

  def setCounterExternalDirectionMode(counterHandle: Int): Unit = jni

  def setCounterSemiPeriodMode(counterHandle: Int, highSemiPeriod: Boolean): Unit = jni

  def setCounterPulseLengthMode(counterHandle: Int, threshold: Double): Unit = jni

  def getCounterSamplesToAverage(counterHandle: Int): Int = jni

  def setCounterSamplesToAverage(counterHandle: Int, samplesToAverage: Int): Unit = jni

  def resetCounter(counterHandle: Int): Unit = jni

  def getCounter(counterHandle: Int): Int = jni

  def getCounterPeriod(counterHandle: Int): Double = jni

  def setCounterMaxPeriod(counterHandle: Int, maxPeriod: Double): Unit = jni

  def setCounterUpdateWhenEmpty(counterHandle: Int, enabled: Boolean): Unit = jni

  def getCounterStopped(counterHandle: Int): Boolean = jni

  def getCounterDirection(counterHandle: Int): Boolean = jni

  def setCounterReverseDirection(counterHandle: Int, reverseDirection: Boolean): Unit = jni
}
