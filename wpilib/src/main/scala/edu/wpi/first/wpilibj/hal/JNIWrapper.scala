package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._
import edu.wpi.first.wpilibj.{AccumulatorResult, PWMConfigDataResult}
import edu.wpi.first.wpilibj.can._
import edu.wpi.first.wpilibj.util.{AllocationException, BoundaryException, HalHandleException, UncleanStatusException}

import scala.scalanative.native._

class JNIWrapper {
  if (!JNIWrapper.hasLoaded) {
    loadJNILibrary(c"libwpilibJNI.so")
    JNIWrapper.hasLoaded = true
  }

  def getPort(channel: Byte): Int = JNIWrapper.getPort(channel)
  def getPortWithModule(module: Byte, channel: Byte): Int = JNIWrapper.getPortWithModule(module, channel)
}

@jnilib("wpilibJNI")
object JNIWrapper {
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

  def getPortWithModule(module: Byte, channel: Byte): Int = jni
  def getPort(channel: Byte): Int = jni

  private[JNIWrapper] var hasLoaded = false
}
