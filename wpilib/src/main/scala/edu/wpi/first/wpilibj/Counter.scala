package edu.wpi.first.wpilibj

import java.nio.ByteBuffer
import java.nio.ByteOrder
import edu.wpi.first.wpilibj.AnalogTriggerOutput.AnalogTriggerType
import edu.wpi.first.wpilibj.hal.CounterJNI
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
//import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import java.util.Objects.requireNonNull

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/**
  * Create an instance of a counter with the given mode.
  */
class Counter(val mode: Counter.Mode) extends CounterBase /*with Sendable with PIDSource*/ {
  protected var m_upSource: DigitalSource = null // /< What makes the counter count up.

  protected var m_downSource: DigitalSource = null // /< What makes the counter count down.

  private var m_allocatedUpSource = false
  private var m_allocatedDownSource = false
  private var m_counter = 0 // /< The FPGA counter object.

  private var m_index = 0 // /< The index of this counter.

  private var m_pidSource = null
  private var m_distancePerPulse = .0 // distance of travel for each tick

  val index: ByteBuffer = ByteBuffer.allocateDirect(4)
  // set the byte order
  index.order(ByteOrder.LITTLE_ENDIAN)
  m_counter = CounterJNI.initializeCounter(mode.value, index.asIntBuffer)
  m_index = index.asIntBuffer.get(0)
  m_allocatedUpSource = false
  m_allocatedDownSource = false
  m_upSource = null
  m_downSource = null
  setMaxPeriod(.5)
  HAL.report(tResourceType.kResourceType_Counter, m_index, mode.value)
  //  setName("Counter", m_index)

  /**
    * Create an instance of a counter where no sources are selected. Then they all must be selected
    * by calling functions to specify the upsource and the downsource independently.
    *
    * <p>The counter will start counting immediately.
    */
  def this() {
    this(Counter.Mode.kTwoPulse)
  }

  /**
    * Create an instance of a counter from a Digital Input. This is used if an existing digital input
    * is to be shared by multiple other objects such as encoders or if the Digital Source is not a
    * DIO channel (such as an Analog Trigger)
    *
    * <p>The counter will start counting immediately.
    *
    * @param source the digital source to count
    */
  def this(source: DigitalSource) {
    this()
    requireNonNull(source, "Digital Source given was null")
    setUpSource(source)
  }

  /**
    * Create an instance of a Counter object. Create an up-Counter instance given a channel.
    *
    * <p>The counter will start counting immediately.
    *
    * @param channel the DIO channel to use as the up source. 0-9 are on-board, 10-25 are on the MXP
    */
  def this(channel: Int) {
    this()
    setUpSource(channel)
  }

  /**
    * Create an instance of a Counter object. Create an instance of a simple up-Counter given an
    * analog trigger. Use the trigger state output from the analog trigger.
    *
    * <p>The counter will start counting immediately.
    *
    * @param encodingType which edges to count
    * @param upSource     first source to count
    * @param downSource   second source for direction
    * @param inverted     true to invert the count
    */
  def this(encodingType: CounterBase.EncodingType, upSource: DigitalSource, downSource: DigitalSource, inverted: Boolean) {
    this(Counter.Mode.kExternalDirection)
    requireNonNull(encodingType, "Encoding type given was null")
    requireNonNull(upSource, "Up Source given was null")
    requireNonNull(downSource, "Down Source given was null")
    if ((encodingType != CounterBase.EncodingType.k1X) && (encodingType != CounterBase.EncodingType.k2X)) throw new RuntimeException("Counters only support 1X and 2X quadrature decoding!")
    setUpSource(upSource)
    setDownSource(downSource)
    if (encodingType == CounterBase.EncodingType.k1X) {
      setUpSourceEdge(true, false)
      CounterJNI.setCounterAverageSize(m_counter, 1)
    }
    else {
      setUpSourceEdge(true, true)
      CounterJNI.setCounterAverageSize(m_counter, 2)
    }
    setDownSourceEdge(inverted, true)
  }

  /**
    * Create an instance of a Counter object. Create an instance of a simple up-Counter given an
    * analog trigger. Use the trigger state output from the analog trigger.
    *
    * <p>The counter will start counting immediately.
    *
    * @param trigger the analog trigger to count
    */
  def this(trigger: AnalogTrigger) {
    this()
    requireNonNull(trigger, "The Analog Trigger given was null")
    setUpSource(trigger.createOutput(AnalogTriggerType.kState))
  }

