/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import java.nio.ByteBuffer

import com.lynbrookrobotics.scalanativejni._

@SuppressWarnings(Array("AbbreviationAsWordInName"))
@jnilib("wpilibJNI")
object SPIJNI extends JNIWrapper {
  def spiInitialize(port: Int): Unit = jni

  def spiTransaction(port: Int, dataToSend: ByteBuffer, dataReceived: ByteBuffer, size: Byte): Int = jni

  def spiTransactionB(port: Int, dataToSend: Array[Byte], dataReceived: Array[Byte], size: Byte): Int = jni

  def spiWrite(port: Int, dataToSend: ByteBuffer, sendSize: Byte): Int = jni

  def spiWriteB(port: Int, dataToSend: Array[Byte], sendSize: Byte): Int = jni

  def spiRead(port: Int, initiate: Boolean, dataReceived: ByteBuffer, size: Byte): Int = jni

  def spiReadB(port: Int, initiate: Boolean, dataReceived: Array[Byte], size: Byte): Int = jni

  def spiClose(port: Int): Unit = jni

  def spiSetSpeed(port: Int, speed: Int): Unit = jni

  def spiSetOpts(port: Int, msbFirst: Int, sampleOnTrailing: Int, clkIdleHigh: Int): Unit = jni

  def spiSetChipSelectActiveHigh(port: Int): Unit = jni

  def spiSetChipSelectActiveLow(port: Int): Unit = jni

  def spiInitAuto(port: Int, bufferSize: Int): Unit = jni

  def spiFreeAuto(port: Int): Unit = jni

  def spiStartAutoRate(port: Int, period: Double): Unit = jni

  def spiStartAutoTrigger(port: Int, digitalSourceHandle: Int, analogTriggerType: Int, triggerRising: Boolean, triggerFalling: Boolean): Unit = jni

  def spiStopAuto(port: Int): Unit = jni

  def spiSetAutoTransmitData(port: Int, dataToSend: Array[Byte], zeroSize: Int): Unit = jni

  def spiForceAutoRead(port: Int): Unit = jni

  def spiReadAutoReceivedData__ILjava_nio_ByteBuffer_2ID(port: Int, buffer: ByteBuffer, numToRead: Int, timeout: Double): Int = jni

  def spiReadAutoReceivedData__I_3BID(port: Int, buffer: Array[Byte], numToRead: Int, timeout: Double): Int = jni

  def spiReadAutoReceivedData(port: Int, buffer: ByteBuffer, numToRead: Int, timeout: Double): Int = spiReadAutoReceivedData__ILjava_nio_ByteBuffer_2ID(port, buffer, numToRead, timeout)
  def spiReadAutoReceivedData(port: Int, buffer: Array[Byte], numToRead: Int, timeout: Double): Int = spiReadAutoReceivedData__I_3BID(port, buffer, numToRead, timeout)

  def spiGetAutoDroppedCount(port: Int): Int = jni
}
