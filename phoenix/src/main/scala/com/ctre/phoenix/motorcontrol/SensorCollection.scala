package com.ctre.phoenix.motorcontrol

import com.ctre.phoenix.ErrorCode
import com.ctre.phoenix.motorcontrol.can.BaseMotorController
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI


class SensorCollection(val motorController: BaseMotorController) {
  private var _handle = motorController.getHandle

  /**
    * Get the position of whatever is in the analog pin of the Talon, regardless of
    * whether it is actually being used for feedback.
    *
    * @return the 24bit analog value.  The bottom ten bits is the ADC (0 - 1023)
    *         on the analog pin of the Talon. The upper 14 bits tracks the overflows and underflows
    *         (continuous sensor).
    */
  def getAnalogIn: Int = MotControllerJNI.GetAnalogIn(_handle)

  /**
    * Sets analog position.
    *
    * @param   newPosition The new position.
    * @param   timeoutMs
    *                      Timeout value in ms. If nonzero, function will wait for
    *                      config success and report an error if it times out.
    *                      If zero, no blocking or checking is performed.
    * @return an ErrorCode.
    */
  def setAnalogPosition(newPosition: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetAnalogPosition(_handle, newPosition, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Get the position of whatever is in the analog pin of the Talon, regardless of whether
    * it is actually being used for feedback.
    *
    * @return the ADC (0 - 1023) on analog pin of the Talon.
    */
  def getAnalogInRaw: Int = MotControllerJNI.GetAnalogInRaw(_handle)

  /**
    * Get the velocity of whatever is in the analog pin of the Talon, regardless of
    * whether it is actually being used for feedback.
    *
    * @return the speed in units per 100ms where 1024 units is one rotation.
    */
  def getAnalogInVel: Int = MotControllerJNI.GetAnalogInVel(_handle)

  /**
    * Get the quadrature position of the Talon, regardless of whether
    * it is actually being used for feedback.
    *
    * @return the quadrature position.
    */
  def getQuadraturePosition: Int = MotControllerJNI.GetQuadraturePosition(_handle)

  /**
    * Change the quadrature reported position.  Typically this is used to "zero" the
    *   sensor. This only works with Quadrature sensor.  To set the selected sensor position
    * regardless of what type it is, see SetSelectedSensorPosition in the motor controller class.
    *
    * @param   newPosition The position value to apply to the sensor.
    * @param   timeoutMs
    *                      Timeout value in ms. If nonzero, function will wait for
    *                      config success and report an error if it times out.
    *                      If zero, no blocking or checking is performed.
    * @return error code.
    */
  def setQuadraturePosition(newPosition: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetQuadraturePosition(_handle, newPosition, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Get the quadrature velocity, regardless of whether
    * it is actually being used for feedback.
    *
    * @return the quadrature velocity in units per 100ms.
    */
  def getQuadratureVelocity: Int = MotControllerJNI.GetQuadratureVelocity(_handle)

  /**
    * Gets pulse width position, regardless of whether
    * it is actually being used for feedback.
    *
    * @return the pulse width position.
    */
  def getPulseWidthPosition: Int = MotControllerJNI.GetPulseWidthPosition(_handle)

  /**
    * Sets pulse width position.
    *
    * @param   newPosition The position value to apply to the sensor.
    * @param   timeoutMs
    *                      Timeout value in ms. If nonzero, function will wait for
    *                      config success and report an error if it times out.
    *                      If zero, no blocking or checking is performed.
    * @return an ErrErrorCode
    */
  def setPulseWidthPosition(newPosition: Int, timeoutMs: Int): ErrorCode = {
    val retval = MotControllerJNI.SetPulseWidthPosition(_handle, newPosition, timeoutMs)
    ErrorCode.valueOf(retval)
  }

  /**
    * Gets pulse width velocity, regardless of whether
    * it is actually being used for feedback.
    *
    * @return the pulse width velocity in units per 100ms (where 4096 units is 1 rotation).
    */
  def getPulseWidthVelocity: Int = MotControllerJNI.GetPulseWidthVelocity(_handle)

  /**
    * Gets pulse width rise to fall time.
    *
    * @return the pulse width rise to fall time in microseconds.
    */
  def getPulseWidthRiseToFallUs: Int = MotControllerJNI.GetPulseWidthRiseToFallUs(_handle)

  /**
    * Gets pulse width rise to rise time.
    *
    * @return the pulse width rise to rise time in microseconds.
    */
  def getPulseWidthRiseToRiseUs: Int = MotControllerJNI.GetPulseWidthRiseToRiseUs(_handle)

  /**
    * Gets pin state quad a.
    *
    * @return the pin state of quad a (1 if asserted, 0 if not asserted).
    */
  def getPinStateQuadA: Boolean = MotControllerJNI.GetPinStateQuadA(_handle) != 0

  /**
    * Gets pin state quad b.
    *
    * @return Digital level of QUADB pin (1 if asserted, 0 if not asserted).
    */
  def getPinStateQuadB: Boolean = MotControllerJNI.GetPinStateQuadB(_handle) != 0

  /**
    * Gets pin state quad index.
    *
    * @return Digital level of QUAD Index pin (1 if asserted, 0 if not asserted).
    */
  def getPinStateQuadIdx: Boolean = MotControllerJNI.GetPinStateQuadIdx(_handle) != 0

  /**
    * Is forward limit switch closed.
    *
    * @return '1' iff forward limit switch is closed, 0 iff switch is open. This function works
    *         regardless if limit switch feature is enabled.
    */
  def isFwdLimitSwitchClosed: Boolean = MotControllerJNI.IsFwdLimitSwitchClosed(_handle) != 0

  /**
    * Is reverse limit switch closed.
    *
    * @return '1' iff reverse limit switch is closed, 0 iff switch is open. This function works
    *         regardless if limit switch feature is enabled.
    */
  def isRevLimitSwitchClosed: Boolean = MotControllerJNI.IsRevLimitSwitchClosed(_handle) != 0
}
