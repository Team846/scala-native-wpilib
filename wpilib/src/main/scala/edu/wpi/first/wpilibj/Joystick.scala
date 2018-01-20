/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL

/**
  * Construct an instance of a joystick. The joystick index is the USB port on the drivers
  * station.
  *
  * @param port The port on the Driver Station that the joystick is plugged into.
  */
class Joystick(val port: Int) extends GenericHID(port) {
  HAL.report(tResourceType.kResourceType_Joystick, m_port)
  final private val m_axes = new Array[Byte](Joystick.Axis.kNumAxes.value)
  m_axes(Joystick.Axis.kX.value) = Joystick.kDefaultXAxis
  m_axes(Joystick.Axis.kY.value) = Joystick.kDefaultYAxis
  m_axes(Joystick.Axis.kZ.value) = Joystick.kDefaultZAxis
  m_axes(Joystick.Axis.kTwist.value) = Joystick.kDefaultTwistAxis
  m_axes(Joystick.Axis.kThrottle.value) = Joystick.kDefaultThrottleAxis

  /**
    * Set the channel associated with the X axis.
    *
    * @param channel The channel to set the axis to.
    */
  def setXChannel(channel: Int): Unit = {
    m_axes(Joystick.Axis.kX.value) = channel.toByte
  }

  /**
    * Set the channel associated with the Y axis.
    *
    * @param channel The channel to set the axis to.
    */
  def setYChannel(channel: Int): Unit = {
    m_axes(Joystick.Axis.kY.value) = channel.toByte
  }

  /**
    * Set the channel associated with the Z axis.
    *
    * @param channel The channel to set the axis to.
    */
  def setZChannel(channel: Int): Unit = {
    m_axes(Joystick.Axis.kZ.value) = channel.toByte
  }

  /**
    * Set the channel associated with the throttle axis.
    *
    * @param channel The channel to set the axis to.
    */
  def setThrottleChannel(channel: Int): Unit = {
    m_axes(Joystick.Axis.kThrottle.value) = channel.toByte
  }

  /**
    * Set the channel associated with the twist axis.
    *
    * @param channel The channel to set the axis to.
    */
  def setTwistChannel(channel: Int): Unit = {
    m_axes(Joystick.Axis.kTwist.value) = channel.toByte
  }

  /**
    * Set the channel associated with a specified axis.
    *
    * @deprecated Use the more specific axis channel setter functions.
    * @param axis    The axis to set the channel for.
    * @param channel The channel to set the axis to.
    */
  @deprecated def setAxisChannel(axis: Joystick.AxisType, channel: Int): Unit = {
    m_axes(axis.value) = channel.toByte
  }

  /**
    * Get the channel currently associated with the X axis.
    *
    * @return The channel for the axis.
    */
  def getXChannel = m_axes(Joystick.Axis.kX.value)

  /**
    * Get the channel currently associated with the Y axis.
    *
    * @return The channel for the axis.
    */
  def getYChannel = m_axes(Joystick.Axis.kY.value)

  /**
    * Get the channel currently associated with the Z axis.
    *
    * @return The channel for the axis.
    */
  def getZChannel = m_axes(Joystick.Axis.kZ.value)

  /**
    * Get the channel currently associated with the twist axis.
    *
    * @return The channel for the axis.
    */
  def getTwistChannel = m_axes(Joystick.Axis.kTwist.value)

  /**
    * Get the channel currently associated with the throttle axis.
    *
    * @return The channel for the axis.
    */
  def getThrottleChannel = m_axes(Joystick.Axis.kThrottle.value)

  /**
    * Get the channel currently associated with the specified axis.
    *
    * @deprecated Use the more specific axis channel getter functions.
    * @param axis The axis to look up the channel for.
    * @return The channel for the axis.
    */
  @deprecated def getAxisChannel(axis: Joystick.AxisType) = m_axes(axis.value)

  /**
    * Get the X value of the joystick. This depends on the mapping of the joystick connected to the
    * current port.
    *
    * @param hand Unused
    * @return The X value of the joystick.
    */
  override final def getX(hand: GenericHID.Hand): Double = getRawAxis(m_axes(Joystick.Axis.kX.value))

  /**
    * Get the Y value of the joystick. This depends on the mapping of the joystick connected to the
    * current port.
    *
    * @param hand Unused
    * @return The Y value of the joystick.
    */
  override final def getY(hand: GenericHID.Hand): Double = getRawAxis(m_axes(Joystick.Axis.kY.value))

  /**
    * Get the z position of the HID.
    *
    * @return the z position
    */
  def getZ(): Double = getRawAxis(m_axes(Joystick.Axis.kZ.value))

  /**
    * Get the twist value of the current joystick. This depends on the mapping of the joystick
    * connected to the current port.
    *
    * @return The Twist value of the joystick.
    */
  def getTwist: Double = getRawAxis(m_axes(Joystick.Axis.kTwist.value))

