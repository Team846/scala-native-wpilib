/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object SerialPortJNI extends JNIWrapper {
  def serialInitializePort(port: Byte): Unit = jni

  def serialSetBaudRate(port: Byte, baud: Int): Unit = jni

  def serialSetDataBits(port: Byte, bits: Byte): Unit = jni

  def serialSetParity(port: Byte, parity: Byte): Unit = jni

  def serialSetStopBits(port: Byte, stopBits: Byte): Unit = jni

  def serialSetWriteMode(port: Byte, mode: Byte): Unit = jni

  def serialSetFlowControl(port: Byte, flow: Byte): Unit = jni

  def serialSetTimeout(port: Byte, timeout: Double): Unit = jni

  def serialEnableTermination(port: Byte, terminator: Char): Unit = jni

  def serialDisableTermination(port: Byte): Unit = jni

  def serialSetReadBufferSize(port: Byte, size: Int): Unit = jni

  def serialSetWriteBufferSize(port: Byte, size: Int): Unit = jni

  def serialGetBytesReceived(port: Byte): Int = jni

  def serialRead(port: Byte, buffer: Array[Byte], count: Int): Int = jni

  def serialWrite(port: Byte, buffer: Array[Byte], count: Int): Int = jni

  def serialFlush(port: Byte): Unit = jni

  def serialClear(port: Byte): Unit = jni

  def serialClose(port: Byte): Unit = jni
}
