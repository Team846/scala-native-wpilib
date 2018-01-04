package edu.wpi.first.wpilibj.hal

import scala.scalanative.native._

@extern @link("wpilibJavaJNI")
object ConstantsJNI {
  @name("Java_edu_wpi_first_wpilibj_hal_ConstantsJNI_getSystemClockTicksPerMicrosecond")
  def getSystemClockTicksPerMicrosecond(env: Ptr[Unit], cls: Ptr[Unit]): Int = extern
}
