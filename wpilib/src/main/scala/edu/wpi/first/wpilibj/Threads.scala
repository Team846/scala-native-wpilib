/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.ThreadsJNI

object Threads {
  /**
    * Get the thread priority for the current thread.
    *
    * @return The current thread priority. Scaled 1-99.
    */
  def getCurrentThreadPriority: Int = ThreadsJNI.getCurrentThreadPriority

  /**
    * Get if the current thread is realtime.
    *
    * @return If the current thread is realtime
    */
  def getCurrentThreadIsRealTime: Boolean = ThreadsJNI.getCurrentThreadIsRealTime

  /**
    * Sets the thread priority for the current thread.
    *
    * @param realTime Set to true to set a realtime priority, false for standard
    *                 priority
    * @param priority Priority to set the thread to. Scaled 1-99, with 1 being
    *     highest. On RoboRIO, priority is ignored for non realtime setting
    * @return The success state of setting the priority
    */
  def setCurrentThreadPriority(realTime: Boolean, priority: Int): Boolean = ThreadsJNI.setCurrentThreadPriority(realTime, priority)
}
