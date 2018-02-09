package com.ctre.phoenix

class CANifierFaults {
  //!< True iff any of the above flags are true.
  def hasAnyFault(): Boolean = false

  def toBitfield(): Int = {
    val retval: Int = 0
    retval
  }

  def update(bits: Int): Unit = {}
}
