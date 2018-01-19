package com.ctre.phoenix.motorcontrol

final class NeutralMode(val value: Int)
object NeutralMode {
  /** Use the NeutralMode that is set by the jumper wire on the CAN device */
  val EEPROMSetting = new NeutralMode(0)

  /** Do not attempt to stop the motor. Instead allow it to coast to a stop
     without applying resistance. */
  val Coast = new NeutralMode(1)

  /** Stop the motor's rotation by applying a force. */
  val Brake = new NeutralMode(2)
}
