/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.util.BaseSystemNotInitializedException

class Timer @SuppressWarnings(Array("JavadocMethod"))() {
  final private var m_timer: Timer.Interface = null
  if (Timer.impl != null) m_timer = Timer.impl.newTimer
  else throw new BaseSystemNotInitializedException(classOf[Timer.StaticInterface], classOf[Timer])

  /**
    * Get the current time from the timer. If the clock is running it is derived from the current
    * system clock the start time stored in the timer class. If the clock is not running, then return
    * the time when it was last stopped.
    *
    * @return Current time value for this timer in seconds
    */
  def get: Double = m_timer.get

  /**
    * Reset the timer by setting the time to 0. Make the timer startTime the current time so new
    * requests will be relative now
    */
  def reset(): Unit = {
    m_timer.reset()
  }

  /**
    * Start the timer running. Just set the running flag to true indicating that all time requests
    * should be relative to the system clock.
    */
  def start(): Unit = {
    m_timer.start()
  }

  /**
    * Stop the timer. This computes the time as of now and clears the running flag, causing all
    * subsequent time requests to be read from the accumulated time rather than looking at the system
    * clock.
    */
  def stop(): Unit = {
    m_timer.stop()
  }

  /**
    * Check if the period specified has passed and if it has, advance the start time by that period.
    * This is useful to decide if it's time to do periodic work without drifting later by the time it
    * took to get around to checking.
    *
    * @param period The period to check for (in seconds).
    * @return If the period has passed.
    */
  def hasPeriodPassed(period: Double): Boolean = m_timer.hasPeriodPassed(period)
}

object Timer {
  private var impl: StaticInterface = null

  @SuppressWarnings(Array("MethodName")) def SetImplementation(ti: Timer.StaticInterface): Unit = {
    impl = ti
  }

  /**
    * Return the system clock time in seconds. Return the time from the FPGA hardware clock in
    * seconds since the FPGA started.
    *
    * @return Robot running time in seconds.
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName"))
  def getFPGATimestamp: Double = {
    if (impl != null) impl.getFPGATimestamp
    else throw new BaseSystemNotInitializedException(classOf[Timer.StaticInterface], classOf[Timer])
  }

  /**
    * Return the approximate match time. The FMS does not send an official match time to the robots,
    * but does send an approximate match time. The value will count down the time remaining in the
    * current period (auto or teleop). Warning: This is not an official time (so it cannot be used to
    * dispute ref calls or guarantee that a function will trigger before the match ends) The
    * Practice Match function of the DS approximates the behaviour seen on the field.
    *
    * @return Time remaining in current match period (auto or teleop) in seconds
    */
  def getMatchTime: Double = {
    if (impl != null) impl.getMatchTime
    else throw new BaseSystemNotInitializedException(classOf[Timer.StaticInterface], classOf[Timer])
  }

  /**
    * Pause the thread for a specified time. Pause the execution of the thread for a specified period
    * of time given in seconds. Motors will continue to run at their last assigned values, and
    * sensors will continue to update. Only the task containing the wait will pause until the wait
    * time is expired.
    *
    * @param seconds Length of time to pause
    */
  def delay(seconds: Double): Unit = {
    if (impl != null) impl.delay(seconds)
    else throw new BaseSystemNotInitializedException(classOf[Timer.StaticInterface], classOf[Timer])
  }

  trait StaticInterface {
    @SuppressWarnings(Array("AbbreviationAsWordInName")) def getFPGATimestamp: Double

    def getMatchTime: Double

    def delay(seconds: Double): Unit

    @SuppressWarnings(Array("JavadocMethod")) def newTimer: Timer.Interface
  }

  trait Interface {
    /**
      * Get the current time from the timer. If the clock is running it is derived from the current
      * system clock the start time stored in the timer class. If the clock is not running, then
      * return the time when it was last stopped.
      *
      * @return Current time value for this timer in seconds
      */
    def get: Double

    /**
      * Reset the timer by setting the time to 0. Make the timer startTime the current time so new
      * requests will be relative now
      */
    def reset(): Unit

    /**
      * Start the timer running. Just set the running flag to true indicating that all time requests
      * should be relative to the system clock.
      */
    def start(): Unit

    /**
      * Stop the timer. This computes the time as of now and clears the running flag, causing all
      * subsequent time requests to be read from the accumulated time rather than looking at the
      * system clock.
      */
    def stop(): Unit

    /**
      * Check if the period specified has passed and if it has, advance the start time by that
      * period. This is useful to decide if it's time to do periodic work without drifting later by
      * the time it took to get around to checking.
      *
      * @param period The period to check for (in seconds).
      * @return If the period has passed.
      */
    def hasPeriodPassed(period: Double): Boolean
  }

}
