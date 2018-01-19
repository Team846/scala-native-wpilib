package com.ctre.phoenix

import scala.scalanative.native._
import com.lynbrookrobotics.scalanativejni._

class CTREJNIWrapper {
  if (!CTREJNIWrapper.libraryLoaded) {
    JNILoad.JNI_OnLoad(vm, null)
    CTREJNIWrapper.libraryLoaded = true
  }

  def getPortWithModule(module: Byte, channel: Byte): Int = CTREJNIWrapper.getPortWithModule(module, channel)

  def getPort(channel: Byte): Int = CTREJNIWrapper.getPort(channel)
}

@jnilib("CTRE_PhoenixCCI")
object CTREJNIWrapper {
  var libraryLoaded = false

  def getPortWithModule(module: Byte, channel: Byte): Int = jni

  def getPort(channel: Byte): Int = jni
}

@extern @link("CTRE_PhoenixCCI")
object JNILoad {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: VM, reserved: Ptr[Unit]): Int = extern
}
