/*----------------------------------------------------------------------------*/
/* Copyright (c) 2008-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.io.UnsupportedEncodingException

import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.SerialPortJNI

import SerialPort._

/**
  * Driver for the RS-232 serial port on the roboRIO.
  *
  * <p>The current implementation uses the VISA formatted I/O mode. This means that all traffic goes
  * through the formatted buffers. This allows the intermingled use of print(), readString(), and the
  * raw buffer accessors read() and write().
  *
  * <p>More information can be found in the NI-VISA User Manual here: http://www.ni
  * .com/pdf/manuals/370423a.pdf and the NI-VISA Programmer's Reference Manual here:
  * http://www.ni.com/pdf/manuals/370132c.pdf
  *
  * Create an instance of a Serial Port class.
  *
  * @param baudRate The baud rate to configure the serial port.
  * @param port     The Serial port to use
  * @param dataBits The number of data bits per transfer. Valid values are between 5 and 8 bits.
  * @param parity   Select the type of parity checking to use.
  * @param stopBits The number of stop bits to use as defined by the enum StopBits.
  */
class SerialPort(baudRate: Int, port: Port, dataBits: Int, parity: Parity, stopBits: StopBits) {
  private var m_port: Byte = port.value.toByte

  SerialPortJNI.serialInitializePort(m_port)
  SerialPortJNI.serialSetBaudRate(m_port, baudRate)
  SerialPortJNI.serialSetDataBits(m_port, dataBits.toByte)
  SerialPortJNI.serialSetParity(m_port, parity.value.toByte)
  SerialPortJNI.serialSetStopBits(m_port, stopBits.value.toByte)

  // Set the default read buffer size to 1 to return bytes immediately
  setReadBufferSize(1)

  // Set the default timeout to 5 seconds.
  setTimeout(5.0)

  // Don't wait until the buffer is full to transmit.
  setWriteBufferMode(WriteBufferMode.kFlushOnAccess)

  disableTermination()

  HAL.report(tResourceType.kResourceType_SerialPort, 0)

  /**
    * Create an instance of a Serial Port class. Defaults to one stop bit.
    *
    * @param baudRate The baud rate to configure the serial port.
    * @param dataBits The number of data bits per transfer. Valid values are between 5 and 8 bits.
    * @param parity   Select the type of parity checking to use.
    */
  def this(baudRate: Int, port: Port, dataBits: Int, parity: Parity) =
    this(baudRate, port, dataBits, parity, StopBits.kOne)

  /**
    * Create an instance of a Serial Port class. Defaults to no parity and one stop bit.
    *
    * @param baudRate The baud rate to configure the serial port.
    * @param dataBits The number of data bits per transfer. Valid values are between 5 and 8 bits.
    */
  def this(baudRate: Int, port: Port, dataBits: Int) =
    this(baudRate, port, dataBits, Parity.kNone, StopBits.kOne)

  /**
    * Create an instance of a Serial Port class. Defaults to 8 databits, no parity, and one stop
    * bit.
    *
    * @param baudRate The baud rate to configure the serial port.
    */
  def this(baudRate: Int, port: Port) =
    this(baudRate, port, 8, Parity.kNone, StopBits.kOne)

  /**
    * Destructor.
    */
  def free(): Unit = {
    SerialPortJNI.serialClose(m_port)
  }

  /**
    * Set the type of flow control to enable on this port.
    *
    * <p>By default, flow control is disabled.
    *
    * @param flowControl the FlowControl m_value to use
    */
  def setFlowControl(flowControl: FlowControl): Unit = {
    SerialPortJNI.serialSetFlowControl(m_port, flowControl.value.toByte)
  }

  /**
    * Enable termination and specify the termination character.
    *
    * <p>Termination is currently only implemented for receive. When the the terminator is received,
    * the read() or readString() will return fewer bytes than requested, stopping after the
    * terminator.
    *
    * @param terminator The character to use for termination.
    */
  def enableTermination(terminator: Char): Unit = {
    SerialPortJNI.serialEnableTermination(m_port, terminator)
  }

  /**
    * Enable termination with the default terminator '\n'
    *
    * <p>Termination is currently only implemented for receive. When the the terminator is received,
    * the read() or readString() will return fewer bytes than requested, stopping after the
    * terminator.
    *
    * <p>The default terminator is '\n'
    */
  def enableTermination(): Unit = {
    enableTermination('\n')
  }

  /**
    * Disable termination behavior.
    */
  def disableTermination(): Unit = {
    SerialPortJNI.serialDisableTermination(m_port)
  }

  /**
    * Get the number of bytes currently available to read from the serial port.
    *
    * @return The number of bytes available to read.
    */
  def getBytesReceived(): Int = SerialPortJNI.serialGetBytesReceived(m_port)

  /**
    * Read a string out of the buffer. Reads the entire contents of the buffer
    *
    * @return The read string
    */
  def readString(): String = readString(getBytesReceived)

  /**
    * Read a string out of the buffer. Reads the entire contents of the buffer
    *
    * @param count the number of characters to read into the string
    * @return The read string
    */
  def readString(count: Int): String = {
    val out: Array[Byte] = read(count)
    try new String(out, 0, out.length, "US-ASCII")
    catch {
      case ex: UnsupportedEncodingException => {
        ex.printStackTrace()
        ""
      }

    }
  }

