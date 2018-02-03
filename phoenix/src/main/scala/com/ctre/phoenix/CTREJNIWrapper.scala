package com.ctre.phoenix

import scala.scalanative.native._
import com.lynbrookrobotics.scalanativejni._
import edu.wpi.first.wpilibj.{AccumulatorResult, PWMConfigDataResult}
import edu.wpi.first.wpilibj.can._
import edu.wpi.first.wpilibj.hal.MatchInfoData
import edu.wpi.first.wpilibj.util.{AllocationException, BoundaryException, HalHandleException, UncleanStatusException}

class CTREJNIWrapper {
  if (!CTREJNIWrapper.libraryLoaded) {
    loadJNILibrary(c"libCTRE_PhoenixCCI.so")
    CTREJNIWrapper.libraryLoaded = true
  }

  def getPortWithModule(module: Byte, channel: Byte): Int = CTREJNIWrapper.getPortWithModule(module, channel)

  def getPort(channel: Byte): Int = CTREJNIWrapper.getPort(channel)
}

@jnilib("CTRE_PhoenixCCI")
object CTREJNIWrapper {
  // HALUtil exceptions
  registerClass(autoClass[RuntimeException])
  registerClass(autoClass[IllegalArgumentException])
  registerClass(autoClass[BoundaryException])
  registerClass(autoClass[AllocationException])
  registerClass(autoClass[HalHandleException])
  registerClass(autoClass[CANInvalidBufferException])
  registerClass(autoClass[CANMessageNotFoundException])
  registerClass(autoClass[CANMessageNotAllowedException])
  registerClass(autoClass[CANNotInitializedException])
  registerClass(autoClass[UncleanStatusException])

  registerClass(autoClass[CANStatus]) // CAN
  registerClass(autoClass[MatchInfoData]) // HAL
  registerClass(autoClass[PWMConfigDataResult]) // PWM
  registerClass(autoClass[AccumulatorResult]) // Analog

  registerClass(autoClass[StackTraceElement])

  var libraryLoaded = false

  def getPortWithModule(module: Byte, channel: Byte): Int = jni

  def getPort(channel: Byte): Int = jni
}
