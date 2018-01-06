package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJavaJNI")
class JNIWrapper {
  registerClass(autoClass[RuntimeException])

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
