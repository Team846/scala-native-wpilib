/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

@jnilib("wpilibJNI")
object ThreadsJNI extends JNIWrapper {
  def getCurrentThreadPriority: Int = jni

  def getCurrentThreadIsRealTime: Boolean = jni

  def setCurrentThreadPriority(realTime: Boolean, priority: Int): Boolean = jni
}

