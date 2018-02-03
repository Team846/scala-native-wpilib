package com.lynbrookrobotics.example

import com.lynbrookrobotics.example.drivetrain.DrivetrainComponent
import com.lynbrookrobotics.potassium.{Component, Signal}
import com.lynbrookrobotics.potassium.clock.Clock
import com.lynbrookrobotics.potassium.events.ImpulseEvent
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.networktables.{NetworkTable, NetworkTableInstance}

class CoreRobot(val coreTicks: Stream[Unit])
               (implicit val config: Signal[RobotConfig], hardware: RobotHardware,
                val clock: Clock, val polling: ImpulseEvent) {
  implicit val driverHardware = hardware.driver
  private val ds = driverHardware.station
//
//  // Drivetrain
//  implicit val drivetrainHardware = hardware.drivetrain
//  implicit val drivetrainProps = config.map(_.drivetrain.properties)
//  val drivetrain: Option[DrivetrainComponent] =
//    if (config.get.drivetrain != null) Some(new DrivetrainComponent) else None
//
//  private val components: List[Component[_]] = List(
//    drivetrain
//  ).flatten
//
//  // Needs to go last because component resets have highest priority
  private val enabled = driverHardware.joystickStream.eventWhen(_ => ds.isEnabled)
//  enabled.onStart.foreach { () =>
//    if (drivetrain.isDefined) {
//      drivetrainHardware.gyro.endCalibration()
//    }
//
//    components.foreach(_.resetToDefault())
//  }

  val inst = NetworkTableInstance.getDefault()
  val tab = inst.getTable("/SmartDashboard")
  val ent = tab.getEntry("DB/Slider 0")
  enabled.foreach { () =>
    println(ent.getDouble(-1))
  }

//  enabled.onEnd.foreach { () =>
//    components.foreach(_.resetToDefault())
//  }
}