  /**
    * Get the throttle value of the current joystick. This depends on the mapping of the joystick
    * connected to the current port.
    *
    * @return The Throttle value of the joystick.
    */
  def getThrottle: Double = getRawAxis(m_axes(Joystick.Axis.kThrottle.value))

  /**
    * For the current joystick, return the axis determined by the argument.
    *
    * <p>This is for cases where the joystick axis is returned programmatically, otherwise one of the
    * previous functions would be preferable (for example getX()).
    *
    * @deprecated Use the more specific axis getter functions.
    * @param axis The axis to read.
    * @return The value of the axis.
    */
  @deprecated def getAxis(axis: Joystick.AxisType): Double = axis match {
    case Joystick.AxisType.kX =>
      getX()
    case Joystick.AxisType.kY =>
      getY()
    case Joystick.AxisType.kZ =>
      getZ()
    case Joystick.AxisType.kTwist =>
      getTwist
    case Joystick.AxisType.kThrottle =>
      getThrottle
    case _ =>
      0.0
  }

  /**
    * Read the state of the trigger on the joystick.
    *
    * @return The state of the trigger.
    */
  def getTrigger: Boolean = getRawButton(Joystick.Button.kTrigger.value)

  /**
    * Whether the trigger was pressed since the last check.
    *
    * @return Whether the button was pressed since the last check.
    */
  def getTriggerPressed: Boolean = getRawButtonPressed(Joystick.Button.kTrigger.value)

  /**
    * Whether the trigger was released since the last check.
    *
    * @return Whether the button was released since the last check.
    */
  def getTriggerReleased: Boolean = getRawButtonReleased(Joystick.Button.kTrigger.value)

  /**
    * Read the state of the top button on the joystick.
    *
    * @return The state of the top button.
    */
  def getTop: Boolean = getRawButton(Joystick.Button.kTop.value)

  /**
    * Whether the top button was pressed since the last check.
    *
    * @return Whether the button was pressed since the last check.
    */
  def getTopPressed: Boolean = getRawButtonPressed(Joystick.Button.kTop.value)

  /**
    * Whether the top button was released since the last check.
    *
    * @return Whether the button was released since the last check.
    */
  def getTopReleased: Boolean = getRawButtonReleased(Joystick.Button.kTop.value)

  /**
    * Get buttons based on an enumerated type.
    *
    * <p>The button type will be looked up in the list of buttons and then read.
    *
    * @deprecated Use Button enum values instead of ButtonType.
    * @param button The type of button to read.
    * @return The state of the button.
    */
  @deprecated def getButton(button: Joystick.ButtonType): Boolean = getRawButton(button.value)

  /**
    * Get the magnitude of the direction vector formed by the joystick's current position relative to
    * its origin.
    *
    * @return The magnitude of the direction vector
    */
  def getMagnitude: Double = Math.sqrt(Math.pow(getX, 2) + Math.pow(getY, 2))

  /**
    * Get the direction of the vector formed by the joystick and its origin in radians.
    *
    * @return The direction of the vector in radians
    */
  def getDirectionRadians: Double = Math.atan2(getX, -(this: GenericHID).getY)

  /**
    * Get the direction of the vector formed by the joystick and its origin in degrees.
    *
    * @return The direction of the vector in degrees
    */
  def getDirectionDegrees: Double = Math.toDegrees(getDirectionRadians)
}

/**
  * Handle input from standard Joysticks connected to the Driver Station.
  *
  * <p>This class handles standard input that comes from the Driver Station. Each time a value is
  * requested the most recent value is returned. There is a single class instance for each joystick
  * and the mapping of ports to hardware buttons depends on the code in the Driver Station.
  */
object Joystick {
  private[wpilibj] val kDefaultXAxis: Byte = 0
  private[wpilibj] val kDefaultYAxis: Byte = 1
  private[wpilibj] val kDefaultZAxis: Byte = 2
  private[wpilibj] val kDefaultTwistAxis: Byte = 2
  private[wpilibj] val kDefaultThrottleAxis: Byte = 3

  /**
    * Represents an analog axis on a joystick.
    */
  final class AxisType(val value: Int)
  object AxisType {
    val kX = new AxisType(0)
    val kY = new AxisType(1)
    val kZ = new AxisType(2)
    val kTwist = new AxisType(3)
    val kThrottle = new AxisType(4)
  }

  /**
    * Represents a digital button on a joystick.
    */
  final class ButtonType(val value: Int)
  object ButtonType {
    val kTrigger = new ButtonType(1)
    val kTop = new ButtonType(2)
  }

  /**
    * Represents a digital button on a joystick.
    */
  final class Button(val value: Int)
  object Button {
    val kTrigger = new Button(1)
    val kTop = new Button(2)
  }

  /**
    * Represents an analog axis on a joystick.
    */
  final class Axis(val value: Int)

  object Axis {
    val kX = new Axis(0)
    val kY = new Axis(1)
    val kZ = new Axis(2)
    val kTwist = new Axis(3)
    val kThrottle = new Axis(4)
    val kNumAxes = new Axis(5)
  }
}
