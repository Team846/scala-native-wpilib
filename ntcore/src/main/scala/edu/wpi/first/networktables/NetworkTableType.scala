/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2017-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.networktables

final class NetworkTableType (val value: Int)
object NetworkTableType {
  val kUnassigned: NetworkTableType = new NetworkTableType(0)

  val kBoolean: NetworkTableType = new NetworkTableType(0x01)

  val kDouble: NetworkTableType = new NetworkTableType(0x02)

  val kString: NetworkTableType = new NetworkTableType(0x04)

  val kRaw: NetworkTableType = new NetworkTableType(0x08)

  val kBooleanArray: NetworkTableType = new NetworkTableType(0x10)

  val kDoubleArray: NetworkTableType = new NetworkTableType(0x20)

  val kStringArray: NetworkTableType = new NetworkTableType(0x40)

  val kRpc: NetworkTableType = new NetworkTableType(0x80)

  def getFromInt(value: Int): NetworkTableType = value match {
    case 0x01 => kBoolean
    case 0x02 => kDouble
    case 0x04 => kString
    case 0x08 => kRaw
    case 0x10 => kBooleanArray
    case 0x20 => kDoubleArray
    case 0x40 => kStringArray
    case 0x80 => kRpc
    case _ => kUnassigned
  }
}
