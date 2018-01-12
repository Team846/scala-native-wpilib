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
import edu.wpi.first.wpilibj.util.AllocationException

/**
  * Construct an analog channel.
  *
  * @param channel The channel number to represent. 0-3 are on-board 4-7 are on the MXP port.
  */
class AnalogInput(var m_channel: Int) /*extends PIDSource with Sendable */ {
  import SensorBase._

  private[wpilibj] var m_port = 0 // explicit no modifier, private and package accessible.

  private var m_accumulatorOffset = 0L
  //protected var m_pidSource: PIDSourceType = PIDSourceType.kDisplacement

  checkAnalogInputChannel(m_channel)
  val portHandle: Int = AnalogJNI.getPort(m_channel.toByte)
  m_port = AnalogJNI.initializeAnalogInputPort(portHandle)
  HAL.report(tResourceType.kResourceType_AnalogChannel, m_channel)
  //setName("AnalogInput", m_channel)

  /**
    * Channel destructor.
    */
  def free(): Unit = {
    //super.free
    AnalogJNI.freeAnalogInputPort(m_port)
    m_port = 0
    m_channel = 0
    m_accumulatorOffset = 0
  }

  /**
    * Get a sample straight from this channel. The sample is a 12-bit value representing the 0V to 5V
    * range of the A/D converter. The units are in A/D converter codes. Use GetVoltage() to get the
    * analog value in calibrated units.
    *
    * @return A sample straight from this channel.
    */
  def getValue: Int = AnalogJNI.getAnalogValue(m_port)

  /**
    * Get a sample from the output of the oversample and average engine for this channel. The sample
    * is 12-bit + the bits configured in SetOversampleBits(). The value configured in
    * setAverageBits() will cause this value to be averaged 2^bits number of samples. This is not a
    * sliding window. The sample will not change until 2^(OversampleBits + AverageBits) samples have
    * been acquired from this channel. Use getAverageVoltage() to get the analog value in calibrated
    * units.
    *
    * @return A sample from the oversample and average engine for this channel.
    */
  def getAverageValue: Int = AnalogJNI.getAnalogAverageValue(m_port)

  /**
    * Get a scaled sample straight from this channel. The value is scaled to units of Volts using the
    * calibrated scaling data from getLSBWeight() and getOffset().
    *
    * @return A scaled sample straight from this channel.
    */
  def getVoltage: Double = AnalogJNI.getAnalogVoltage(m_port)

  /**
    * Get a scaled sample from the output of the oversample and average engine for this channel. The
    * value is scaled to units of Volts using the calibrated scaling data from getLSBWeight() and
    * getOffset(). Using oversampling will cause this value to be higher resolution, but it will
    * update more slowly. Using averaging will cause this value to be more stable, but it will update
    * more slowly.
    *
    * @return A scaled sample from the output of the oversample and average engine for this channel.
    */
  def getAverageVoltage: Double = AnalogJNI.getAnalogAverageVoltage(m_port)

  /**
    * Get the factory scaling least significant bit weight constant. The least significant bit weight
    * constant for the channel that was calibrated in manufacturing and stored in an eeprom.
    *
    * <p>Volts = ((LSB_Weight * 1e-9) * raw) - (Offset * 1e-9)
    *
    * @return Least significant bit weight.
    */
  def getLSBWeight: Long = AnalogJNI.getAnalogLSBWeight(m_port)

  /**
    * Get the factory scaling offset constant. The offset constant for the channel that was
    * calibrated in manufacturing and stored in an eeprom.
    *
    * <p>Volts = ((LSB_Weight * 1e-9) * raw) - (Offset * 1e-9)
    *
    * @return Offset constant.
    */
  def getOffset: Int = AnalogJNI.getAnalogOffset(m_port)

  /**
    * Get the channel number.
    *
    * @return The channel number.
    */
  def getChannel: Int = m_channel

  /**
    * Set the number of averaging bits. This sets the number of averaging bits. The actual number of
    * averaged samples is 2^bits. The averaging is done automatically in the FPGA.
    *
    *
    * @param bits The number of averaging bits.
    */
  def setAverageBits(bits: Int): Unit = {
    AnalogJNI.setAnalogAverageBits(m_port, bits)
  }

  /**
    * Get the number of averaging bits. This gets the number of averaging bits from the FPGA. The
    * actual number of averaged samples is 2^bits. The averaging is done automatically in the FPGA.
    *
    *
    * @return The number of averaging bits.
    */
  def getAverageBits: Int = AnalogJNI.getAnalogAverageBits(m_port)

  /**
    * Set the number of oversample bits. This sets the number of oversample bits. The actual number
    * of oversampled values is 2^bits. The oversampling is done automatically in the FPGA.
    *
    *
    * @param bits The number of oversample bits.
    */
  def setOversampleBits(bits: Int): Unit = {
    AnalogJNI.setAnalogOversampleBits(m_port, bits)
  }

  /**
    * Get the number of oversample bits. This gets the number of oversample bits from the FPGA. The
    * actual number of oversampled values is 2^bits. The oversampling is done automatically in the
    * FPGA.
    *
    *
    * @return The number of oversample bits.
    */
  def getOversampleBits: Int = AnalogJNI.getAnalogOversampleBits(m_port)

