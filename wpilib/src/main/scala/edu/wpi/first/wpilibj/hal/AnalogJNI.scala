/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import edu.wpi.first.wpilibj.AccumulatorResult
import java.nio.IntBuffer

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object AnalogJNI extends JNIWrapper {
  /**
    * <i>native declaration : AthenaJava\target\native\include\HAL\Analog.h:58</i><br> enum values
    */
  object AnalogTriggerType {
    /**
      * <i>native declaration : AthenaJava\target\native\include\HAL\Analog.h:54</i>
      */
    val kInWindow = 0
    /**
      * <i>native declaration : AthenaJava\target\native\include\HAL\Analog.h:55</i>
      */
    val kState = 1
    /**
      * <i>native declaration : AthenaJava\target\native\include\HAL\Analog.h:56</i>
      */
    val kRisingPulse = 2
    /**
      * <i>native declaration : AthenaJava\target\native\include\HAL\Analog.h:57</i>
      */
    val kFallingPulse = 3
  }

  def initializeAnalogInputPort(halPortHandle: Int): Int = jni

  def freeAnalogInputPort(portHandle: Int): Unit = jni

  def initializeAnalogOutputPort(halPortHandle: Int): Int = jni

  def freeAnalogOutputPort(portHandle: Int): Unit = jni

  def checkAnalogModule(module: Byte): Boolean = jni

  def checkAnalogInputChannel(channel: Int): Boolean = jni

  def checkAnalogOutputChannel(channel: Int): Boolean = jni

  def setAnalogOutput(portHandle: Int, voltage: Double): Unit = jni

  def getAnalogOutput(portHandle: Int): Double = jni

  def setAnalogSampleRate(samplesPerSecond: Double): Unit = jni

  def getAnalogSampleRate: Double = jni

  def setAnalogAverageBits(analogPortHandle: Int, bits: Int): Unit = jni

  def getAnalogAverageBits(analogPortHandle: Int): Int = jni

  def setAnalogOversampleBits(analogPortHandle: Int, bits: Int): Unit = jni

  def getAnalogOversampleBits(analogPortHandle: Int): Int = jni

  def getAnalogValue(analogPortHandle: Int): Short = jni

  def getAnalogAverageValue(analogPortHandle: Int): Int = jni

  def getAnalogVoltsToValue(analogPortHandle: Int, voltage: Double): Int = jni

  def getAnalogVoltage(analogPortHandle: Int): Double = jni

  def getAnalogAverageVoltage(analogPortHandle: Int): Double = jni

  def getAnalogLSBWeight(analogPortHandle: Int): Int = jni

  def getAnalogOffset(analogPortHandle: Int): Int = jni

  def isAccumulatorChannel(analogPortHandle: Int): Boolean = jni

  def initAccumulator(analogPortHandle: Int): Unit = jni

  def resetAccumulator(analogPortHandle: Int): Unit = jni

  def setAccumulatorCenter(analogPortHandle: Int, center: Int): Unit = jni

  def setAccumulatorDeadband(analogPortHandle: Int, deadband: Int): Unit = jni

  def getAccumulatorValue(analogPortHandle: Int): Long = jni

  def getAccumulatorCount(analogPortHandle: Int): Int = jni

  def getAccumulatorOutput(analogPortHandle: Int, result: AccumulatorResult): Unit = jni

  def initializeAnalogTrigger(analogInputHandle: Int, index: IntBuffer): Int = jni

  def cleanAnalogTrigger(analogTriggerHandle: Int): Unit = jni

  def setAnalogTriggerLimitsRaw(analogTriggerHandle: Int, lower: Int, upper: Int): Unit = jni

  def setAnalogTriggerLimitsVoltage(analogTriggerHandle: Int, lower: Double, upper: Double): Unit = jni

  def setAnalogTriggerAveraged(analogTriggerHandle: Int, useAveragedValue: Boolean): Unit = jni

  def setAnalogTriggerFiltered(analogTriggerHandle: Int, useFilteredValue: Boolean): Unit = jni

  def getAnalogTriggerInWindow(analogTriggerHandle: Int): Boolean = jni

  def getAnalogTriggerTriggerState(analogTriggerHandle: Int): Boolean = jni

  def getAnalogTriggerOutput(analogTriggerHandle: Int, `type`: Int): Boolean = jni
}
