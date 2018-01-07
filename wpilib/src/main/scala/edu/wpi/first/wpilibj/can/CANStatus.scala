/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.can

/**
  * Structure for holding the result of a CAN Status request.
  */
class CANStatus {
  /**
    * The utilization of the CAN Bus.
    */
  @SuppressWarnings(Array("MemberName")) var percentBusUtilization = .0
  /**
    * The CAN Bus off count.
    */
  @SuppressWarnings(Array("MemberName")) var busOffCount = 0
  /**
    * The CAN Bus TX full count.
    */
  @SuppressWarnings(Array("MemberName")) var txFullCount = 0
  /**
    * The CAN Bus receive error count.
    */
  @SuppressWarnings(Array("MemberName")) var receiveErrorCount = 0
  /**
    * The CAN Bus transmit error count.
    */
  @SuppressWarnings(Array("MemberName")) var transmitErrorCount = 0

  @SuppressWarnings(Array("JavadocMethod")) def setStatus(percentBusUtilization: Double, busOffCount: Int, txFullCount: Int, receiveErrorCount: Int, transmitErrorCount: Int): Unit = {
    this.percentBusUtilization = percentBusUtilization
    this.busOffCount = busOffCount
    this.txFullCount = txFullCount
    this.receiveErrorCount = receiveErrorCount
    this.transmitErrorCount = transmitErrorCount
  }
}
