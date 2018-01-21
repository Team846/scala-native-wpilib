package com.lynbrookrobotics.example

import com.lynbrookrobotics.example.driver.DriverConfig
import com.lynbrookrobotics.example.drivetrain.{DrivetrainConfig, DrivetrainPorts, DrivetrainProperties}
import com.lynbrookrobotics.potassium.Signal
import com.lynbrookrobotics.potassium.control.PIDConfig
import com.lynbrookrobotics.potassium.events.ImpulseEventSource
import com.lynbrookrobotics.potassium.frc.WPIClock
import com.lynbrookrobotics.potassium.streams.Stream
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.hal.HAL
import squants.Percent
import squants.motion.{DegreesPerSecond, FeetPerSecond, MetersPerSecondSquared}
import squants.space.{Degrees, Feet, Inches, Meters}
import squants.time.Seconds
import com.lynbrookrobotics.potassium.units._
import GenericValue._

object LaunchRobot extends RobotBase {
  implicit val clock = WPIClock

  private var coreRobot: CoreRobot = null

  private val ds = m_ds

  private val eventPollingSource = new ImpulseEventSource
  private implicit val eventPolling = eventPollingSource.event

  implicit val config = Signal.constant(RobotConfig(
    driver = DriverConfig(
      driverPort = 0,
      operatorPort = 1,
      driverWheelPort = 2,
      launchpadPort = 3
    ),
    drivetrain = DrivetrainConfig(
      ports = DrivetrainPorts(
        leftBack = 50,
        leftFront = 51,
        rightBack = 41,
        rightFront = 40
      ),
      properties = DrivetrainProperties(
        maxLeftVelocity = FeetPerSecond(21.9),
        maxRightVelocity = FeetPerSecond(23.1),
        maxAcceleration = MetersPerSecondSquared(0),
        wheelDiameter = Inches(4),
        track = Inches(21.75),
        gearRatio = 28.0 / 56,
        turnControlGains = PIDConfig(
          Percent(50) / DegreesPerSecond(360),
          Percent(0) / Degrees(1),
          Percent(0) / (DegreesPerSecond(1).toGeneric / Seconds(1))
        ),
        forwardPositionControlGains = PIDConfig(
          Percent(100) / Feet(2),
          Percent(0) / (Feet(1).toGeneric * Seconds(1)),
          Percent(0) / FeetPerSecond(1)
        ),
        turnPositionControlGains = PIDConfig(
          Percent(100) / Degrees(90),
          Percent(0) / (Degrees(1).toGeneric * Seconds(1)),
          Percent(0) / DegreesPerSecond(1)
        ),
        leftControlGains = PIDConfig(
          Percent(30) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        rightControlGains = PIDConfig(
          Percent(30) / FeetPerSecond(5),
          Percent(0) / Meters(1),
          Percent(0) / MetersPerSecondSquared(1)
        ),
        currentLimit = Percent(50),
        defaultLookAheadDistance = Feet(1)
      )
    )
  ))

  implicit val hardware = RobotHardware(config.get)

  override def startCompetition(): Unit = {
    coreRobot = new CoreRobot(
      Stream.periodic(Seconds(0.01))(())
    )

    HAL.observeUserProgramStarting()

    while (true) {
      ds.waitForData()
      eventPollingSource.fire()
      coreRobot.driverHardware.driverStationUpdate.apply()
    }
  }

  override def main(args: Array[String]): Unit = super.main(args)
}