  /**
    * Initialize the accumulator.
    */
  def initAccumulator(): Unit = {
    if (!isAccumulatorChannel) throw new AllocationException("Accumulators are only available on slot " + AnalogInput.kAccumulatorSlot + " on channels " + AnalogInput.kAccumulatorChannels(0) + ", " + AnalogInput.kAccumulatorChannels(1))
    m_accumulatorOffset = 0
    AnalogJNI.initAccumulator(m_port)
  }

  /**
    * Set an initial value for the accumulator.
    *
    * <p>This will be added to all values returned to the user.
    *
    * @param initialValue The value that the accumulator should start from when reset.
    */
  def setAccumulatorInitialValue(initialValue: Long): Unit = {
    m_accumulatorOffset = initialValue
  }

  /**
    * Resets the accumulator to the initial value.
    */
  def resetAccumulator(): Unit = {
    AnalogJNI.resetAccumulator(m_port)
    // Wait until the next sample, so the next call to getAccumulator*()
    // won't have old values.
    val sampleTime = 1.0 / AnalogInput.getGlobalSampleRate
    val overSamples = 1 << getOversampleBits
    val averageSamples = 1 << getAverageBits
    Timer.delay(sampleTime * overSamples * averageSamples)
  }

  /**
    * Set the center value of the accumulator.
    *
    * <p>The center value is subtracted from each A/D value before it is added to the accumulator.
    * This is used for the center value of devices like gyros and accelerometers to take the device
    * offset into account when integrating.
    *
    * <p>This center value is based on the output of the oversampled and averaged source the
    * accumulator channel. Because of this, any non-zero oversample bits will affect the size of the
    * value for this field.
    */
  def setAccumulatorCenter(center: Int): Unit = {
    AnalogJNI.setAccumulatorCenter(m_port, center)
  }

  /**
    * Set the accumulator's deadband.
    *
    * @param deadband The deadband size in ADC codes (12-bit value)
    */
  def setAccumulatorDeadband(deadband: Int): Unit = {
    AnalogJNI.setAccumulatorDeadband(m_port, deadband)
  }

  /**
    * Read the accumulated value.
    *
    * <p>Read the value that has been accumulating. The accumulator is attached after the oversample
    * and average engine.
    *
    * @return The 64-bit value accumulated since the last Reset().
    */
  def getAccumulatorValue: Long = AnalogJNI.getAccumulatorValue(m_port) + m_accumulatorOffset

  /**
    * Read the number of accumulated values.
    *
    * <p>Read the count of the accumulated values since the accumulator was last Reset().
    *
    * @return The number of times samples from the channel were accumulated.
    */
  def getAccumulatorCount: Long = AnalogJNI.getAccumulatorCount(m_port)

  /**
    * Read the accumulated value and the number of accumulated values atomically.
    *
    * <p>This function reads the value and count from the FPGA atomically. This can be used for
    * averaging.
    *
    * @param result AccumulatorResult object to store the results in.
    */
  def getAccumulatorOutput(result: AccumulatorResult): Unit = {
    if (result == null) throw new IllegalArgumentException("Null parameter `result'")
    if (!isAccumulatorChannel) throw new IllegalArgumentException("Channel " + m_channel + " is not an accumulator channel.")
    AnalogJNI.getAccumulatorOutput(m_port, result)
    result.value += m_accumulatorOffset
  }

  /**
    * Is the channel attached to an accumulator.
    *
    * @return The analog channel is attached to an accumulator.
    */
  def isAccumulatorChannel: Boolean = {
    for (channel <- AnalogInput.kAccumulatorChannels) {
      if (m_channel == channel) return true
    }
    false
  }

//  def setPIDSourceType(pidSource: PIDSourceType): Unit = {
//    m_pidSource = pidSource
//  }

//  def getPIDSourceType: PIDSourceType = m_pidSource

  /**
    * Get the average voltage for use with PIDController.
    *
    * @return the average voltage
    */
  def pidGet: Double = getAverageVoltage

//  def initSendable(builder: Nothing): Unit = {
//    builder.setSmartDashboardType("Analog Input")
//    builder.addDoubleProperty("Value", this.getAverageVoltage, null)
//  }
}

/**
  * Analog channel class.
  *
  * <p>Each analog channel is read from hardware as a 12-bit number representing 0V to 5V.
  *
  * <p>Connected to each analog channel is an averaging and oversampling engine. This engine
  * accumulates the specified ( by setAverageBits() and setOversampleBits() ) number of samples
  * before returning a new value. This is not a sliding window average. The only difference between
  * the oversampled samples and the averaged samples is that the oversampled samples are simply
  * accumulated effectively increasing the resolution, while the averaged samples are divided by the
  * number of samples to retain the resolution, but get more stable values.
  */
object AnalogInput {
  private val kAccumulatorSlot = 1
  private val kAccumulatorChannels = Array(0, 1)

  /**
    * Set the sample rate per channel.
    *
    * <p>This is a global setting for all channels. The maximum rate is 500kS/s divided by the number
    * of channels in use. This is 62500 samples/s per channel if all 8 channels are used.
    *
    * @param samplesPerSecond The number of samples per second.
    */
  def setGlobalSampleRate(samplesPerSecond: Double): Unit = {
    AnalogJNI.setAnalogSampleRate(samplesPerSecond)
  }

  /**
    * Get the current sample rate.
    *
    * <p>This assumes one entry in the scan list. This is a global setting for all channels.
    *
    * @return Sample rate.
    */
  def getGlobalSampleRate: Double = AnalogJNI.getAnalogSampleRate
}
