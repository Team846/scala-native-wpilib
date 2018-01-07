/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

/**
  * Structure for holding the values stored in an accumulator.
  */
class AccumulatorResult {
  /**
    * The total value accumulated.
    */
  @SuppressWarnings(Array("MemberName")) var value = 0L
  /**
    * The number of sample value was accumulated over.
    */
  @SuppressWarnings(Array("MemberName")) var count = 0L

  /**
    * Set the value and count.
    */
  def set(value: Long, count: Long): Unit = {
    this.value = value
    this.count = count
  }
}
