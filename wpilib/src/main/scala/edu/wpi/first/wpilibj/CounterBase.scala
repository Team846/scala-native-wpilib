/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

trait CounterBase {
  /**
    * Get the count.
    *
    * @return the count
    */
  def get: Int

  /**
    * Reset the count to zero.
    */
  def reset(): Unit

  /**
    * Get the time between the last two edges counted.
    *
    * @return the time between the last two ticks in seconds
    */
  def getPeriod: Double

  /**
    * Set the maximum time between edges to be considered stalled.
    *
    * @param maxPeriod the maximum period in seconds
    */
  def setMaxPeriod(maxPeriod: Double): Unit

  /**
    * Determine if the counter is not moving.
    *
    * @return true if the counter has not changed for the max period
    */
  def getStopped: Boolean

  /**
    * Determine which direction the counter is going.
    *
    * @return true for one direction, false for the other
    */
  def getDirection: Boolean
}


/**
  * Interface for counting the number of ticks on a digital input channel. Encoders, Gear tooth
  * sensors, and counters should all subclass this so it can be used to build more advanced classes
  * for control and driving.
  *
  * <p>All counters will immediately start counting - reset() them if you need them to be zeroed
  * before use.
  */
object CounterBase {
  /**
    * The number of edges for the counterbase to increment or decrement on.
    */
  final class EncodingType(val value: Int)
  object EncodingType {
    /**
      * Count only the rising edge.
      */
    val k1X = new EncodingType(0)

    /**
      * Count both the rising and falling edge.
      */
    val k2X = new EncodingType(1)

    /**
      * Count rising and falling on both channels.
      */
    val k4X = new EncodingType(2)
  }
}
