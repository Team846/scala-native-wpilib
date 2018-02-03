package com.lynbrookrobotics.example.drivetrain

import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.TwoSidedDriveProperties
import squants.motion.{Acceleration, RadiansPerSecond, Velocity}
import squants.space.Inches
import squants.time.Seconds
import squants.{Dimensionless, Length}

case class DrivetrainProperties(maxLeftVelocity: Velocity, maxRightVelocity: Velocity,
                                maxAcceleration: Acceleration,
                                wheelDiameter: Length, track: Length, gearRatio: Double,
                                turnControlGains: TurnVelocityGains,
                                forwardPositionControlGains: ForwardPositionGains,
                                turnPositionControlGains: TurnPositionGains,
                                leftControlGains: ForwardVelocityGains,
                                rightControlGains: ForwardVelocityGains,
                                currentLimit: Dimensionless,
                                defaultLookAheadDistance: Length) extends TwoSidedDriveProperties {
  override val maxTurnVelocity = RadiansPerSecond((((maxLeftVelocity + maxRightVelocity) * Seconds(1)) / Inches(21.75)) / 2)
  override val blendExponent = 0D
}
