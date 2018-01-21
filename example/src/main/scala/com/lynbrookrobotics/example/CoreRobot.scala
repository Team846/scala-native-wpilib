package com.lynbrookrobotics.example

import com.lynbrookrobotics.example.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent

import com.lynbrookrobotics.potassium.streams.Stream

class CoreRobot(val coreTicks: Stream[Unit])
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                val clock: Clock, val polling: ImpulseEvent) {
  implicit val driverHardware = hardware.driver
  private val ds = driverHardware.station

  // Drivetrain
  implicit val drivetrainHardware = hardware.drivetrain
  implicit val drivetrainProps = config.map(_.drivetrain.properties)
  val drivetrain: Option[DrivetrainComponent] =
    if (config.get.drivetrain != null) Some(new DrivetrainComponent) else None

  private val components: List[Component[_]] = List(
    drivetrain
  ).flatten

  // Needs to go last because component resets have highest priority
  private val enabled = Signal(ds.isEnabled).filter(identity)
  enabled.onStart.foreach { () =>
    if (drivetrain.isDefined) {
      drivetrainHardware.gyro.endCalibration()
    }

    components.foreach(_.resetToDefault())
  }

  enabled.onEnd.foreach { () =>
    components.foreach(_.resetToDefault())
  }
}
