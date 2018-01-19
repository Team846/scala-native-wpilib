package com.ctre.phoenix.motorcontrol.can

import com.ctre.phoenix.motorcontrol.ControlFrame
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.Faults
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.IMotorController
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal
import com.ctre.phoenix.motorcontrol.LimitSwitchSource
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource
import com.ctre.phoenix.motorcontrol.RemoteSensorSource
import com.ctre.phoenix.motorcontrol.SensorCollection
import com.ctre.phoenix.motorcontrol.SensorTerm
import com.ctre.phoenix.motorcontrol.StatusFrame
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced
import com.ctre.phoenix.motorcontrol.StickyFaults
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI
import com.ctre.phoenix.ParamEnum
import com.ctre.phoenix.motion.MotionProfileStatus
import com.ctre.phoenix.motion.SetValueMotionProfile
import com.ctre.phoenix.motion.TrajectoryPoint
import com.ctre.phoenix.ErrorCode

/**
  * Base motor controller features for all CTRE CAN motor controllers.
  *
  * @param arbId
  */
abstract class BaseMotorController(val arbId: Int) extends IMotorController {
  private var m_controlMode = ControlMode.PercentOutput
  private var m_sendMode = ControlMode.PercentOutput
  private var _arbId = arbId
  private var _invert = false
  protected var m_handle = MotControllerJNI.Create(arbId)
  private val _motionProfStats = new Array[Int](11)
  private var _sensorColl = new SensorCollection(this)

  /**
    * @return CCI handle for child classes.
    */
  def getHandle: Long = m_handle

  /**
    * Returns the Device ID
    *
    * @return Device number.
    */
  override def getDeviceID: Int = MotControllerJNI.GetDeviceNumber(m_handle)

  /**
    * Sets the appropriate output on the talon, depending on the mode.
    *
    * @param mode        The output mode to apply.
    *                    In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
    *                    In Current mode, output value is in amperes.
    *                    In Velocity mode, output value is in position change / 100ms.
    *                    In Position mode, output value is in encoder ticks or an analog value,
    *                    depending on the sensor.
    *                    In Follower mode, the output value is the integer device ID of the talon to
    *                    duplicate.
    * @param outputValue The setpoint value, as described above.
    */
  override def set(mode: ControlMode, outputValue: Double): Unit = {
    set(mode, outputValue, 0)
  }

  /**
    * @param mode    Sets the appropriate output on the talon, depending on the mode.
    * @param demand0 The output value to apply.
    *                such as advanced feed forward and/or cascaded close-looping in firmware.
    *                In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
    *                In Current mode, output value is in amperes.
    *                In Velocity mode, output value is in position change / 100ms.
    *                In Position mode, output value is in encoder ticks or an analog value,
    *                depending on the sensor. See
    *                In Follower mode, the output value is the integer device ID of the talon to
    *                duplicate.
    * @param demand1 Supplemental value.  This will also be control mode specific for future features.
    */
  override def set(mode: ControlMode, demand0: Double, demand1: Double): Unit = {
    m_controlMode = mode
    m_sendMode = mode
    var work = 0
    import ControlMode._
    m_controlMode match {
      case PercentOutput =>
        // case TimedPercentOutput:
        MotControllerJNI.SetDemand(m_handle, m_sendMode.value, (1023 * demand0).toInt, 0)
      case Follower =>
        /* did caller specify device ID */
        if ((0 <= demand0) && (demand0 <= 62)) { // [0,62]
          work = getBaseID
          work >>= 16
          work <<= 8
          work |= demand0.toInt & 0xFF
        } else {
          work = demand0.toInt
        }
        MotControllerJNI.SetDemand(m_handle, m_sendMode.value, work, 0)
      case Velocity | Position | MotionMagic | MotionMagicArc | MotionProfile =>
        MotControllerJNI.SetDemand(m_handle, m_sendMode.value, demand0.toInt, 0)
      case Current =>
        MotControllerJNI.SetDemand(m_handle, m_sendMode.value, (1000.0 * demand0).toInt, 0) /* milliamps */
      //case Disabled =>
        /* fall thru... */
      case _ =>
        MotControllerJNI.SetDemand(m_handle, m_sendMode.value, 0, 0)
    }
  }

  /**
    * Neutral the motor output by setting control mode to disabled.
    */
  override def neutralOutput(): Unit = {
    set(ControlMode.Disabled, 0)
  }

  /**
    * Sets the mode of operation during neutral throttle output.
    *
    * @param neutralMode
    * The desired mode of operation when the Controller output
    * throttle is neutral (ie brake/coast)
    **/
  def setNeutralMode(neutralMode: NeutralMode): Unit = {
    MotControllerJNI.SetNeutralMode(m_handle, neutralMode.value)
  }

  /**
    * Enables a future feature called "Heading Hold".
    * For now this simply updates the CAN signal to the motor controller.
    * Future firmware updates will use this.
    *
    * @param enable true/false enable
    */
  def enableHeadingHold(enable: Boolean): Unit = {
    MotControllerJNI.EnableHeadingHold(m_handle, if (enable) 1
    else 0)
  }

