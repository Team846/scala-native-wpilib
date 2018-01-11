/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

import com.lynbrookrobotics.scalanativejni._

/**
  * The NotifierJNI class directly wraps the C++ HAL Notifier.
  *
  * <p>This class is not meant for direct use by teams. Instead, the edu.wpi.first.wpilibj.Notifier
  * class, which corresponds to the C++ Notifier class, should be used.
  */
@jnilib("wpilibJNI")
object NotifierJNI extends JNIWrapper {
  /**
    * Initializes the notifier.
    */
  def initializeNotifier: Int = jni

  /**
    * Wakes up the waiter with time=0.  Note: after this function is called, all
    * calls to waitForNotifierAlarm() will immediately start returning 0.
    */
  def stopNotifier(notifierHandle: Int): Unit = jni

  /**
    * Deletes the notifier object when we are done with it.
    */
  def cleanNotifier(notifierHandle: Int): Unit = jni

  /**
    * Sets the notifier to wakeup the waiter in another triggerTime microseconds.
    */
  def updateNotifierAlarm(notifierHandle: Int, triggerTime: Long): Unit = jni

  /**
    * Cancels any pending wakeups set by updateNotifierAlarm().  Does NOT wake
    * up any waiters.
    */
  def cancelNotifierAlarm(notifierHandle: Int): Unit = jni

  /**
    * Block until woken up by an alarm (or stop).
    *
    * @return Time when woken up.
    */
  def waitForNotifierAlarm(notifierHandle: Int): Long = jni
}
