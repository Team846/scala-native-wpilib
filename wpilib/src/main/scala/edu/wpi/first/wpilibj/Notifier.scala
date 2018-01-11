/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import edu.wpi.first.wpilibj.hal.NotifierJNI

/**
  * Create a Notifier for timer event notification.
  *
  * @param run The handler that is called at the notification time which is set using StartSingle
  *            or StartPeriodic.
  */
class Notifier(// The handler passed in by the user which should be called at the
               // appropriate interval.
               private var m_handler: Runnable) {
  // The thread waiting on the HAL alarm.
  final private var m_thread: Thread = null
  // The lock for the process information.
  final private val m_processLock = new ReentrantLock
  // The C pointer to the notifier object. We don't use it directly, it is
  // just passed to the JNI bindings.
  final private val m_notifier = new AtomicInteger
  // The time, in microseconds, at which the corresponding handler should be
  // called. Has the same zero as Utility.getFPGATime().
  private var m_expirationTime: Double = 0
  // Whether we are calling the handler just once or periodically.
  private var m_periodic = false
  // If periodic, the period of the calling; if just once, stores how long it
  // is until we call the handler.
  private var m_period: Double = 0

  m_notifier.set(NotifierJNI.initializeNotifier)
  m_thread = new Thread(new Runnable {
    override def run() = {
      while (!Thread.interrupted) {
        val notifier = m_notifier.get
        if (notifier != 0) {
          val curTime = NotifierJNI.waitForNotifierAlarm(notifier)
          if (curTime != 0) {
            var handler: Runnable = null
            m_processLock.lock()
            try {
              handler = m_handler
              if (m_periodic) {
                m_expirationTime += m_period
                updateAlarm()
              }
            } finally {
              m_processLock.unlock()
            }

            if (handler != null) {
              handler.run()
            }
          }
        }
      }
    }
  })
  m_thread.setDaemon(true)
  m_thread.start()

  @SuppressWarnings(Array("NoFinalizer")) override protected def finalize(): Unit = {
    val handle = m_notifier.getAndSet(0)
    NotifierJNI.stopNotifier(handle)
    // Join the thread to ensure the handler has exited.
    if (m_thread.isAlive) try {
      m_thread.interrupt()
      m_thread.join()
    } catch {
      case ex: InterruptedException =>
        Thread.currentThread.interrupt()
    }
    NotifierJNI.cleanNotifier(handle)
  }

  /**
    * Update the alarm hardware to reflect the next alarm.
    */
  private def updateAlarm(): Unit = {
    val notifier = m_notifier.get
    if (notifier == 0) return
    NotifierJNI.updateNotifierAlarm(notifier, (m_expirationTime * 1e6).toLong)
  }

  /**
    * Change the handler function.
    *
    * @param handler Handler
    */
  def setHandler(handler: Runnable): Unit = {
    m_processLock.lock()
    try
      m_handler = handler
    finally m_processLock.unlock()
  }

  /**
    * Register for single event notification. A timer event is queued for a single event after the
    * specified delay.
    *
    * @param delay Seconds to wait before the handler is called.
    */
  def startSingle(delay: Double): Unit = {
    m_processLock.lock()
    try {
      m_periodic = false
      m_period = delay
      m_expirationTime = RobotController.getFPGATime * 1e-6 + delay
      updateAlarm()
    } finally m_processLock.unlock()
  }

  /**
    * Register for periodic event notification. A timer event is queued for periodic event
    * notification. Each time the interrupt occurs, the event will be immediately requeued for the
    * same time interval.
    *
    * @param period Period in seconds to call the handler starting one period after the call to this
    *               method.
    */
  def startPeriodic(period: Double): Unit = {
    m_processLock.lock()
    try {
      m_periodic = true
      m_period = period
      m_expirationTime = RobotController.getFPGATime * 1e-6 + period
      updateAlarm()
    } finally {
      m_processLock.unlock()
    }
  }

  /**
    * Stop timer events from occurring. Stop any repeating timer events from occurring. This will
    * also remove any single notification events from the queue. If a timer-based call to the
    * registered handler is in progress, this function will block until the handler call is complete.
    */
  def stop(): Unit = {
    NotifierJNI.cancelNotifierAlarm(m_notifier.get)
  }
}