  /**
    * For now this simply updates the CAN signal to the motor controller.
    * Future firmware updates will use this to control advanced cascaded loop behavior.
    *
    * @param value
    */
  def selectDemandType(value: Boolean): Unit = {
    MotControllerJNI.SelectDemandType(m_handle, if (value) 1
    else 0)
  }

  /**
    * Sets the phase of the sensor. Use when controller forward/reverse output
    * doesn't correlate to appropriate forward/reverse reading of sensor.
    * Pick a value so that positive PercentOutput yields a positive change in sensor.
    * After setting this, user can freely call SetInvert() with any value.
    *
    * @param PhaseSensor
    * Indicates whether to invert the phase of the sensor.
    */
  override def setSensorPhase(PhaseSensor: Boolean): Unit = {
    MotControllerJNI.SetSensorPhase(m_handle, PhaseSensor)
  }

  /**
    * Inverts the hbridge output of the motor controller.
    *
    * This does not impact sensor phase and should not be used to correct sensor polarity.
    *
    * This will invert the hbridge output but NOT the LEDs.
    * This ensures....
    *  - Green LEDs always represents positive request from robot-controller/closed-looping mode.
    *  - Green LEDs correlates to forward limit switch.
    *  - Green LEDs correlates to forward soft limit.
    *
    * @param invert
    * Invert state to set.
    */
  override def setInverted(invert: Boolean): Unit = {
    _invert = invert /* cache for getter */
    MotControllerJNI.SetInverted(m_handle, invert)
  }

  /**
    * @return invert setting of motor output.
    */
  override def getInverted: Boolean = _invert

  /**
    * Configures the open-loop ramp rate of throttle output.
    *
    * @param secondsFromNeutralToFull
    * Minimum desired time to go from neutral to full throttle. A
    * value of '0' will disable the ramp.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configOpenloopRamp(secondsFromNeutralToFull: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigOpenLoopRamp(m_handle, secondsFromNeutralToFull, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the closed-loop ramp rate of throttle output.
    *
    * @param secondsFromNeutralToFull
    * Minimum desired time to go from neutral to full throttle. A
    * value of '0' will disable the ramp.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configClosedloopRamp(secondsFromNeutralToFull: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigClosedLoopRamp(m_handle, secondsFromNeutralToFull, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the forward peak output percentage.
    *
    * @param percentOut
    * Desired peak output percentage. [0,1]
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configPeakOutputForward(percentOut: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigPeakOutputForward(m_handle, percentOut, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the reverse peak output percentage.
    *
    * @param percentOut
    * Desired peak output percentage.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configPeakOutputReverse(percentOut: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigPeakOutputReverse(m_handle, percentOut, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the forward nominal output percentage.
    *
    * @param percentOut
    * Nominal (minimum) percent output. [0,+1]
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configNominalOutputForward(percentOut: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigNominalOutputForward(m_handle, percentOut, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the reverse nominal output percentage.
    *
    * @param percentOut
    * Nominal (minimum) percent output. [-1,0]
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configNominalOutputReverse(percentOut: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigNominalOutputReverse(m_handle, percentOut, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the output deadband percentage.
    *
    * @param percentDeadband
    * Desired deadband percentage. Minimum is 0.1%, Maximum is 25%.
    * Pass 0.04 for 4% (factory default).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configNeutralDeadband(percentDeadband: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigNeutralDeadband(m_handle, percentDeadband, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the Voltage Compensation saturation voltage.
    *
    * @param voltage
    * This is the max voltage to apply to the hbridge when voltage
    * compensation is enabled.  For example, if 10 (volts) is specified
    * and a TalonSRX is commanded to 0.5 (PercentOutput, closed-loop, etc)
    * then the TalonSRX will attempt to apply a duty-cycle to produce 5V.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configVoltageCompSaturation(voltage: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigVoltageCompSaturation(m_handle, voltage, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the voltage measurement filter.
    *
    * @param filterWindowSamples
    * Number of samples in the rolling average of voltage
    * measurement.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configVoltageMeasurementFilter(filterWindowSamples: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigVoltageMeasurementFilter(m_handle, filterWindowSamples, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Enables voltage compensation. If enabled, voltage compensation works in
    * all control modes.
    *
    * @param enable
    * Enable state of voltage compensation.
    **/
  override def enableVoltageCompensation(enable: Boolean): Unit = {
    MotControllerJNI.EnableVoltageCompensation(m_handle, enable)
  }

  /**
    * Gets the bus voltage seen by the device.
    *
    * @return The bus voltage value (in volts).
    */
  override def getBusVoltage: Double = MotControllerJNI.GetBusVoltage(m_handle)

  /**
    * Gets the output percentage of the motor controller.
    *
    * @return Output of the motor controller (in percent).
    */
  override def getMotorOutputPercent: Double = MotControllerJNI.GetMotorOutputPercent(m_handle)

  /**
    * @return applied voltage to motor  in volts.
    */
  override def getMotorOutputVoltage: Double = getBusVoltage * getMotorOutputPercent

