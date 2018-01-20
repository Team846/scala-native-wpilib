/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.AnalogJNI
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import java.util.Objects.requireNonNull

/**
  * Create an object that represents one of the four outputs from an analog trigger.
  *
  * <p>Because this class derives from DigitalSource, it can be passed into routing functions for
  * Counter, Encoder, etc.
  *
  * @param trigger    The trigger for which this is an output.
  * @param outputType An enum that specifies the output on the trigger to represent.
  */
class AnalogTriggerOutput(val m_trigger: AnalogTrigger, val m_outputType: AnalogTriggerOutput.AnalogTriggerType) extends DigitalSource {
  requireNonNull(m_trigger, "Analog Trigger given was null")
  requireNonNull(m_outputType, "Analog Trigger Type given was null")
  HAL.report(tResourceType.kResourceType_AnalogTriggerOutput, m_trigger.getIndex, m_outputType.value)

  /**
    * Exceptions dealing with improper operation of the Analog trigger output.
    *
    * Create a new exception with the given message.
    *
    * @param message the message to pass with the exception
    */
  class AnalogTriggerOutputException(val message: String) extends RuntimeException(message) {
  }

  /**
    * Get the state of the analog trigger output.
    *
    * @return The state of the analog trigger output.
    */
  def get: Boolean = AnalogJNI.getAnalogTriggerOutput(m_trigger.m_port, m_outputType.value)

  override def getPortHandleForRouting: Int = m_trigger.m_port

  override def getAnalogTriggerTypeForRouting: Int = m_outputType.value

  override def getChannel: Int = m_trigger.m_index

  override def isAnalogTrigger = true

//  def initSendable(builder: SendableBuilder): Unit = {
//  }
}

/**
  * Class to represent a specific output from an analog trigger. This class is used to get the
  * current output value and also as a DigitalSource to provide routing of an output to digital
  * subsystems on the FPGA such as Counter, Encoder, and Interrupt.
  *
  * <p>The TriggerState output indicates the primary output value of the trigger. If the analog
  * signal is less than the lower limit, the output is false. If the analog value is greater than the
  * upper limit, then the output is true. If the analog value is in between, then the trigger output
  * state maintains its most recent value.
  *
  * <p>The InWindow output indicates whether or not the analog signal is inside the range defined by
  * the limits.
  *
  * <p>The RisingPulse and FallingPulse outputs detect an instantaneous transition from above the
  * upper limit to below the lower limit, and vise versa. These pulses represent a rollover condition
  * of a sensor and can be routed to an up / down counter or to interrupts. Because the outputs
  * generate a pulse, they cannot be read directly. To help ensure that a rollover condition is not
  * missed, there is an average rejection filter available that operates on the upper 8 bits of a 12
  * bit number and selects the nearest outlier of 3 samples. This will reject a sample that is (due
  * to averaging or sampling) errantly between the two limits. This filter will fail if more than one
  * sample in a row is errantly in between the two limits. You may see this problem if attempting to
  * use this feature with a mechanical rollover sensor, such as a 360 degree no-stop potentiometer
  * without signal conditioning, because the rollover transition is not sharp / clean enough. Using
  * the averaging engine may help with this, but rotational speeds of the sensor will then be
  * limited.
  */
object AnalogTriggerOutput {
  /**
    * Defines the state in which the AnalogTrigger triggers.
    */
  final class AnalogTriggerType(val value: Int)
  object AnalogTriggerType {
    val kInWindow = new AnalogTriggerType(AnalogJNI.AnalogTriggerType.kInWindow)
    val kState = new AnalogTriggerType(AnalogJNI.AnalogTriggerType.kState)
    val kRisingPulse = new AnalogTriggerType(AnalogJNI.AnalogTriggerType.kRisingPulse)
    val kFallingPulse = new AnalogTriggerType(AnalogJNI.AnalogTriggerType.kFallingPulse)
  }
}
