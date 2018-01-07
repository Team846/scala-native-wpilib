package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._
import edu.wpi.first.wpilibj.{AccumulatorResult, PWMConfigDataResult}
import edu.wpi.first.wpilibj.can._
import edu.wpi.first.wpilibj.util.{AllocationException, BoundaryException, HalHandleException, UncleanStatusException}

@jnilib("wpilibJNI")
class JNIWrapper {
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

  if (!JNIWrapper.hasLoaded) {
    JNILoad.JNI_OnLoad(vm, null)
    JNIWrapper.hasLoaded = true
  }

  // from JNIWrapper
  def getPort(channel: Byte): Int = jni
}

object JNIWrapper {
  private[JNIWrapper] var hasLoaded = false
}
