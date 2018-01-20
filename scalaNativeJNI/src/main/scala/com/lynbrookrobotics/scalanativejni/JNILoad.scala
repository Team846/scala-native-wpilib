package com.lynbrookrobotics.scalanativejni

import scala.scalanative.native._

@extern
object JNILoad {
  @name("JNI_OnLoad")
  def JNI_OnLoad(vm: VM, reserved: Ptr[Unit]): Int = extern
}