  /**
    * Gets the output current of the motor controller.
    *
    * @return The output current (in amps).
    */
  override def getOutputCurrent: Double = MotControllerJNI.GetOutputCurrent(m_handle)

  /**
    * Gets the temperature of the motor controller.
    *
    * @return Temperature of the motor controller (in 'C)
    */
  override def getTemperature: Double = MotControllerJNI.GetTemperature(m_handle)

  /**
    * Select the remote feedback device for the motor controller.
    * Most CTRE CAN motor controllers will support remote sensors over CAN.
    *
    * @param feedbackDevice
    * Remote Feedback Device to select.
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configSelectedFeedbackSensor(feedbackDevice: RemoteFeedbackDevice, pidIdx: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigSelectedFeedbackSensor(m_handle, feedbackDevice.value, pidIdx, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Select the feedback device for the motor controller.
    *
    * @param feedbackDevice
    * Feedback Device to select.
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configSelectedFeedbackSensor(feedbackDevice: FeedbackDevice, pidIdx: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigSelectedFeedbackSensor(m_handle, feedbackDevice.value, pidIdx, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Select what remote device and signal to assign to Remote Sensor 0 or Remote Sensor 1.
    * After binding a remote device and signal to Remote Sensor X, you may select Remote Sensor X
    * as a PID source for closed-loop features.
    *
    * @param deviceID
    * The CAN ID of the remote sensor device.
    * @param remoteSensorSource
    * The remote sensor device and signal type to bind.
    * @param remoteOrdinal
    * 0 for configuring Remote Sensor 0
    * 1 for configuring Remote Sensor 1
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configRemoteFeedbackFilter(deviceID: Int, remoteSensorSource: RemoteSensorSource, remoteOrdinal: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigRemoteFeedbackFilter(m_handle, deviceID, remoteSensorSource.value, remoteOrdinal, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Select what sensor term should be bound to switch feedback device.
    * Sensor Sum = Sensor Sum Term 0 - Sensor Sum Term 1
    * Sensor Difference = Sensor Diff Term 0 - Sensor Diff Term 1
    * The four terms are specified with this routine.  Then Sensor Sum/Difference
    * can be selected for closed-looping.
    *
    * @param sensorTerm     Which sensor term to bind to a feedback source.
    * @param feedbackDevice The sensor signal to attach to sensorTerm.
    * @param timeoutMs
    *                       Timeout value in ms. If nonzero, function will wait for
    *                       config success and report an error if it times out.
    *                       If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configSensorTerm(sensorTerm: SensorTerm, feedbackDevice: FeedbackDevice, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigSensorTerm(m_handle, sensorTerm.value, feedbackDevice.value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Get the selected sensor position (in raw sensor units).
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop. See
    * Phoenix-Documentation for how to interpret.
    * @return Position of selected sensor (in raw sensor units).
    */
  override def getSelectedSensorPosition(pidIdx: Int): Int = MotControllerJNI.GetSelectedSensorPosition(m_handle, pidIdx)

  /**
    * Get the selected sensor velocity.
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @return selected sensor (in raw sensor units) per 100ms.
    *         See Phoenix-Documentation for how to interpret.
    */
  override def getSelectedSensorVelocity(pidIdx: Int): Int = MotControllerJNI.GetSelectedSensorVelocity(m_handle, pidIdx)

  /**
    * Sets the sensor position to the given value.
    *
    * @param sensorPos
    * Position to set for the selected sensor (in raw sensor units).
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def setSelectedSensorPosition(sensorPos: Int, pidIdx: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetSelectedSensorPosition(m_handle, sensorPos, pidIdx, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the period of the given control frame.
    *
    * @param frame
    * Frame whose period is to be changed.
    * @param periodMs
    * Period in ms for the given frame.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def setControlFramePeriod(frame: ControlFrame, periodMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetControlFramePeriod(m_handle, frame.value, periodMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the period of the given status frame.
    *
    * User ensure CAN Bus utilization is not high.
    *
    * This setting is not persistent and is lost when device is reset.
    * If this is a concern, calling application can use HasReset()
    * to determine if the status frame needs to be reconfigured.
    *
    * @param frame
    * Frame whose period is to be changed.
    * @param periodMs
    * Period in ms for the given frame.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def setControlFramePeriod(frame: Int, periodMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetControlFramePeriod(m_handle, frame, periodMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the period of the given status frame.
    *
    * User ensure CAN Bus utilization is not high.
    *
    * This setting is not persistent and is lost when device is reset. If this
    * is a concern, calling application can use HasReset() to determine if the
    * status frame needs to be reconfigured.
    *
    * @param frameValue
    * Frame whose period is to be changed.
    * @param periodMs
    * Period in ms for the given frame.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def setStatusFramePeriod(frameValue: Int, periodMs: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetStatusFramePeriod(m_handle, frameValue, periodMs, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the period of the given status frame.
    *
    * @param frame
    * Frame whose period is to be changed.
    * @param periodMs
    * Period in ms for the given frame.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def setStatusFramePeriod(frame: StatusFrame, periodMs: Int, timeoutMs: Int): ErrorCode = setStatusFramePeriod(frame.value, periodMs, timeoutMs)

  /**
    * Gets the period of the given status frame.
    *
    * @param frame
    * Frame to get the period of.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Period of the given status frame.
    */
  def getStatusFramePeriod(frame: Int, timeoutMs: Int): Int = MotControllerJNI.GetStatusFramePeriod(m_handle, frame, timeoutMs)

