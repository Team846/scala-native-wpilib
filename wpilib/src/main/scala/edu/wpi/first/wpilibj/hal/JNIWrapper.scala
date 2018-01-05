package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJavaJNI")
class JNIWrapper {
  // from JNIWrapper
  def getPort(channel: Byte): Int = jni
}
