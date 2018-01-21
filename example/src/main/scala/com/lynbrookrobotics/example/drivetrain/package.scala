package com.lynbrookrobotics.example

import com.ctre.phoenix.motorcontrol.ControlMode
import com.lynbrookrobotics.potassium.commons.drivetrain._
import com.lynbrookrobotics.potassium.commons.drivetrain.twoSided.{TwoSidedDrive, TwoSidedSignal}
import com.lynbrookrobotics.potassium.commons.electronics.CurrentLimiting
import com.lynbrookrobotics.potassium.frc.Implicits._
import com.lynbrookrobotics.potassium.streams.Stream
import com.lynbrookrobotics.potassium.{Component, Signal}
import squants.Each
import squants.electro.Volts

package object drivetrain extends TwoSidedDrive { self =>
  type Hardware = DrivetrainHardware
  type Properties = DrivetrainProperties


  override protected def output(hardware: DrivetrainHardware,
                                signal: TwoSidedSignal): Unit = {
    hardware.leftBack.talon.set(ControlMode.PercentOutput, signal.left.toEach)
    hardware.leftFront.talon.set(ControlMode.PercentOutput, signal.left.toEach)
    hardware.rightBack.talon.set(ControlMode.PercentOutput, -signal.right.toEach)
    hardware.rightFront.talon.set(ControlMode.PercentOutput, -signal.right.toEach)
  }

  override protected def controlMode(implicit hardware: DrivetrainHardware,
                                     props: DrivetrainProperties): UnicycleControlMode = {
    if (hardware.driverHardware.station.isEnabled && hardware.driverHardware.station.isOperatorControl) {
      ArcadeControlsOpen(
        hardware.driverHardware.joystickStream.map(v => v.driver.y).syncTo(hardware.leftVelocity),
        hardware.driverHardware.joystickStream.map(v => v.driverWheel.x).syncTo(hardware.leftVelocity)
      )
    } else {
      NoOperation
    }
  }

  class DrivetrainComponent(implicit hardware: Hardware, props: Signal[Properties]) extends Component[TwoSidedSignal] {
    override def setController(controller: Stream[TwoSidedSignal]): Unit = {
      val currentLimited = controller.zip(hardware.leftVelocity).zip(hardware.rightVelocity).map { case ((control, leftVelocity), rightVelocity) =>
        val leftVelocityPercent = Each(leftVelocity / hardware.props.maxLeftVelocity)
        val rightVelocityPercent = Each(rightVelocity / hardware.props.maxRightVelocity)

//        val leftOut = CurrentLimiting.limitCurrentOutput(control.left,
//          leftVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)
//        val rightOut = CurrentLimiting.limitCurrentOutput(control.right,
//          rightVelocityPercent, hardware.props.currentLimit, hardware.props.currentLimit)

        TwoSidedSignal(control.left, control.right)
      }

      super.setController(currentLimited)
    }

    override def defaultController: Stream[TwoSidedSignal] = self.defaultController

    val normalDrivetrainVoltage = Volts(12)

    override def applySignal(signal: TwoSidedSignal): Unit = {
      val compFactor = normalDrivetrainVoltage / Volts(hardware.driverHardware.station.getBatteryVoltage)
      output(hardware, TwoSidedSignal(signal.left * compFactor, signal.right * compFactor))
    }
  }

  type Drivetrain = DrivetrainComponent
}

