package com.ctre.phoenix.motorcontrol.can

import com.ctre.phoenix.motorcontrol.IMotorController
import edu.wpi.first.wpilibj.hal.HAL

/**
  * VEX Victor SPX Motor Controller when used on CAN Bus.
  *
  * Constructor
  *
  * @param deviceNumber
  * [0,62]
  */
class VictorSPX(val deviceNumber: Int)
  extends BaseMotorController(deviceNumber | 0x01040000) with IMotorController {
  HAL.report(65, deviceNumber + 1)
}
