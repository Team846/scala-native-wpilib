package com.ctre.phoenix.motorcontrol

class StickyFaults {
  var UnderVoltage = false
  var ForwardLimitSwitch = false
  var ReverseLimitSwitch = false
  var ForwardSoftLimit = false
  var ReverseSoftLimit = false
  var ResetDuringEn = false
  var SensorOverflow = false
  var SensorOutOfPhase = false
  var HardwareESDReset = false
  var RemoteLossOfSignal = false

  //!< True iff any of the above flags are true.
  def hasAnyFault: Boolean = UnderVoltage | ForwardLimitSwitch | ReverseLimitSwitch | ForwardSoftLimit | ReverseSoftLimit | ResetDuringEn | SensorOverflow | SensorOutOfPhase | HardwareESDReset | RemoteLossOfSignal

  def toBitfield: Int = {
    var retval = 0
    var mask = 1
    retval |= (if (UnderVoltage) mask else 0)
    mask <<= 1
    retval |= (if (ForwardLimitSwitch) mask else 0)
    mask <<= 1
    retval |= (if (ReverseLimitSwitch) mask else 0)
    mask <<= 1
    retval |= (if (ForwardSoftLimit) mask else 0)
    mask <<= 1
    retval |= (if (ReverseSoftLimit) mask else 0)
    mask <<= 1
    retval |= (if (ResetDuringEn) mask else 0)
    mask <<= 1
    retval |= (if (SensorOverflow) mask else 0)
    mask <<= 1
    retval |= (if (SensorOutOfPhase) mask else 0)
    mask <<= 1
    retval |= (if (HardwareESDReset) mask else 0)
    mask <<= 1
    retval |= (if (RemoteLossOfSignal) mask else 0)
    mask <<= 1
    retval
  }

  def update(bits: Int): Unit = {
    var mask = 1
    UnderVoltage = if ((bits & mask) != 0) true else false
    mask <<= 1
    ForwardLimitSwitch = if ((bits & mask) != 0) true else false
    mask <<= 1
    ReverseLimitSwitch = if ((bits & mask) != 0) true else false
    mask <<= 1
    ForwardSoftLimit = if ((bits & mask) != 0) true else false
    mask <<= 1
    ReverseSoftLimit = if ((bits & mask) != 0) true else false
    mask <<= 1
    ResetDuringEn = if ((bits & mask) != 0) true else false
    mask <<= 1
    SensorOverflow = if ((bits & mask) != 0) true else false
    mask <<= 1
    SensorOutOfPhase = if ((bits & mask) != 0) true else false
    mask <<= 1
    HardwareESDReset = if ((bits & mask) != 0) true else false
    mask <<= 1
    RemoteLossOfSignal = if ((bits & mask) != 0) true else false
    mask <<= 1
  }

  override def toString: String = {
    val work = new StringBuilder
    work.append(" UnderVoltage:")
    work.append(if (UnderVoltage) "1" else "0")
    work.append(" ForwardLimitSwitch:")
    work.append(if (ForwardLimitSwitch) "1" else "0")
    work.append(" ReverseLimitSwitch:")
    work.append(if (ReverseLimitSwitch) "1" else "0")
    work.append(" ForwardSoftLimit:")
    work.append(if (ForwardSoftLimit) "1" else "0")
    work.append(" ReverseSoftLimit:")
    work.append(if (ReverseSoftLimit) "1" else "0")
    work.append(" ResetDuringEn:")
    work.append(if (ResetDuringEn) "1" else "0")
    work.append(" SensorOverflow:")
    work.append(if (SensorOverflow) "1" else "0")
    work.append(" SensorOutOfPhase:")
    work.append(if (SensorOutOfPhase) "1" else "0")
    work.append(" HardwareESDReset:")
    work.append(if (HardwareESDReset) "1" else "0")
    work.append(" RemoteLossOfSignal:")
    work.append(if (RemoteLossOfSignal) "1" else "0")
    work.toString
  }
}
