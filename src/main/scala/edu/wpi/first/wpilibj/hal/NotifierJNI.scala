package edu.wpi.first.wpilibj.hal

import scala.scalanative.native._

@extern @link("wpilibJavaJNI")
object NotifierJNI {
  @name("Java_edu_wpi_first_wpilibj_hal_NotifierJNI_initializeNotifier")
  def initializeNotifier(env: Ptr[Unit], cls: Ptr[Unit]): Int = extern
}
