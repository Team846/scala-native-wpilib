/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.HAL

abstract class GenericHID(val m_port: Int) {
  private var m_ds = DriverStation.getInstance
  private var m_outputs = 0
  private var m_leftRumble: Short = 0
  private var m_rightRumble: Short = 0

  /**
    * Get the x position of the HID.
    *
    * @return the x position of the HID
    */
  final def getX(): Double = getX(GenericHID.Hand.kRight)

  /**
    * Get the x position of HID.
    *
    * @param hand which hand, left or right
    * @return the x position
    */
  def getX(hand: GenericHID.Hand): Double

  /**
    * Get the y position of the HID.
    *
    * @return the y position
    */
  final def getY(): Double = getY(GenericHID.Hand.kRight)

  /**
    * Get the y position of the HID.
    *
    * @param hand which hand, left or right
    * @return the y position
    */
  def getY(hand: GenericHID.Hand): Double

  /**
    * Get the button value (starting at button 1).
    *
    * <p>The buttons are returned in a single 16 bit value with one bit representing the state of
    * each button. The appropriate button is returned as a boolean value.
    *
    * @param button The button number to be read (starting at 1)
    * @return The state of the button.
    */
  def getRawButton(button: Int): Boolean = m_ds.getStickButton(m_port, button.toByte)

  /**
    * Whether the button was pressed since the last check. Button indexes begin at
    * 1.
    *
    * @param button The button index, beginning at 1.
    * @return Whether the button was pressed since the last check.
    */
  def getRawButtonPressed(button: Int): Boolean = m_ds.getStickButtonPressed(m_port, button.toByte)

  /**
    * Whether the button was released since the last check. Button indexes begin at
    * 1.
    *
    * @param button The button index, beginning at 1.
    * @return Whether the button was released since the last check.
    */
  def getRawButtonReleased(button: Int): Boolean = m_ds.getStickButtonReleased(m_port, button)

  /**
    * Get the value of the axis.
    *
    * @param axis The axis to read, starting at 0.
    * @return The value of the axis.
    */
  def getRawAxis(axis: Int): Double = m_ds.getStickAxis(m_port, axis)

  /**
    * Get the angle in degrees of a POV on the HID.
    *
    * <p>The POV angles start at 0 in the up direction, and increase clockwise (eg right is 90,
    * upper-left is 315).
    *
    * @param pov The index of the POV to read (starting at 0)
    * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
    */
  def getPOV(pov: Int): Int = m_ds.getStickPOV(m_port, pov)

  def getPOV: Int = getPOV(0)

  /**
    * Get the number of axes for the HID.
    *
    * @return the number of axis for the current HID
    */
  def getAxisCount: Int = m_ds.getStickAxisCount(m_port)

  /**
    * For the current HID, return the number of POVs.
    */
  def getPOVCount: Int = m_ds.getStickPOVCount(m_port)

  /**
    * For the current HID, return the number of buttons.
    */
  def getButtonCount: Int = m_ds.getStickButtonCount(m_port)

  /**
    * Get the type of the HID.
    *
    * @return the type of the HID.
    */
  def getType = GenericHID.HIDType.values(m_ds.getJoystickType(m_port))

  /**
    * Get the name of the HID.
    *
    * @return the name of the HID.
    */
  def getName: String = m_ds.getJoystickName(m_port)

  /**
    * Get the axis type of a joystick axis.
    *
    * @return the axis type of a joystick axis.
    */
  def getAxisType(axis: Int): Int = m_ds.getJoystickAxisType(m_port, axis)

  /**
    * Get the port number of the HID.
    *
    * @return The port number of the HID.
    */
  def getPort: Int = m_port

  /**
    * Set a single HID output value for the HID.
    *
    * @param outputNumber The index of the output to set (1-32)
    * @param value        The value to set the output to
    */
  def setOutput(outputNumber: Int, value: Boolean): Unit = {
    m_outputs = (m_outputs & ~(1 << (outputNumber - 1))) | ((if (value) 1
    else 0) << (outputNumber - 1))
    HAL.setJoystickOutputs(m_port.toByte, m_outputs, m_leftRumble, m_rightRumble)
  }

  /**
    * Set all HID output values for the HID.
    *
    * @param value The 32 bit output value (1 bit for each output)
    */
  def setOutputs(value: Int): Unit = {
    m_outputs = value
    HAL.setJoystickOutputs(m_port.toByte, m_outputs, m_leftRumble, m_rightRumble)
  }

  /**
    * Set the rumble output for the HID. The DS currently supports 2 rumble values, left rumble and
    * right rumble.
    *
    * @param type  Which rumble value to set
    * @param value The normalized value (0 to 1) to set the rumble to
    */
  def setRumble(`type`: GenericHID.RumbleType, _value: Double): Unit = {
    var value = _value
    if (value < 0) value = 0
    else if (value > 1) value = 1
    if (`type` eq GenericHID.RumbleType.kLeftRumble) m_leftRumble = (value * 65535).toShort
    else m_rightRumble = (value * 65535).toShort
    HAL.setJoystickOutputs(m_port.toByte, m_outputs, m_leftRumble, m_rightRumble)
  }
}

/**
  * GenericHID Interface.
  */
object GenericHID {
  type RumbleType = RumbleType.Value

  /**
    * Represents a rumble output on the JoyStick.
    */
  object RumbleType extends Enumeration {
    val kLeftRumble, kRightRumble = Value
  }

  final class HIDType(val value: Int)

  object HIDType {
    val kUnknown = new HIDType(-1)
    val kXInputUnknown = new HIDType(0)
    val kXInputGamepad = new HIDType(1)
    val kXInputWheel = new HIDType(2)
    val kXInputArcadeStick = new HIDType(3)
    val kXInputFlightStick = new HIDType(4)
    val kXInputDancePad = new HIDType(5)
    val kXInputGuitar = new HIDType(6)
    val kXInputGuitar2 = new HIDType(7)
    val kXInputDrumKit = new HIDType(8)
    val kXInputGuitar3 = new HIDType(11)
    val kXInputArcadePad = new HIDType(19)
    val kHIDJoystick = new HIDType(20)
    val kHIDGamepad = new HIDType(21)
    val kHIDDriving = new HIDType(22)
    val kHIDFlight = new HIDType(23)
    val kHID1stPerson = new HIDType(24)

    val values = List(
      kUnknown, kXInputUnknown, kXInputGamepad, kXInputWheel, kXInputArcadeStick,
      kXInputFlightStick, kXInputDancePad, kXInputGuitar, kXInputGuitar2, kXInputDrumKit,
      kXInputGuitar3, kXInputArcadePad, kHIDJoystick, kHIDGamepad, kHIDDriving, kHIDFlight, kHID1stPerson
    ).map(v => v.value -> v).toMap
  }

  final class Hand(val value: Int)

  /**
    * Which hand the Human Interface Device is associated with.
    */
  object Hand {
    val kLeft = new Hand(0)
    val kRight = new Hand(1)
  }
}