  def getStatusFramePeriod(frame: StatusFrame, timeoutMs: Int): Int = MotControllerJNI.GetStatusFramePeriod(m_handle, frame.value, timeoutMs)

  def getStatusFramePeriod(frame: StatusFrameEnhanced, timeoutMs: Int): Int = MotControllerJNI.GetStatusFramePeriod(m_handle, frame.value, timeoutMs)

  /**
    * Sets the period over which velocity measurements are taken.
    *
    * @param period
    * Desired period for the velocity measurement. @see
    * #VelocityMeasPeriod
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configVelocityMeasurementPeriod(period: VelocityMeasPeriod, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigVelocityMeasurementPeriod(m_handle, period.value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the number of velocity samples used in the rolling average velocity
    * measurement.
    *
    * @param windowSize
    *            Number of samples in the rolling average of velocity
    *            measurement. Valid values are 1,2,4,8,16,32. If another value
    *            is specified, it will truncate to nearest support value.
    * @param timeoutMs
    *            Timeout value in ms. If nonzero, function will wait for config
    *            success and report an error if it times out. If zero, no
    *            blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configVelocityMeasurementWindow(windowSize: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigVelocityMeasurementWindow(m_handle, windowSize, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the forward limit switch for a remote source. For example, a
    * CAN motor controller may need to monitor the Limit-F pin of another Talon
    * or CANifier.
    *
    * @param type
    * Remote limit switch source. User can choose between a remote
    * Talon SRX, CANifier, or deactivate the feature.
    * @param normalOpenOrClose
    * Setting for normally open, normally closed, or disabled. This
    * setting matches the web-based configuration drop down.
    * @param deviceID
    * Device ID of remote source (Talon SRX or CANifier device ID).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configForwardLimitSwitchSource(`type`: RemoteLimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, deviceID: Int, timeoutMs: Int): ErrorCode = configForwardLimitSwitchSource(`type`.value, normalOpenOrClose.value, deviceID, timeoutMs)

  /**
    * Configures the reverse limit switch for a remote source. For example, a
    * CAN motor controller may need to monitor the Limit-R pin of another Talon
    * or CANifier.
    *
    * @param type
    * Remote limit switch source. User can choose between a remote
    * Talon SRX, CANifier, or deactivate the feature.
    * @param normalOpenOrClose
    * Setting for normally open, normally closed, or disabled. This
    * setting matches the web-based configuration drop down.
    * @param deviceID
    * Device ID of remote source (Talon SRX or CANifier device ID).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configReverseLimitSwitchSource(`type`: RemoteLimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, deviceID: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigReverseLimitSwitchSource(m_handle, `type`.value, normalOpenOrClose.value, deviceID, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures a limit switch for a local/remote source.
    *
    * For example, a CAN motor controller may need to monitor the Limit-R pin
    * of another Talon, CANifier, or local Gadgeteer feedback connector.
    *
    * If the sensor is remote, a device ID of zero is assumed. If that's not
    * desired, use the four parameter version of this function.
    *
    * @param type
    * Limit switch source. @see #LimitSwitchSource User can choose
    * between the feedback connector, remote Talon SRX, CANifier, or
    * deactivate the feature.
    * @param normalOpenOrClose
    * Setting for normally open, normally closed, or disabled. This
    * setting matches the web-based configuration drop down.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configForwardLimitSwitchSource(`type`: LimitSwitchSource, normalOpenOrClose: LimitSwitchNormal, timeoutMs: Int): ErrorCode = configForwardLimitSwitchSource(`type`.value, normalOpenOrClose.value, 0x00000000, timeoutMs)

  protected def configForwardLimitSwitchSource(typeValue: Int, normalOpenOrCloseValue: Int, deviceID: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigForwardLimitSwitchSource(m_handle, typeValue, normalOpenOrCloseValue, deviceID, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures a limit switch for a local/remote source.
    *
    * For example, a CAN motor controller may need to monitor the Limit-R pin
    * of another Talon, CANifier, or local Gadgeteer feedback connector.
    *
    * If the sensor is remote, a device ID of zero is assumed. If that's not
    * desired, use the four parameter version of this function.
    *
    * @param typeValue
    * Limit switch source. @see #LimitSwitchSource User can choose
    * between the feedback connector, remote Talon SRX, CANifier, or
    * deactivate the feature.
    * @param normalOpenOrCloseValue
    * Setting for normally open, normally closed, or disabled. This
    * setting matches the web-based configuration drop down.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  protected def configReverseLimitSwitchSource(typeValue: Int, normalOpenOrCloseValue: Int, deviceID: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigReverseLimitSwitchSource(m_handle, typeValue, normalOpenOrCloseValue, deviceID, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the enable state for limit switches.
    *
    * @param enable
    * Enable state for limit switches.
    **/
  override def overrideLimitSwitchesEnable(enable: Boolean): Unit = {
    MotControllerJNI.OverrideLimitSwitchesEnable(m_handle, enable)
  }

