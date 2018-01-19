package com.ctre.phoenix

import java.util

final class ErrorCode(val value: Int)
object ErrorCode {
  val OK = new ErrorCode(0) 						//!< No Error - Function executed as expected

  //CAN-Related
  val CAN_MSG_STALE = new ErrorCode(1)
  val CAN_TX_FULL = new ErrorCode(-1)
  val TxFailed = new ErrorCode(-1)				//!< Could not transmit the CAN frame.
  val InvalidParamValue = new ErrorCode(-2) 	//!< Caller passed an invalid param
  val CAN_INVALID_PARAM = new ErrorCode(-2)
  val RxTimeout = new ErrorCode(-3)				//!< CAN frame has not been received within specified period of time.
  val CAN_MSG_NOT_FOUND = new ErrorCode(-3)
  val TxTimeout = new ErrorCode(-4)				//!< Not used.
  val CAN_NO_MORE_TX_JOBS = new ErrorCode(-4)
  val UnexpectedArbId = new ErrorCode(-5)		//!< Specified CAN Id is invalid.
  val CAN_NO_SESSIONS_AVAIL = new ErrorCode(-5)
  val BufferFull = new ErrorCode(+6)			//!< Caller attempted to insert data into a buffer that is full.
  val CAN_OVERFLOW = new ErrorCode(-6)
  val SensorNotPresent = new ErrorCode(-7)		//!< Sensor is not present
  val FirmwareTooOld  = new ErrorCode(-8)

  //General
  val GeneralError = new ErrorCode(-100)		//!< User Specified General Error
  val GENERAL_ERROR = new ErrorCode(-100)

  //Signal
  val SIG_NOT_UPDATED = new ErrorCode(-200)
  val SigNotUpdated = new ErrorCode(-200)			//!< Have not received an value response for signal.
  val NotAllPIDValuesUpdated = new ErrorCode(-201)

  //Gadgeteer Port Error Codes
  //These include errors between ports and modules
  val GEN_PORT_ERROR = new ErrorCode(-300)
  val PORT_MODULE_TYPE_MISMATCH = new ErrorCode(-301)

  //Gadgeteer Module Error Codes
  //These apply only to the module units themselves
  val GEN_MODULE_ERROR = new ErrorCode(-400)
  val MODULE_NOT_INIT_SET_ERROR = new ErrorCode(-401)
  val MODULE_NOT_INIT_GET_ERROR = new ErrorCode(-402)

  //API
  val WheelRadiusTooSmall = new ErrorCode(-500)
  val TicksPerRevZero = new ErrorCode(-501)
  val DistanceBetweenWheelsTooSmall = new ErrorCode(-502)
  val GainsAreNotSet = new ErrorCode(-503)

  //Higher Level
  val IncompatibleMode = new ErrorCode(-600)
  val InvalidHandle = new ErrorCode(-601)		//!< Handle does not match stored map of handles

  //CAN Related
  val PulseWidthSensorNotPresent  = new ErrorCode(10)	//!< Special Code for "isSensorPresent"

  //General
  val GeneralWarning = new ErrorCode(100)
  val FeatureNotSupported = new ErrorCode(101)
  val NotImplemented = new ErrorCode(102)
  val FirmVersionCouldNotBeRetrieved = new ErrorCode(103)

  val values = Seq(
    OK,

    //CAN-Related
    CAN_MSG_STALE,
    CAN_TX_FULL,
    TxFailed,
    InvalidParamValue,
    CAN_INVALID_PARAM,
    RxTimeout,
    CAN_MSG_NOT_FOUND,
    TxTimeout,
    CAN_NO_MORE_TX_JOBS,
    UnexpectedArbId,
    CAN_NO_SESSIONS_AVAIL,
    BufferFull,
    CAN_OVERFLOW,
    SensorNotPresent,
    FirmwareTooOld ,

    //General
    GeneralError,
    GENERAL_ERROR,

    //Signal
    SIG_NOT_UPDATED,
    SigNotUpdated,
    NotAllPIDValuesUpdated,

    //Gadgeteer Port Error Codes
    //These include errors between ports and modules
    GEN_PORT_ERROR,
    PORT_MODULE_TYPE_MISMATCH,

    //Gadgeteer Module Error Codes
    //These apply only to the module units themselves
    GEN_MODULE_ERROR,
    MODULE_NOT_INIT_SET_ERROR,
    MODULE_NOT_INIT_GET_ERROR,

    //API
    WheelRadiusTooSmall,
    TicksPerRevZero,
    DistanceBetweenWheelsTooSmall,
    GainsAreNotSet,

    //Higher Level
    IncompatibleMode,
    InvalidHandle,

    //CAN Related
    PulseWidthSensorNotPresent,

    //General
    GeneralWarning,
    FeatureNotSupported,
    NotImplemented,
    FirmVersionCouldNotBeRetrieved
  )

  //---------------------- Integral To Enum operators -----------//

  val value = 0 //!< Hold the integral value of an enum instance.

  /** Keep singleton map to quickly lookup enum via int */
  private var _map = new util.HashMap[Int, ErrorCode]()

  values.foreach(v => _map.put(v.value, v))

  /** public lookup to convert int to enum */
  def valueOf(value: Int): ErrorCode = {
    val retval = _map.get(value)
    if (retval != null) return retval
    GeneralError
  }

  /** @return the first nonzero error code */
  def worstOne(errorCode1: ErrorCode, errorCode2: ErrorCode): ErrorCode = {
    if (errorCode1.value != 0) return errorCode1
    errorCode2
  }

  def worstOne(errorCode1: ErrorCode, errorCode2: ErrorCode, errorCode3: ErrorCode): ErrorCode = {
    if (errorCode1.value != 0) return errorCode1
    if (errorCode2.value != 0) return errorCode2
    errorCode3
  }

  def worstOne(errorCode1: ErrorCode, errorCode2: ErrorCode, errorCode3: ErrorCode, errorCode4: ErrorCode): ErrorCode = {
    if (errorCode1.value != 0) return errorCode1
    if (errorCode2.value != 0) return errorCode2
    if (errorCode3.value != 0) return errorCode3
    errorCode4
  }
}
