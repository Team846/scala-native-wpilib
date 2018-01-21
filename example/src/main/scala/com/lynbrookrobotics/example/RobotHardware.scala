package com.lynbrookrobotics.example

import com.lynbrookrobotics.example.driver.DriverHardware
import com.lynbrookrobotics.example.drivetrain.DrivetrainHardware
import com.lynbrookrobotics.potassium.clock.Clock

case class RobotHardware(driver: DriverHardware,
                         drivetrain: DrivetrainHardware)

object RobotHardware {
  def apply(robotConfig: RobotConfig)(implicit clock: Clock): RobotHardware = {
    val driver = DriverHardware(robotConfig.driver)

    import robotConfig._

    RobotHardware(
      driver = driver,
      drivetrain = if (drivetrain != null) DrivetrainHardware(drivetrain, driver) else null
    )
  }
}