  /**
    * Configures the forward soft limit threhold.
    *
    * @param forwardSensorLimit
    * Forward Sensor Position Limit (in raw sensor units).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configForwardSoftLimitThreshold(forwardSensorLimit: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigForwardSoftLimitThreshold(m_handle, forwardSensorLimit, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the reverse soft limit threshold.
    *
    * @param reverseSensorLimit
    * Reverse Sensor Position Limit (in raw sensor units).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configReverseSoftLimitThreshold(reverseSensorLimit: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigReverseSoftLimitThreshold(m_handle, reverseSensorLimit, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the forward soft limit enable.
    *
    * @param enable
    * Forward Sensor Position Limit Enable.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configForwardSoftLimitEnable(enable: Boolean, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigForwardSoftLimitEnable(m_handle, enable, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Configures the reverse soft limit enable.
    *
    * @param enable
    * Reverse Sensor Position Limit Enable.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configReverseSoftLimitEnable(enable: Boolean, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigReverseSoftLimitEnable(m_handle, enable, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Can be used to override-disable the soft limits.
    * This function can be used to quickly disable soft limits without
    * having to modify the persistent configuration.
    *
    * @param enable
    * Enable state for soft limit switches.
    */
  override def overrideSoftLimitsEnable(enable: Boolean): Unit = {
    MotControllerJNI.OverrideSoftLimitsEnable(m_handle, enable)
  }

  /**
    * Sets the 'P' constant in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param value
    * Value of the P constant.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def config_kP(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.Config_kP(m_handle, slotIdx, value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the 'I' constant in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param value
    * Value of the I constant.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def config_kI(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.Config_kI(m_handle, slotIdx, value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the 'D' constant in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param value
    * Value of the D constant.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def config_kD(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.Config_kD(m_handle, slotIdx, value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the 'F' constant in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param value
    * Value of the F constant.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def config_kF(slotIdx: Int, value: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.Config_kF(m_handle, slotIdx, value, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the Integral Zone constant in the given parameter slot. If the
    * (absolute) closed-loop error is outside of this zone, integral
    * accumulator is automatically cleared. This ensures than integral wind up
    * events will stop after the sensor gets far enough from its target.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param izone
    * Value of the Integral Zone constant (closed loop error units X
    * 1ms).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def config_IntegralZone(slotIdx: Int, izone: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.Config_IntegralZone(m_handle, slotIdx, izone, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the allowable closed-loop error in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param allowableClosedLoopError
    * Value of the allowable closed-loop error.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configAllowableClosedloopError(slotIdx: Int, allowableClosedLoopError: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigAllowableClosedloopError(m_handle, slotIdx, allowableClosedLoopError, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the maximum integral accumulator in the given parameter slot.
    *
    * @param slotIdx
    * Parameter slot for the constant.
    * @param iaccum
    * Value of the maximum integral accumulator (closed loop error
    * units X 1ms).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configMaxIntegralAccumulator(slotIdx: Int, iaccum: Double, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigMaxIntegralAccumulator(m_handle, slotIdx, iaccum, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the integral accumulator. Typically this is used to clear/zero the
    * integral accumulator, however some use cases may require seeding the
    * accumulator for a faster response.
    *
    * @param iaccum
    * Value to set for the integral accumulator (closed loop error
    * units X 1ms).
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def setIntegralAccumulator(iaccum: Double, pidIdx: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetIntegralAccumulator(m_handle, iaccum, pidIdx, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets the closed-loop error. The units depend on which control mode is in
    * use. See Phoenix-Documentation information on units.
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @return Closed-loop error value.
    */
  override def getClosedLoopError(pidIdx: Int): Int = MotControllerJNI.GetClosedLoopError(m_handle, pidIdx)

  /**
    * Gets the iaccum value.
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @return Integral accumulator value (Closed-loop error X 1ms).
    */
  override def getIntegralAccumulator(pidIdx: Int): Double = MotControllerJNI.GetIntegralAccumulator(m_handle, pidIdx)

  /**
    * Gets the derivative of the closed-loop error.
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @return The error derivative value.
    */
  override def getErrorDerivative(pidIdx: Int): Double = MotControllerJNI.GetErrorDerivative(m_handle, pidIdx)

  /**
    * Selects which profile slot to use for closed-loop control.
    *
    * @param slotIdx
    * Profile slot to select.
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    **/
  override def selectProfileSlot(slotIdx: Int, pidIdx: Int): Unit = {
    MotControllerJNI.SelectProfileSlot(m_handle, slotIdx, pidIdx)
  }

  /**
    * Gets the current target of a given closed loop.
    *
    * @param pidIdx
    * 0 for Primary closed-loop. 1 for cascaded closed-loop.
    * @return The closed loop target.
    */
  def getClosedLoopTarget(pidIdx: Int): Int = MotControllerJNI.GetClosedLoopTarget(m_handle, pidIdx)