  def free(): Unit = {
//    super.free()
    setUpdateWhenEmpty(true)
    clearUpSource()
    clearDownSource()
    CounterJNI.freeCounter(m_counter)
    m_upSource = null
    m_downSource = null
    m_counter = 0
  }

  /**
    * The counter's FPGA index.
    *
    * @return the Counter's FPGA index
    */
  @SuppressWarnings(Array("AbbreviationAsWordInName")) def getFPGAIndex: Int = m_index

  /**
    * Set the upsource for the counter as a digital input channel.
    *
    * @param channel the DIO channel to count 0-9 are on-board, 10-25 are on the MXP
    */
  def setUpSource(channel: Int): Unit = {
    setUpSource(new DigitalInput(channel))
    m_allocatedUpSource = true
//    addChild(m_upSource)
  }

  /**
    * Set the source object that causes the counter to count up. Set the up counting DigitalSource.
    *
    * @param source the digital source to count
    */
  def setUpSource(source: DigitalSource): Unit = {
    if (m_upSource != null && m_allocatedUpSource) {
      m_upSource.free()
      m_allocatedUpSource = false
    }
    m_upSource = source
    CounterJNI.setCounterUpSource(m_counter, source.getPortHandleForRouting, source.getAnalogTriggerTypeForRouting)
  }

  /**
    * Set the up counting source to be an analog trigger.
    *
    * @param analogTrigger The analog trigger object that is used for the Up Source
    * @param triggerType   The analog trigger output that will trigger the counter.
    */
  def setUpSource(analogTrigger: AnalogTrigger, triggerType: AnalogTriggerOutput.AnalogTriggerType): Unit = {
    requireNonNull(analogTrigger, "Analog Trigger given was null")
    requireNonNull(triggerType, "Analog Trigger Type given was null")
    setUpSource(analogTrigger.createOutput(triggerType))
    m_allocatedUpSource = true
  }

  /**
    * Set the edge sensitivity on an up counting source. Set the up source to either detect rising
    * edges or falling edges.
    *
    * @param risingEdge  true to count rising edge
    * @param fallingEdge true to count falling edge
    */
  def setUpSourceEdge(risingEdge: Boolean, fallingEdge: Boolean): Unit = {
    if (m_upSource == null) throw new RuntimeException("Up Source must be set before setting the edge!")
    CounterJNI.setCounterUpSourceEdge(m_counter, risingEdge, fallingEdge)
  }

  /**
    * Disable the up counting source to the counter.
    */
  def clearUpSource(): Unit = {
    if (m_upSource != null && m_allocatedUpSource) {
      m_upSource.free()
      m_allocatedUpSource = false
    }
    m_upSource = null
    CounterJNI.clearCounterUpSource(m_counter)
  }

  /**
    * Set the down counting source to be a digital input channel.
    *
    * @param channel the DIO channel to count 0-9 are on-board, 10-25 are on the MXP
    */
  def setDownSource(channel: Int): Unit = {
    setDownSource(new DigitalInput(channel))
    m_allocatedDownSource = true
//    addChild(m_downSource)
  }

  /**
    * Set the source object that causes the counter to count down. Set the down counting
    * DigitalSource.
    *
    * @param source the digital source to count
    */
  def setDownSource(source: DigitalSource): Unit = {
    requireNonNull(source, "The Digital Source given was null")
    if (m_downSource != null && m_allocatedDownSource) {
      m_downSource.free()
      m_allocatedDownSource = false
    }
    CounterJNI.setCounterDownSource(m_counter, source.getPortHandleForRouting, source.getAnalogTriggerTypeForRouting)
    m_downSource = source
  }

  /**
    * Set the down counting source to be an analog trigger.
    *
    * @param analogTrigger The analog trigger object that is used for the Down Source
    * @param triggerType   The analog trigger output that will trigger the counter.
    */
  def setDownSource(analogTrigger: AnalogTrigger, triggerType: AnalogTriggerOutput.AnalogTriggerType): Unit = {
    requireNonNull(analogTrigger, "Analog Trigger given was null")
    requireNonNull(triggerType, "Analog Trigger Type given was null")
    setDownSource(analogTrigger.createOutput(triggerType))
    m_allocatedDownSource = true
  }