  /**
    * Read raw bytes out of the buffer.
    *
    * @param count The maximum number of bytes to read.
    * @return An array of the read bytes
    */
  def read(count: Int): Array[Byte] = {
    val dataReceivedBuffer: Array[Byte] = new Array[Byte](count)
    val gotten: Int =
      SerialPortJNI.serialRead(m_port, dataReceivedBuffer, count)
    if (gotten == count) {
      dataReceivedBuffer
    }
    val retVal: Array[Byte] = new Array[Byte](gotten)
    System.arraycopy(dataReceivedBuffer, 0, retVal, 0, gotten)
    retVal
  }

  /**
    * Write raw bytes to the serial port.
    *
    * @param buffer The buffer of bytes to write.
    * @param count  The maximum number of bytes to write.
    * @return The number of bytes actually written into the port.
    */
  def write(buffer: Array[Byte], count: Int): Int = {
    if (buffer.length < count) {
      throw new IllegalArgumentException(
        "buffer is too small, must be at least " + count)
    }
    SerialPortJNI.serialWrite(m_port, buffer, count)
  }

  /**
    * Write a string to the serial port.
    *
    * @param data The string to write to the serial port.
    * @return The number of bytes actually written into the port.
    */
  def writeString(data: String): Int = write(data.getBytes, data.length)

  /**
    * Configure the timeout of the serial m_port.
    *
    * <p>This defines the timeout for transactions with the hardware. It will affect reads if less
    * bytes are available than the read buffer size (defaults to 1) and very large writes.
    *
    * @param timeout The number of seconds to to wait for I/O.
    */
  def setTimeout(timeout: Double): Unit = {
    SerialPortJNI.serialSetTimeout(m_port, timeout)
  }

  /**
    * Specify the size of the input buffer.
    *
    * <p>Specify the amount of data that can be stored before data from the device is returned to
    * Read. If you want data that is received to be returned immediately, set this to 1.
    *
    * <p>It the buffer is not filled before the read timeout expires, all data that has been received
    * so far will be returned.
    *
    * @param size The read buffer size.
    */
  def setReadBufferSize(size: Int): Unit = {
    SerialPortJNI.serialSetReadBufferSize(m_port, size)
  }

  /**
    * Specify the size of the output buffer.
    *
    * <p>Specify the amount of data that can be stored before being transmitted to the device.
    *
    * @param size The write buffer size.
    */
  def setWriteBufferSize(size: Int): Unit = {
    SerialPortJNI.serialSetWriteBufferSize(m_port, size)
  }

  /**
    * Specify the flushing behavior of the output buffer.
    *
    * <p>When set to kFlushOnAccess, data is synchronously written to the serial port after each
    * call to either print() or write().
    *
    * <p>When set to kFlushWhenFull, data will only be written to the serial port when the buffer
    * is full or when flush() is called.
    *
    * @param mode The write buffer mode.
    */
  def setWriteBufferMode(mode: WriteBufferMode): Unit = {
    SerialPortJNI.serialSetWriteMode(m_port, mode.value.toByte)
  }

  /**
    * Force the output buffer to be written to the port.
    *
    * <p>This is used when setWriteBufferMode() is set to kFlushWhenFull to force a flush before the
    * buffer is full.
    */
  def flush(): Unit = {
    SerialPortJNI.serialFlush(m_port)
  }

  /**
    * Reset the serial port driver to a known state.
    *
    * <p>Empty the transmit and receive buffers in the device and formatted I/O.
    */
  def reset(): Unit = {
    SerialPortJNI.serialClear(m_port)
  }

}

object SerialPort {
  class Port(val value: Int)
  object Port {
    val kOnboard: Port = new Port(0)

    val kMXP: Port = new Port(1)

    val kUSB: Port = new Port(2)

    val kUSB1: Port = new Port(2)

    val kUSB2: Port = new Port(3)
  }

  class Parity(val value: Int)
  object Parity {
    val kNone: Parity = new Parity(0)

    val kOdd: Parity = new Parity(1)

    val kEven: Parity = new Parity(2)

    val kMark: Parity = new Parity(3)

    val kSpace: Parity = new Parity(4)
  }

  class StopBits(val value: Int)
  object StopBits {
    val kOne: StopBits = new StopBits(10)

    val kOnePointFive: StopBits = new StopBits(15)

    val kTwo: StopBits = new StopBits(20)
  }

  class FlowControl(val value: Int)
  object FlowControl {
    val kNone: FlowControl = new FlowControl(0)

    val kXonXoff: FlowControl = new FlowControl(1)

    val kRtsCts: FlowControl = new FlowControl(2)

    val kDtsDsr: FlowControl = new FlowControl(4)
  }

  class WriteBufferMode(val value: Int)
  object WriteBufferMode {
    val kFlushOnAccess: WriteBufferMode = new WriteBufferMode(1)

    val kFlushWhenFull: WriteBufferMode = new WriteBufferMode(2)
  }
}