  /**
    * Gets the active trajectory target position using
    * MotionMagic/MotionProfile control modes.
    *
    * @return The Active Trajectory Position in sensor units.
    */
  override def getActiveTrajectoryPosition: Int = MotControllerJNI.GetActiveTrajectoryPosition(m_handle)

  /**
    * Gets the active trajectory target velocity using
    * MotionMagic/MotionProfile control modes.
    *
    * @return The Active Trajectory Velocity in sensor units per 100ms.
    */
  override def getActiveTrajectoryVelocity: Int = MotControllerJNI.GetActiveTrajectoryVelocity(m_handle)

  /**
    * Gets the active trajectory target heading using
    * MotionMagicArc/MotionProfileArc control modes.
    *
    * @return The Active Trajectory Heading in degreees.
    */
  override def getActiveTrajectoryHeading: Double = MotControllerJNI.GetActiveTrajectoryHeading(m_handle)

  /**
    * Sets the Motion Magic Cruise Velocity. This is the peak target velocity
    * that the motion magic curve generator can use.
    *
    * @param sensorUnitsPer100ms
    * Motion Magic Cruise Velocity (in raw sensor units per 100 ms).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configMotionCruiseVelocity(sensorUnitsPer100ms: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigMotionCruiseVelocity(m_handle, sensorUnitsPer100ms, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Sets the Motion Magic Acceleration. This is the target acceleration that
    * the motion magic curve generator can use.
    *
    * @param sensorUnitsPer100msPerSec
    * Motion Magic Acceleration (in raw sensor units per 100 ms per
    * second).
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configMotionAcceleration(sensorUnitsPer100msPerSec: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigMotionAcceleration(m_handle, sensorUnitsPer100msPerSec, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Clear the buffered motion profile in both controller's RAM (bottom), and in the
    * API (top).
    */
  override def clearMotionProfileTrajectories: ErrorCode = {
    val retval = MotControllerJNI.ClearMotionProfileTrajectories(m_handle)
    ErrorCode.valueOf(retval)
  }

  /**
    * Retrieve just the buffer count for the api-level (top) buffer. This
    * routine performs no CAN or data structure lookups, so its fast and ideal
    * if caller needs to quickly poll the progress of trajectory points being
    * emptied into controller's RAM. Otherwise just use GetMotionProfileStatus.
    *
    * @return number of trajectory points in the top buffer.
    */
  override def getMotionProfileTopLevelBufferCount: Int = MotControllerJNI.GetMotionProfileTopLevelBufferCount(m_handle)

  /**
    * Push another trajectory point into the top level buffer (which is emptied
    * into the motor controller's bottom buffer as room allows).
    *
    * @param trajPt to push into buffer.
    *               The members should be filled in with these values...
    *
    *               targPos:  servo position in sensor units.
    *               targVel:  velocity to feed-forward in sensor units
    *               per 100ms.
    *               profileSlotSelect0  Which slot to get PIDF gains. PID is used for position servo. F is used
    *               as the Kv constant for velocity feed-forward. Typically this is hardcoded
    *               to the a particular slot, but you are free gain schedule if need be.
    *               Choose from [0,3]
    *               profileSlotSelect1 Which slot to get PIDF gains for cascaded PId.
    *               This only has impact during MotionProfileArc Control mode.
    *               Choose from [0,1].
    *               isLastPoint  set to nonzero to signal motor controller to keep processing this
    *               trajectory point, instead of jumping to the next one
    *               when timeDurMs expires.  Otherwise MP executer will
    *               eventually see an empty buffer after the last point
    *               expires, causing it to assert the IsUnderRun flag.
    *               However this may be desired if calling application
    *               never wants to terminate the MP.
    *               zeroPos  set to nonzero to signal motor controller to "zero" the selected
    *               position sensor before executing this trajectory point.
    *               Typically the first point should have this set only thus
    *               allowing the remainder of the MP positions to be relative to
    *               zero.
    *               timeDur Duration to apply this trajectory pt.
    *               This time unit is ADDED to the exising base time set by
    *               configMotionProfileTrajectoryPeriod().
    * @return CTR_OKAY if trajectory point push ok. ErrorCode if buffer is
    *         full due to kMotionProfileTopBufferCapacity.
    */
  override def pushMotionProfileTrajectory(trajPt: TrajectoryPoint): ErrorCode = {
    val retval = MotControllerJNI.PushMotionProfileTrajectory2(m_handle, trajPt.position, trajPt.velocity, trajPt.headingDeg, trajPt.profileSlotSelect0, trajPt.profileSlotSelect1, trajPt.isLastPoint, trajPt.zeroPos, trajPt.timeDur.value)
    ErrorCode.valueOf(retval)
  }

  /**
    * Retrieve just the buffer full for the api-level (top) buffer. This
    * routine performs no CAN or data structure lookups, so its fast and ideal
    * if caller needs to quickly poll. Otherwise just use
    * GetMotionProfileStatus.
    *
    * @return number of trajectory points in the top buffer.
    */
  override def isMotionProfileTopLevelBufferFull: Boolean = MotControllerJNI.IsMotionProfileTopLevelBufferFull(m_handle)

