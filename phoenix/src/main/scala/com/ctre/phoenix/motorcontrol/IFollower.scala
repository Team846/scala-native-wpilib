package com.ctre.phoenix.motorcontrol

import com.ctre.phoenix.motorcontrol.IMotorController

trait IFollower {
  def follow(masterToFollow: IMotorController): Unit

  def valueUpdated(): Unit
}