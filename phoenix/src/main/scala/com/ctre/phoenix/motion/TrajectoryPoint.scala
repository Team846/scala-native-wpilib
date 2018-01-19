package com.ctre.phoenix.motion

class TrajectoryPoint {
  var position = .0 // !< The position to servo to.

  var velocity = .0 // !< The velocity to feed-forward.

  var headingDeg = .0
  /**
    * Which slot to get PIDF gains. PID is used for position servo. F is used
    * as the Kv constant for velocity feed-forward. Typically this is hardcoded
    * to the a particular slot, but you are free gain schedule if need be.
    * Choose from [0,3]
    */
  var profileSlotSelect0 = 0
  /**
    * Which slot to get PIDF gains for cascaded PId.
    * This only has impact during MotionProfileArc Control mode.
    * Choose from [0,1].
    */
  var profileSlotSelect1 = 0
  /**
    * Set to true to signal Talon that this is the final point, so do not
    * attempt to pop another trajectory point from out of the Talon buffer.
    * Instead continue processing this way point. Typically the velocity member
    * variable should be zero so that the motor doesn't spin indefinitely.
    */
  var isLastPoint = false
  /**
    * Set to true to signal Talon to zero the selected sensor. When generating
    * MPs, one simple method is to make the first target position zero, and the
    * final target position the target distance from the current position. Then
    * when you fire the MP, the current position gets set to zero. If this is
    * the intent, you can set zeroPos on the first trajectory point.
    *
    * Otherwise you can leave this false for all points, and offset the
    * positions of all trajectory points so they are correct.
    */
  var zeroPos = false
  /**
    * Duration to apply this trajectory pt.
    * This time unit is ADDED to the exising base time set by
    * configMotionProfileTrajectoryPeriod().
    */
  var timeDur: TrajectoryPoint.TrajectoryDuration = null
}

/**
  * Motion Profile Trajectory Point This is simply a data transer object.
  */
object TrajectoryPoint {
  /**
    * Duration to apply to a particular trajectory pt.
    * This time unit is ADDED to the exising base time set by
    * configMotionProfileTrajectoryPeriod().
    */
  final class TrajectoryDuration(val value: Int)
  object TrajectoryDuration {
    val Trajectory_Duration_0ms = new TrajectoryDuration(0)
    val Trajectory_Duration_5ms = new TrajectoryDuration(5)
    val Trajectory_Duration_10ms = new TrajectoryDuration(10)
    val Trajectory_Duration_20ms = new TrajectoryDuration(20)
    val Trajectory_Duration_30ms = new TrajectoryDuration(30)
    val Trajectory_Duration_40ms = new TrajectoryDuration(40)
    val Trajectory_Duration_50ms = new TrajectoryDuration(50)
    val Trajectory_Duration_100ms = new TrajectoryDuration(100)

    val values = Seq(
      Trajectory_Duration_0ms,
      Trajectory_Duration_5ms,
      Trajectory_Duration_10ms,
      Trajectory_Duration_20ms,
      Trajectory_Duration_30ms,
      Trajectory_Duration_40ms,
      Trajectory_Duration_50ms,
      Trajectory_Duration_100ms
    )
    
    def valueOf(`val`: Int): TrajectoryPoint.TrajectoryDuration = {
      for (td <- TrajectoryDuration.values) {
        if (td.value == `val`) return td
      }
      Trajectory_Duration_100ms
    }
  }
}
