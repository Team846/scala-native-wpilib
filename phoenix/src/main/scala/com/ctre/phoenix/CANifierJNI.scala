package com.ctre.phoenix

import com.lynbrookrobotics.scalanativejni._

@jnilib("CTRE_PhoenixCCI")
object CANifierJNI extends CTREJNIWrapper {
  class GeneralPin(val value: Int)
  object GeneralPin {
    val QUAD_IDX: GeneralPin = new GeneralPin(0)

    val QUAD_B: GeneralPin = new GeneralPin(1)

    val QUAD_A: GeneralPin = new GeneralPin(2)

    val LIMR: GeneralPin = new GeneralPin(3)

    val LIMF: GeneralPin = new GeneralPin(4)

    val SDA: GeneralPin = new GeneralPin(5)

    val SCL: GeneralPin = new GeneralPin(6)

    val SPI_CS: GeneralPin = new GeneralPin(7)

    val SPI_MISO_PWM2P: GeneralPin = new GeneralPin(8)

    val SPI_MOSI_PWM1P: GeneralPin = new GeneralPin(9)

    val SPI_CLK_PWM0P: GeneralPin = new GeneralPin(10)
  }
  
  def JNI_new_CANifier(deviceNumber: Int): Long = jni

  def JNI_SetLEDOutput(handle: Long, dutyCycle: Int, ledChannel: Int): Unit = jni

  def JNI_SetGeneralOutputs(handle: Long,
                            outputBits: Int,
                            isOutputBits: Int): Unit = jni

  def JNI_SetGeneralOutput(handle: Long,
                           outputPin: Int,
                           outputValue: Boolean,
                           outputEnable: Boolean): Unit = jni

  def JNI_SetPWMOutput(handle: Long, pwmChannel: Int, dutyCycle: Int): Unit = jni

  def JNI_EnablePWMOutput(handle: Long,
                          pwmChannel: Int,
                          bEnable: Boolean): Unit = jni

  def JNI_GetGeneralInputs(handle: Long, allPins: Array[Boolean]): Unit = jni

  def JNI_GetGeneralInput(handl: Long, inputPin: Int): Boolean = jni

  def JNI_GetPWMInput(handle: Long,
                      pwmChannel: Int,
                      dutyCycleAndPeriod: Array[Double]): Unit = jni

  def JNI_GetLastError(handle: Long): Int = jni

  def JNI_GetBatteryVoltage(handle: Long): Double = jni

  def JNI_ConfigSetCustomParam(handle: Long,
                               newValue: Int,
                               paramIndex: Int,
                               timeoutMs: Int): Int = jni

  def JNI_ConfigGetCustomParam(handle: Long,
                               paramIndex: Int,
                               timoutMs: Int): Int = jni

  def JNI_ConfigSetParameter(handle: Long,
                             param: Int,
                             value: Double,
                             subValue: Int,
                             ordinal: Int,
                             timeoutMs: Int): Int = jni

  def JNI_ConfigGetParameter(handle: Long,
                             param: Int,
                             ordinal: Int,
                             timeoutMs: Int): Double = jni

  def JNI_SetStatusFramePeriod(handle: Long,
                               statusFrame: Int,
                               periodMs: Int,
                               timeoutMs: Int): Int = jni

  def JNI_GetStatusFramePeriod(handle: Long, frame: Int, timeoutMs: Int): Int = jni

  def JNI_SetControlFramePeriod(handle: Long, frame: Int, periodMs: Int): Int = jni

  def JNI_GetFirmwareVersion(handle: Long): Int = jni

  def JNI_HasResetOccurred(handle: Long): Boolean = jni

  def JNI_GetFaults(handle: Long): Int = jni

  def JNI_GetStickyFaults(handle: Long): Int = jni

  def JNI_ClearStickyFaults(handle: Long, timeoutMs: Int): Int = jni

  def JNI_GetBusVoltage(handle: Long): Double = jni
}
