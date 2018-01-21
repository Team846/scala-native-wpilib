package com.lynbrookrobotics.example

import com.lynbrookrobotics.example.driver.DriverConfig
import com.lynbrookrobotics.example.drivetrain.DrivetrainConfig

case class RobotConfig(driver: DriverConfig,
                       drivetrain: DrivetrainConfig)
