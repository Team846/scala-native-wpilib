/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2017. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.hal

sealed trait AllianceStationID

object AllianceStationID {
  case object Red1 extends AllianceStationID

  case object Red2 extends AllianceStationID

  case object Red3 extends AllianceStationID

  case object Blue1 extends AllianceStationID

  case object Blue2 extends AllianceStationID

  case object Blue3 extends AllianceStationID
}