  /**
    * This must be called periodically to funnel the trajectory points from the
    * API's top level buffer to the controller's bottom level buffer. Recommendation
    * is to call this twice as fast as the execution rate of the motion
    * profile. So if MP is running with 20ms trajectory points, try calling
    * this routine every 10ms. All motion profile functions are thread-safe
    * through the use of a mutex, so there is no harm in having the caller
    * utilize threading.
    */
  override def processMotionProfileBuffer(): Unit = {
    MotControllerJNI.ProcessMotionProfileBuffer(m_handle)
  }

  /**
    * Retrieve all status information.
    * For best performance, Caller can snapshot all status information regarding the
    * motion profile executer.
    *
    * @param statusToFill Caller supplied object to fill.
    *
    *                     The members are filled, as follows...
    *
    *                     topBufferRem:	The available empty slots in the trajectory buffer.
    *                     The robot API holds a "top buffer" of trajectory points, so your applicaion
    *                     can dump several points at once.  The API will then stream them into the
    *                     low-level buffer, allowing the motor controller to act on them.
    *
    *                     topBufferRem: The number of points in the top trajectory buffer.
    *
    *                     btmBufferCnt: The number of points in the low level controller buffer.
    *
    *                     hasUnderrun: 	Set if isUnderrun ever gets set.
    *                     Only is cleared by clearMotionProfileHasUnderrun() to ensure
    *
    *                     isUnderrun:		This is set if controller needs to shift a point from its buffer into
    *                     the active trajectory point however
    *                     the buffer is empty.
    *                     This gets cleared automatically when is resolved.
    *
    *                     activePointValid:	True if the active trajectory point has not empty, false otherwise. The members in activePoint are only valid if this signal is set.
    *
    *                     isLast:	is set/cleared based on the MP executer's current
    *                     trajectory point's IsLast value.  This assumes
    *                     IsLast was set when PushMotionProfileTrajectory
    *                     was used to insert the currently processed trajectory
    *                     point.
    *
    *                     profileSlotSelect: The currently processed trajectory point's
    *                     selected slot.  This can differ in the currently selected slot used
    *                     for Position and Velocity servo modes
    *
    *                     outputEnable:		The current output mode of the motion profile
    *                     executer (disabled, enabled, or hold).  When changing the set()
    *                     value in MP mode, it's important to check this signal to
    *                     confirm the change takes effect before interacting with the top buffer.
    */
  override def getMotionProfileStatus(statusToFill: MotionProfileStatus): ErrorCode = {
    val retval = MotControllerJNI.GetMotionProfileStatus2(m_handle, _motionProfStats)
    statusToFill.topBufferRem = _motionProfStats(0)
    statusToFill.topBufferCnt = _motionProfStats(1)
    statusToFill.btmBufferCnt = _motionProfStats(2)
    statusToFill.hasUnderrun = _motionProfStats(3) != 0
    statusToFill.isUnderrun = _motionProfStats(4) != 0
    statusToFill.activePointValid = _motionProfStats(5) != 0
    statusToFill.isLast = _motionProfStats(6) != 0
    statusToFill.profileSlotSelect = _motionProfStats(7)
    statusToFill.outputEnable = SetValueMotionProfile.valueOf(_motionProfStats(8))
    statusToFill.timeDurMs = _motionProfStats(9)
    statusToFill.profileSlotSelect1 = _motionProfStats(10)
    ErrorCode.valueOf(retval)
  }

  /**
    * Clear the "Has Underrun" flag. Typically this is called after application
    * has confirmed an underrun had occured.
    *
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def clearMotionProfileHasUnderrun(timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ClearMotionProfileHasUnderrun(m_handle, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Calling application can opt to speed up the handshaking between the robot
    * API and the controller to increase the download rate of the controller's Motion
    * Profile. Ideally the period should be no more than half the period of a
    * trajectory point.
    *
    * @param periodMs
    * The transmit period in ms.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def changeMotionControlFramePeriod(periodMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ChangeMotionControlFramePeriod(m_handle, periodMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * When trajectory points are processed in the motion profile executer, the MPE determines
    * how long to apply the active trajectory point by summing baseTrajDurationMs with the
    * timeDur of the trajectory point (see TrajectoryPoint).
    *
    * This allows general selection of the execution rate of the points with 1ms resolution,
    * while allowing some degree of change from point to point.
    *
    * @param baseTrajDurationMs The base duration time of every trajectory point.
    *                           This is summed with the trajectory points unique timeDur.
    * @param timeoutMs
    *                           Timeout value in ms. If nonzero, function will wait for
    *                           config success and report an error if it times out.
    *                           If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  def configMotionProfileTrajectoryPeriod(baseTrajDurationMs: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigMotionProfileTrajectoryPeriod(m_handle, baseTrajDurationMs, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets the last error generated by this object. Not all functions return an
    * error code but can potentially report errors. This function can be used
    * to retrieve those error codes.
    *
    * @return Last Error Code generated by a function.
    */
  override def getLastError: ErrorCode = {
    val retval = MotControllerJNI.GetLastError(m_handle)
    ErrorCode.valueOf(retval)
  }

