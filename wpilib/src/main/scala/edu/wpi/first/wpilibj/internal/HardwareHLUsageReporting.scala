/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2016-2018. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.internal

import edu.wpi.first.wpilibj.HLUsageReporting
import edu.wpi.first.wpilibj.hal.FRCNetComm.tInstances
import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL

class HardwareHLUsageReporting extends HLUsageReporting.Interface {
  override def reportScheduler(): Unit = {
    HAL.report(tResourceType.kResourceType_Command, tInstances.kCommand_Scheduler)
  }

  override def reportPIDController(num: Int): Unit = {
    HAL.report(tResourceType.kResourceType_PIDController, num)
  }

  override def reportSmartDashboard(): Unit = {
    HAL.report(tResourceType.kResourceType_SmartDashboard, 0)
  }
}