  /**
    * Set the edge sensitivity on a down counting source. Set the down source to either detect rising
    * edges or falling edges.
    *
    * @param risingEdge  true to count the rising edge
    * @param fallingEdge true to count the falling edge
    */
  def setDownSourceEdge(risingEdge: Boolean, fallingEdge: Boolean): Unit = {
    requireNonNull(m_downSource, "Down Source must be set before setting the edge!")
    CounterJNI.setCounterDownSourceEdge(m_counter, risingEdge, fallingEdge)
  }

  /**
    * Disable the down counting source to the counter.
    */
  def clearDownSource(): Unit = {
    if (m_downSource != null && m_allocatedDownSource) {
      m_downSource.free()
      m_allocatedDownSource = false
    }
    m_downSource = null
    CounterJNI.clearCounterDownSource(m_counter)
  }

  /**
    * Set standard up / down counting mode on this counter. Up and down counts are sourced
    * independently from two inputs.
    */
  def setUpDownCounterMode(): Unit = {
    CounterJNI.setCounterUpDownMode(m_counter)
  }

  /**
    * Set external direction mode on this counter. Counts are sourced on the Up counter input. The
    * Down counter input represents the direction to count.
    */
  def setExternalDirectionMode(): Unit = {
    CounterJNI.setCounterExternalDirectionMode(m_counter)
  }

  /**
    * Set Semi-period mode on this counter. Counts up on both rising and falling edges.
    *
    * @param highSemiPeriod true to count up on both rising and falling
    */
  def setSemiPeriodMode(highSemiPeriod: Boolean): Unit = {
    CounterJNI.setCounterSemiPeriodMode(m_counter, highSemiPeriod)
  }

  /**
    * Configure the counter to count in up or down based on the length of the input pulse. This mode
    * is most useful for direction sensitive gear tooth sensors.
    *
    * @param threshold The pulse length beyond which the counter counts the opposite direction. Units
    *                  are seconds.
    */
  def setPulseLengthMode(threshold: Double): Unit = {
    CounterJNI.setCounterPulseLengthMode(m_counter, threshold)
  }

  /**
    * Read the current counter value. Read the value at this instant. It may still be running, so it
    * reflects the current value. Next time it is read, it might have a different value.
    */
  def get: Int = CounterJNI.getCounter(m_counter)

  /**
    * Read the current scaled counter value. Read the value at this instant, scaled by the distance
    * per pulse (defaults to 1).
    *
    * @return The distance since the last reset
    */
  def getDistance: Double = get * m_distancePerPulse

  /**
    * Reset the Counter to zero. Set the counter value to zero. This doesn't effect the running state
    * of the counter, just sets the current value to zero.
    */
  def reset(): Unit = {
    CounterJNI.resetCounter(m_counter)
  }

  /**
    * Set the maximum period where the device is still considered "moving". Sets the maximum period
    * where the device is considered moving. This value is used to determine the "stopped" state of
    * the counter using the GetStopped method.
    *
    * @param maxPeriod The maximum period where the counted device is considered moving in seconds.
    */
  def setMaxPeriod(maxPeriod: Double): Unit = {
    CounterJNI.setCounterMaxPeriod(m_counter, maxPeriod)
  }

  /**
    * Select whether you want to continue updating the event timer output when there are no samples
    * captured. The output of the event timer has a buffer of periods that are averaged and posted to
    * a register on the FPGA. When the timer detects that the event source has stopped (based on the
    * MaxPeriod) the buffer of samples to be averaged is emptied. If you enable the update when
    * empty, you will be notified of the stopped source and the event time will report 0 samples. If
    * you disable update when empty, the most recent average will remain on the output until a new
    * sample is acquired. You will never see 0 samples output (except when there have been no events
    * since an FPGA reset) and you will likely not see the stopped bit become true (since it is
    * updated at the end of an average and there are no samples to average).
    *
    * @param enabled true to continue updating
    */
  def setUpdateWhenEmpty(enabled: Boolean): Unit = {
    CounterJNI.setCounterUpdateWhenEmpty(m_counter, enabled)
  }

  /**
    * Determine if the clock is stopped. Determine if the clocked input is stopped based on the
    * MaxPeriod value set using the SetMaxPeriod method. If the clock exceeds the MaxPeriod, then the
    * device (and counter) are assumed to be stopped and it returns true.
    *
    * @return true if the most recent counter period exceeds the MaxPeriod value set by SetMaxPeriod.
    */
  def getStopped: Boolean = CounterJNI.getCounterStopped(m_counter)

