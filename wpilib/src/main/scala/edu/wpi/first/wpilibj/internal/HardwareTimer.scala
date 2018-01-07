/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.internal

import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj.Timer

/**
  * Timer objects measure accumulated time in milliseconds. The timer object functions like a
  * stopwatch. It can be started, stopped, and cleared. When the timer is running its value counts
  * up in milliseconds. When stopped, the timer holds the current value. The implementation simply
  * records the time when started and subtracts the current time whenever the value is requested.
  */
class HardwareTimer extends Timer.StaticInterface {
  /**
    * Pause the thread for a specified time. Pause the execution of the thread for a specified period
    * of time given in seconds. Motors will continue to run at their last assigned values, and
    * sensors will continue to update. Only the task containing the wait will pause until the wait
    * time is expired.
    *
    * @param seconds Length of time to pause
    */
  override def delay(seconds: Double): Unit = {
    try
      Thread.sleep((seconds * 1e3).toLong)
    catch {
      case ex: InterruptedException =>
        Thread.currentThread.interrupt()
    }
  }

  /**
    * Return the system clock time in seconds. Return the time from the FPGA hardware clock in
    * seconds since the FPGA started.
    *
    * @return Robot running time in seconds.
    */
  override def getFPGATimestamp: Double = RobotController.getFPGATime / 1000000.0

  override def getMatchTime: Double = DriverStation.getInstance.getMatchTime

  override def newTimer = new TimerImpl

  /**
    * Create a new timer object. Create a new timer object and reset the time to zero. The timer is
    * initially not running and must be started.
    */
  private[internal] class TimerImpl private[internal]() extends Timer.Interface {
    reset()
    private var m_startTime = .0
    private var m_accumulatedTime = .0
    private var m_running = false

    private def getMsClock = RobotController.getFPGATime / 1000.0

    /**
      * Get the current time from the timer. If the clock is running it is derived from the current
      * system clock the start time stored in the timer class. If the clock is not running, then
      * return the time when it was last stopped.
      *
      * @return Current time value for this timer in seconds
      */
    override def get: Double = if (m_running) ((getMsClock - m_startTime) + m_accumulatedTime) / 1000.0
    else m_accumulatedTime

    /**
      * Reset the timer by setting the time to 0. Make the timer start time the current time so new
      * requests will be relative now
      */
    override def reset(): Unit = {
      m_accumulatedTime = 0
      m_startTime = getMsClock
    }

    /**
      * Start the timer running. Just set the running flag to true indicating that all time
      * requests should be relative to the system clock.
      */
    override def start(): Unit = {
      m_startTime = getMsClock
      m_running = true
    }

    /**
      * Stop the timer. This computes the time as of now and clears the running flag, causing all
      * subsequent time requests to be read from the accumulated time rather than looking at the
      * system clock.
      */
    override def stop(): Unit = {
      val temp = get
      m_accumulatedTime = temp
      m_running = false
    }

    /**
      * Check if the period specified has passed and if it has, advance the start time by that
      * period. This is useful to decide if it's time to do periodic work without drifting later by
      * the time it took to get around to checking.
      *
      * @param period The period to check for (in seconds).
      * @return If the period has passed.
      */
    override def hasPeriodPassed(period: Double): Boolean = {
      if (get > period) { // Advance the start time by the period.
        // Don't set it to the current time... we want to avoid drift.
        m_startTime += period * 1000
        return true
      }
      false
    }
  }

}