  /**
    * Polls the various fault flags.
    *
    * @param toFill
    * Caller's object to fill with latest fault flags.
    * @return Last Error Code generated by a function.
    */
  override def getFaults(toFill: Faults): ErrorCode = {
    val bits = MotControllerJNI.GetFaults(m_handle)
    toFill.update(bits)
    getLastError
  }

  /**
    * Polls the various sticky fault flags.
    *
    * @param toFill
    * Caller's object to fill with latest sticky fault flags.
    * @return Last Error Code generated by a function.
    */
  def getStickyFaults(toFill: StickyFaults): ErrorCode = {
    val bits = MotControllerJNI.GetStickyFaults(m_handle)
    toFill.update(bits)
    getLastError
  }

  /**
    * Clears all sticky faults.
    *
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Last Error Code generated by a function.
    */
  override def clearStickyFaults(timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ClearStickyFaults(m_handle, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets the firmware version of the device.
    *
    * @return Firmware version of device. For example: version 1-dot-2 is
    *         0x0102.
    */
  override def getFirmwareVersion: Int = MotControllerJNI.GetFirmwareVersion(m_handle)

  /**
    * Returns true if the device has reset since last call.
    *
    * @return Has a Device Reset Occurred?
    */
  override def hasResetOccurred: Boolean = MotControllerJNI.HasResetOccurred(m_handle)

  /**
    * Sets the value of a custom parameter. This is for arbitrary use.
    *
    * Sometimes it is necessary to save calibration/limit/target information in
    * the device. Particularly if the device is part of a subsystem that can be
    * replaced.
    *
    * @param newValue
    * Value for custom parameter.
    * @param paramIndex
    * Index of custom parameter [0,1]
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configSetCustomParam(newValue: Int, paramIndex: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigSetCustomParam(m_handle, newValue, paramIndex, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets the value of a custom parameter.
    *
    * @param paramIndex
    * Index of custom parameter [0,1].
    * @param timoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Value of the custom param.
    */
  override def configGetCustomParam(paramIndex: Int, timoutMs: Int): Int = {
    val retval = MotControllerJNI.ConfigGetCustomParam(m_handle, paramIndex, timoutMs)
    retval
  }

  /**
    * Sets a parameter. Generally this is not used. This can be utilized in -
    * Using new features without updating API installation. - Errata
    * workarounds to circumvent API implementation. - Allows for rapid testing
    * / unit testing of firmware.
    *
    * @param param
    * Parameter enumeration.
    * @param value
    * Value of parameter.
    * @param subValue
    * Subvalue for parameter. Maximum value of 255.
    * @param ordinal
    * Ordinal of parameter.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for config
    * success and report an error if it times out. If zero, no
    * blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configSetParameter(param: ParamEnum, value: Double, subValue: Int, ordinal: Int, timeoutMs: Int): ErrorCode = configSetParameter(param.value, value, subValue, ordinal, timeoutMs)

  /**
    * Sets a parameter.
    *
    * @param param
    * Parameter enumeration.
    * @param value
    * Value of parameter.
    * @param subValue
    * Subvalue for parameter. Maximum value of 255.
    * @param ordinal
    * Ordinal of parameter.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Error Code generated by function. 0 indicates no error.
    */
  override def configSetParameter(param: Int, value: Double, subValue: Int, ordinal: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.ConfigSetParameter(m_handle, param, value, subValue, ordinal, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets a parameter.
    *
    * @param param
    * Parameter enumeration.
    * @param ordinal
    * Ordinal of parameter.
    * @param timeoutMs
    * Timeout value in ms. If nonzero, function will wait for
    * config success and report an error if it times out.
    * If zero, no blocking or checking is performed.
    * @return Value of parameter.
    */
  override def configGetParameter(param: ParamEnum, ordinal: Int, timeoutMs: Int): Double = configGetParameter(param.value, ordinal, timeoutMs)

  override def configGetParameter(param: Int, ordinal: Int, timeoutMs: Int): Double = MotControllerJNI.ConfigGetParameter(m_handle, param, ordinal, timeoutMs)

  // ------ Misc. ----------//
  override def getBaseID: Int = _arbId

  /**
    * @return control mode motor controller is in
    */
  def getControlMode: ControlMode = m_controlMode

  /**
    * Set the control mode and output value so that this motor controller will
    * follow another motor controller. Currently supports following Victor SPX
    * and Talon SRX.
    */
  override def follow(masterToFollow: IMotorController): Unit = {
    val id32 = masterToFollow.getBaseID
    var id24 = id32
    id24 >>= 16
    id24 = id24.toShort
    id24 <<= 8
    id24 |= (id32 & 0xFF)
    set(ControlMode.Follower, id24)
  }

  /**
    * When master makes a device, this routine is called to signal the update.
    */
  override def valueUpdated(): Unit = {
    // MT
  }

  /**
    * @return object that can get/set individual raw sensor values.
    */
  def getSensorCollection: SensorCollection = _sensorColl
}