  /**
    * The last direction the counter value changed.
    *
    * @return The last direction the counter value changed.
    */
  def getDirection: Boolean = CounterJNI.getCounterDirection(m_counter)

  /**
    * Set the Counter to return reversed sensing on the direction. This allows counters to change the
    * direction they are counting in the case of 1X and 2X quadrature encoding only. Any other
    * counter mode isn't supported.
    *
    * @param reverseDirection true if the value counted should be negated.
    */
  def setReverseDirection(reverseDirection: Boolean): Unit = {
    CounterJNI.setCounterReverseDirection(m_counter, reverseDirection)
  }

  /**
    * Get the Period of the most recent count. Returns the time interval of the most recent count.
    * This can be used for velocity calculations to determine shaft speed.
    *
    * @return The period of the last two pulses in units of seconds.
    */
  def getPeriod: Double = CounterJNI.getCounterPeriod(m_counter)

  /**
    * Get the current rate of the Counter. Read the current rate of the counter accounting for the
    * distance per pulse value. The default value for distance per pulse (1) yields units of pulses
    * per second.
    *
    * @return The rate in units/sec
    */
  def getRate: Double = m_distancePerPulse / getPeriod

  /**
    * Set the Samples to Average which specifies the number of samples of the timer to average when
    * calculating the period. Perform averaging to account for mechanical imperfections or as
    * oversampling to increase resolution.
    *
    * @param samplesToAverage The number of samples to average from 1 to 127.
    */
  def setSamplesToAverage(samplesToAverage: Int): Unit = {
    CounterJNI.setCounterSamplesToAverage(m_counter, samplesToAverage)
  }

  /**
    * Get the Samples to Average which specifies the number of samples of the timer to average when
    * calculating the period. Perform averaging to account for mechanical imperfections or as
    * oversampling to increase resolution.
    *
    * @return SamplesToAverage The number of samples being averaged (from 1 to 127)
    */
  def getSamplesToAverage: Int = CounterJNI.getCounterSamplesToAverage(m_counter)

  /**
    * Set the distance per pulse for this counter. This sets the multiplier used to determine the
    * distance driven based on the count value from the encoder. Set this value based on the Pulses
    * per Revolution and factor in any gearing reductions. This distance can be in any units you
    * like, linear or angular.
    *
    * @param distancePerPulse The scale factor that will be used to convert pulses to useful units.
    */
  def setDistancePerPulse(distancePerPulse: Double): Unit = {
    m_distancePerPulse = distancePerPulse
  }

//  /**
//    * Set which parameter of the encoder you are using as a process control variable. The counter
//    * class supports the rate and distance parameters.
//    *
//    * @param pidSource An enum to select the parameter.
//    */
//  def setPIDSourceType(pidSource: Nothing): Unit = {
//    requireNonNull(pidSource, "PID Source Parameter given was null")
//    if ((pidSource ne PIDSourceType.kDisplacement) && (pidSource ne PIDSourceType.kRate)) throw new IllegalArgumentException("PID Source parameter was not valid type: " + pidSource)
//    m_pidSource = pidSource
//  }
//
//  def getPIDSourceType: Nothing = m_pidSource
//
//  def pidGet: Double = m_pidSource match {
//    case kDisplacement =>
//      getDistance
//    case kRate =>
//      getRate
//    case _ =>
//      0.0
//  }

//  def initSendable(builder: Nothing): Unit = {
//    builder.setSmartDashboardType("Counter")
//    builder.addDoubleProperty("Value", this.get, null)
//  }
}

/**
  * Class for counting the number of ticks on a digital input channel.
  *
  * <p>This is a general purpose class for counting repetitive events. It can return the number of
  * counts, the period of the most recent cycle, and detect when the signal being counted has
  * stopped by supplying a maximum cycle time.
  *
  * <p>All counters will immediately start counting - reset() them if you need them to be zeroed
  * before use.
  */
object Counter {
  final class Mode(val value: Int)
  /**
    * Mode determines how and what the counter counts.
    */
  object Mode {
    /**
      * mode: two pulse.
      */
    val kTwoPulse = new Mode(0)

    /**
      * mode: semi period.
      */
    val kSemiperiod = new Mode(1)

    /**
      * mode: pulse length.
      */
    val kPulseLength = new Mode(2)

    /**
      * mode: external direction.
      */
    val kExternalDirection = new Mode(3)
  }
}