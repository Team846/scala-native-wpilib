/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import edu.wpi.first.wpilibj.hal.InterruptJNI
import edu.wpi.first.wpilibj.util.AllocationException

/**
  * Create a new InterrupatableSensorBase.
  */
abstract class InterruptableSensorBase() {
  /**
    * The interrupt resource.
    */
  protected var m_interrupt: Int = InterruptJNI.HalInvalidHandle
  /**
    * Flags if the interrupt being allocated is synchronous.
    */
  protected var m_isSynchronousInterrupt = false

  m_interrupt = 0

  /**
    * Frees the resources for this output.
    */
  def free(): Unit = {
//    super.free
    if (m_interrupt != 0) cancelInterrupts()
  }

  /**
    * If this is an analog trigger.
    *
    * @return true if this is an analog trigger.
    */
  def getAnalogTriggerTypeForRouting: Int

  /**
    * The channel routing number.
    *
    * @return channel routing number
    */
  def getPortHandleForRouting: Int

  /**
    * Request one of the 8 interrupts asynchronously on this digital input.
    *
    * @param handler The { @link InterruptHandlerFunction} that contains the method { @link
    *                            InterruptHandlerFunction#interruptFired(int, Object)} that will be called
    *                            whenever there is an interrupt on this device. Request interrupts in synchronous
    *                            mode where the user program interrupt handler will be called when an interrupt
    *                occurs. The default is interrupt on rising edges only.
    */
  def requestInterrupts(handler: InterruptHandlerFunction[_ <: Object]): Unit = {
    if (m_interrupt != 0) throw new AllocationException("The interrupt has already been allocated")
    allocateInterrupts(false)
    assert(m_interrupt != 0)
    InterruptJNI.requestInterrupts(m_interrupt, getPortHandleForRouting, getAnalogTriggerTypeForRouting)
    setUpSourceEdge(true, false)
    InterruptJNI.attachInterruptHandler(m_interrupt, handler.m_function, handler.overridableParameter)
  }

  /**
    * Request one of the 8 interrupts synchronously on this digital input. Request interrupts in
    * synchronous mode where the user program will have to explicitly wait for the interrupt to occur
    * using {@link #waitForInterrupt}. The default is interrupt on rising edges only.
    */
  def requestInterrupts(): Unit = {
    if (m_interrupt != 0) throw new AllocationException("The interrupt has already been allocated")
    allocateInterrupts(true)
    assert(m_interrupt != 0)
    InterruptJNI.requestInterrupts(m_interrupt, getPortHandleForRouting, getAnalogTriggerTypeForRouting)
    setUpSourceEdge(true, false)
  }

  /**
    * Allocate the interrupt.
    *
    * @param watcher true if the interrupt should be in synchronous mode where the user program will
    *                have to explicitly wait for the interrupt to occur.
    */
  protected def allocateInterrupts(watcher: Boolean): Unit = {
    m_isSynchronousInterrupt = watcher
    m_interrupt = InterruptJNI.initializeInterrupts(watcher)
  }

  /**
    * Cancel interrupts on this device. This deallocates all the chipobject structures and disables
    * any interrupts.
    */
  def cancelInterrupts(): Unit = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    InterruptJNI.cleanInterrupts(m_interrupt)
    m_interrupt = 0
  }

  /**
    * In synchronous mode, wait for the defined interrupt to occur.
    *
    * @param timeout        Timeout in seconds
    * @param ignorePrevious If true, ignore interrupts that happened before waitForInterrupt was
    *                       called.
    * @return Result of the wait.
    */
  def waitForInterrupt(timeout: Double, ignorePrevious: Boolean): InterruptableSensorBase.WaitResult = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    var result = InterruptJNI.waitForInterrupt(m_interrupt, timeout, ignorePrevious)
    // Rising edge result is the interrupt bit set in the byte 0xFF
    // Falling edge result is the interrupt bit set in the byte 0xFF00
    // Set any bit set to be true for that edge, and AND the 2 results
    // together to match the existing enum for all interrupts
    val rising = if ((result & 0xFF) != 0) 0x1
    else 0x0
    val falling = if ((result & 0xFF00) != 0) 0x0100
    else 0x0
    result = rising | falling
    for (mode <- InterruptableSensorBase.WaitResult.values) {
      if (mode.value == result) return mode
    }
    null
  }

  /**
    * In synchronous mode, wait for the defined interrupt to occur.
    *
    * @param timeout Timeout in seconds
    * @return Result of the wait.
    */
  def waitForInterrupt(timeout: Double): InterruptableSensorBase.WaitResult = waitForInterrupt(timeout, true)

  /**
    * Enable interrupts to occur on this input. Interrupts are disabled when the RequestInterrupt
    * call is made. This gives time to do the setup of the other options before starting to field
    * interrupts.
    */
  def enableInterrupts(): Unit = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    if (m_isSynchronousInterrupt) throw new IllegalStateException("You do not need to enable synchronous interrupts")
    InterruptJNI.enableInterrupts(m_interrupt)
  }

  /**
    * Disable Interrupts without without deallocating structures.
    */
  def disableInterrupts(): Unit = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    if (m_isSynchronousInterrupt) throw new IllegalStateException("You can not disable synchronous interrupts")
    InterruptJNI.disableInterrupts(m_interrupt)
  }

  /**
    * Return the timestamp for the rising interrupt that occurred most recently. This is in the same
    * time domain as getClock(). The rising-edge interrupt should be enabled with {@link
    * #setUpSourceEdge}.
    *
    * @return Timestamp in seconds since boot.
    */
  def readRisingTimestamp: Double = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    InterruptJNI.readInterruptRisingTimestamp(m_interrupt)
  }

  /**
    * Return the timestamp for the falling interrupt that occurred most recently. This is in the same
    * time domain as getClock(). The falling-edge interrupt should be enabled with {@link
    * #setUpSourceEdge}.
    *
    * @return Timestamp in seconds since boot.
    */
  def readFallingTimestamp: Double = {
    if (m_interrupt == 0) throw new IllegalStateException("The interrupt is not allocated.")
    InterruptJNI.readInterruptFallingTimestamp(m_interrupt)
  }

  /**
    * Set which edge to trigger interrupts on.
    *
    * @param risingEdge  true to interrupt on rising edge
    * @param fallingEdge true to interrupt on falling edge
    */
  def setUpSourceEdge(risingEdge: Boolean, fallingEdge: Boolean): Unit = if (m_interrupt != 0) InterruptJNI.setInterruptUpSourceEdge(m_interrupt, risingEdge, fallingEdge)
  else throw new IllegalArgumentException("You must call RequestInterrupts before setUpSourceEdge")
}

/**
  * Base for sensors to be used with interrupts.
  */
object InterruptableSensorBase {
  final class WaitResult(val value: Int)
  @SuppressWarnings(Array("JavadocMethod"))
  object WaitResult {
    val kTimeout = new WaitResult(0x0)
    val kRisingEdge = new WaitResult(0x1)
    val kFallingEdge = new WaitResult(0x100)
    val kBoth = new WaitResult(0x101)

    val values = Seq(kTimeout, kRisingEdge, kFallingEdge, kBoth)
  }
}
