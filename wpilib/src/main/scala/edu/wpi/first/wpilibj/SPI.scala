/*----------------------------------------------------------------------------*/
/* Copyright (c) 2016-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj

import java.nio.ByteBuffer

import edu.wpi.first.wpilibj.hal.FRCNetComm.tResourceType
import edu.wpi.first.wpilibj.hal.HAL
import edu.wpi.first.wpilibj.hal.SPIJNI

/**
  * Constructor.
  *
  * @param port the physical SPI port
  */
class SPI(val port: SPI.Port) {
  private var m_port = port.value.toByte
  private var m_bitOrder = 0
  private var m_clockPolarity = 0
  private var m_dataOnTrailing = 0

  SPI.devices += 1
  SPIJNI.spiInitialize(m_port)
  HAL.report(tResourceType.kResourceType_SPI, SPI.devices)

  /**
    * Free the resources used by this object.
    */
  def free(): Unit = {
    if (m_accum != null) {
      m_accum.free()
      m_accum = null
    }
    SPIJNI.spiClose(m_port)
  }

  /**
    * Configure the rate of the generated clock signal. The default value is 500,000 Hz. The maximum
    * value is 4,000,000 Hz.
    *
    * @param hz The clock rate in Hertz.
    */
  final def setClockRate(hz: Int): Unit = SPIJNI.spiSetSpeed(m_port, hz)

  /**
    * Configure the order that bits are sent and received on the wire to be most significant bit
    * first.
    */
  final def setMSBFirst(): Unit = {
    m_bitOrder = 1
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure the order that bits are sent and received on the wire to be least significant bit
    * first.
    */
  final def setLSBFirst(): Unit = {
    m_bitOrder = 0
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure the clock output line to be active low. This is sometimes called clock polarity high
    * or clock idle high.
    */
  final def setClockActiveLow(): Unit = {
    m_clockPolarity = 1
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure the clock output line to be active high. This is sometimes called clock polarity low
    * or clock idle low.
    */
  final def setClockActiveHigh(): Unit = {
    m_clockPolarity = 0
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure that the data is stable on the falling edge and the data changes on the rising edge.
    */
  final def setSampleDataOnFalling(): Unit = {
    m_dataOnTrailing = 1
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure that the data is stable on the rising edge and the data changes on the falling edge.
    */
  final def setSampleDataOnRising(): Unit = {
    m_dataOnTrailing = 0
    SPIJNI.spiSetOpts(m_port, m_bitOrder, m_dataOnTrailing, m_clockPolarity)
  }

  /**
    * Configure the chip select line to be active high.
    */
  final def setChipSelectActiveHigh(): Unit = SPIJNI.spiSetChipSelectActiveHigh(m_port)

  /**
    * Configure the chip select line to be active low.
    */
  final def setChipSelectActiveLow(): Unit = SPIJNI.spiSetChipSelectActiveLow(m_port)

  /**
    * Write data to the slave device. Blocks until there is space in the output FIFO.
    *
    * <p>If not running in output only mode, also saves the data received on the MISO input during
    * the transfer into the receive FIFO.
    */
  def write(dataToSend: Array[Byte], size: Int): Int = {
    if (dataToSend.length < size) throw new IllegalArgumentException("buffer is too small, must be at least " + size)
    SPIJNI.spiWriteB(m_port, dataToSend, size.toByte)
  }

  /**
    * Write data to the slave device. Blocks until there is space in the output FIFO.
    *
    * <p>If not running in output only mode, also saves the data received on the MISO input during
    * the transfer into the receive FIFO.
    *
    * @param dataToSend The buffer containing the data to send.
    */
  def write(dataToSend: ByteBuffer, size: Int): Int = {
    if (dataToSend.hasArray) return write(dataToSend.array, size)
    if (!dataToSend.isDirect) throw new IllegalArgumentException("must be a direct buffer")
    if (dataToSend.capacity < size) throw new IllegalArgumentException("buffer is too small, must be at least " + size)
    SPIJNI.spiWrite(m_port, dataToSend, size.toByte)
  }

  /**
    * Read a word from the receive FIFO.
    *
    * <p>Waits for the current transfer to complete if the receive FIFO is empty.
    *
    * <p>If the receive FIFO is empty, there is no active transfer, and initiate is false, errors.
    *
    * @param initiate If true, this function pushes "0" into the transmit buffer and initiates a
    *                 transfer. If false, this function assumes that data is already in the receive
    *                 FIFO from a previous write.
    */
  def read(initiate: Boolean, dataReceived: Array[Byte], size: Int): Int = {
    if (dataReceived.length < size) throw new IllegalArgumentException("buffer is too small, must be at least " + size)
    SPIJNI.spiReadB(m_port, initiate, dataReceived, size.toByte)
  }

  /**
    * Read a word from the receive FIFO.
    *
    * <p>Waits for the current transfer to complete if the receive FIFO is empty.
    *
    * <p>If the receive FIFO is empty, there is no active transfer, and initiate is false, errors.
    *
    * @param initiate     If true, this function pushes "0" into the transmit buffer and initiates
    *                     a transfer. If false, this function assumes that data is already in the
    *                     receive FIFO from a previous write.
    * @param dataReceived The buffer to be filled with the received data.
    * @param size         The length of the transaction, in bytes
    */
  def read(initiate: Boolean, dataReceived: ByteBuffer, size: Int): Int = {
    if (dataReceived.hasArray) return read(initiate, dataReceived.array, size)
    if (!dataReceived.isDirect) throw new IllegalArgumentException("must be a direct buffer")
    if (dataReceived.capacity < size) throw new IllegalArgumentException("buffer is too small, must be at least " + size)
    SPIJNI.spiRead(m_port, initiate, dataReceived, size.toByte)
  }

  /**
    * Perform a simultaneous read/write transaction with the device.
    *
    * @param dataToSend   The data to be written out to the device
    * @param dataReceived Buffer to receive data from the device
    * @param size         The length of the transaction, in bytes
    */
  def transaction(dataToSend: Array[Byte], dataReceived: Array[Byte], size: Int): Int = {
    if (dataToSend.length < size) throw new IllegalArgumentException("dataToSend is too small, must be at least " + size)
    if (dataReceived.length < size) throw new IllegalArgumentException("dataReceived is too small, must be at least " + size)
    SPIJNI.spiTransactionB(m_port, dataToSend, dataReceived, size.toByte)
  }

  /**
    * Perform a simultaneous read/write transaction with the device.
    *
    * @param dataToSend   The data to be written out to the device.
    * @param dataReceived Buffer to receive data from the device.
    * @param size         The length of the transaction, in bytes
    */
  def transaction(dataToSend: ByteBuffer, dataReceived: ByteBuffer, size: Int): Int = {
    if (dataToSend.hasArray && dataReceived.hasArray) return transaction(dataToSend.array, dataReceived.array, size)
    if (!dataToSend.isDirect) throw new IllegalArgumentException("dataToSend must be a direct buffer")
    if (dataToSend.capacity < size) throw new IllegalArgumentException("dataToSend is too small, must be at least " + size)
    if (!dataReceived.isDirect) throw new IllegalArgumentException("dataReceived must be a direct buffer")
    if (dataReceived.capacity < size) throw new IllegalArgumentException("dataReceived is too small, must be at least " + size)
    SPIJNI.spiTransaction(m_port, dataToSend, dataReceived, size.toByte)
  }

  /**
    * Initialize automatic SPI transfer engine.
    *
    * <p>Only a single engine is available, and use of it blocks use of all other
    * chip select usage on the same physical SPI port while it is running.
    *
    * @param bufferSize buffer size in bytes
    */
  def initAuto(bufferSize: Int): Unit = SPIJNI.spiInitAuto(m_port, bufferSize)

  /**
    * Frees the automatic SPI transfer engine.
    */
  def freeAuto(): Unit = SPIJNI.spiFreeAuto(m_port)

  /**
    * Set the data to be transmitted by the engine.
    *
    * <p>Up to 16 bytes are configurable, and may be followed by up to 127 zero
    * bytes.
    *
    * @param dataToSend data to send (maximum 16 bytes)
    * @param zeroSize   number of zeros to send after the data
    */
  def setAutoTransmitData(dataToSend: Array[Byte], zeroSize: Int): Unit = SPIJNI.spiSetAutoTransmitData(m_port, dataToSend, zeroSize)

  /**
    * Start running the automatic SPI transfer engine at a periodic rate.
    *
    * <p>{@link #initAuto(int)} and {@link #setAutoTransmitData(byte[], int)} must
    * be called before calling this function.
    *
    * @param period period between transfers, in seconds (us resolution)
    */
  def startAutoRate(period: Double): Unit = SPIJNI.spiStartAutoRate(m_port, period)

  /**
    * Start running the automatic SPI transfer engine when a trigger occurs.
    *
    * <p>{@link #initAuto(int)} and {@link #setAutoTransmitData(byte[], int)} must
    * be called before calling this function.
    *
    * @param source  digital source for the trigger (may be an analog trigger)
    * @param rising  trigger on the rising edge
    * @param falling trigger on the falling edge
    */
  def startAutoTrigger(source: DigitalSource, rising: Boolean, falling: Boolean): Unit = SPIJNI.spiStartAutoTrigger(m_port, source.getPortHandleForRouting, source.getAnalogTriggerTypeForRouting, rising, falling)

  /**
    * Stop running the automatic SPI transfer engine.
    */
  def stopAuto(): Unit = SPIJNI.spiStopAuto(m_port)

  /**
    * Force the engine to make a single transfer.
    */
  def forceAutoRead(): Unit = SPIJNI.spiForceAutoRead(m_port)

  /**
    * Read data that has been transferred by the automatic SPI transfer engine.
    *
    * <p>Transfers may be made a byte at a time, so it's necessary for the caller
    * to handle cases where an entire transfer has not been completed.
    *
    * <p>Blocks until numToRead bytes have been read or timeout expires.
    * May be called with numToRead=0 to retrieve how many bytes are available.
    *
    * @param buffer    buffer where read bytes are stored
    * @param numToRead number of bytes to read
    * @param timeout   timeout in seconds (ms resolution)
    * @return Number of bytes remaining to be read
    */
  def readAutoReceivedData(buffer: ByteBuffer, numToRead: Int, timeout: Double): Int = {
    if (buffer.hasArray) return readAutoReceivedData(buffer.array, numToRead, timeout)
    if (!buffer.isDirect) throw new IllegalArgumentException("must be a direct buffer")
    if (buffer.capacity < numToRead) throw new IllegalArgumentException("buffer is too small, must be at least " + numToRead)
    SPIJNI.spiReadAutoReceivedData(m_port, buffer, numToRead, timeout)
  }

  /**
    * Read data that has been transferred by the automatic SPI transfer engine.
    *
    * <p>Transfers may be made a byte at a time, so it's necessary for the caller
    * to handle cases where an entire transfer has not been completed.
    *
    * <p>Blocks until numToRead bytes have been read or timeout expires.
    * May be called with numToRead=0 to retrieve how many bytes are available.
    *
    * @param buffer    array where read bytes are stored
    * @param numToRead number of bytes to read
    * @param timeout   timeout in seconds (ms resolution)
    * @return Number of bytes remaining to be read
    */
  def readAutoReceivedData(buffer: Array[Byte], numToRead: Int, timeout: Double): Int = {
    if (buffer.length < numToRead) throw new IllegalArgumentException("buffer is too small, must be at least " + numToRead)
    SPIJNI.spiReadAutoReceivedData(m_port, buffer, numToRead, timeout)
  }

  /**
    * Get the number of bytes dropped by the automatic SPI transfer engine due
    * to the receive buffer being full.
    *
    * @return Number of bytes dropped
    */
  def getAutoDroppedCount: Int = SPIJNI.spiGetAutoDroppedCount(m_port)

  private var m_accum: SPI.Accumulator = null

  /**
    * Initialize the accumulator.
    *
    * @param period     Time between reads
    * @param cmd        SPI command to send to request data
    * @param xferSize   SPI transfer size, in bytes
    * @param validMask  Mask to apply to received data for validity checking
    * @param validValue After validMask is applied, required matching value for validity checking
    * @param dataShift  Bit shift to apply to received data to get actual data value
    * @param dataSize   Size (in bits) of data field
    * @param isSigned   Is data field signed?
    * @param bigEndian  Is device big endian?
    */
  def initAccumulator(period: Double, _cmd: Int, xferSize: Int, validMask: Int, validValue: Int, dataShift: Int, dataSize: Int, isSigned: Boolean, bigEndian: Boolean): Unit = {
    var cmd = _cmd
    initAuto(xferSize * 2048)
    val cmdBytes = Array[Byte](0, 0, 0, 0)
    if (bigEndian) {
      var i = xferSize - 1
      while ( {
        i >= 0
      }) {
        cmdBytes(i) = (cmd & 0xff).toByte
        cmd >>= 8

        {
          i -= 1; i
        }
      }
    }
    else {
      cmdBytes(0) = (cmd & 0xff).toByte
      cmd >>= 8
      cmdBytes(1) = (cmd & 0xff).toByte
      cmd >>= 8
      cmdBytes(2) = (cmd & 0xff).toByte
      cmd >>= 8
      cmdBytes(3) = (cmd & 0xff).toByte
    }
    setAutoTransmitData(cmdBytes, xferSize - 4)
    startAutoRate(period)
    m_accum = new SPI.Accumulator(m_port, xferSize, validMask, validValue, dataShift, dataSize, isSigned, bigEndian)
    m_accum.m_notifier.startPeriodic(period * 1024)
  }

  /**
    * Frees the accumulator.
    */
  def freeAccumulator(): Unit = {
    if (m_accum != null) {
      m_accum.free()
      m_accum = null
    }
    freeAuto()
  }

  /**
    * Resets the accumulator to zero.
    */
  def resetAccumulator(): Unit = {
    if (m_accum == null) return
    m_accum.m_mutex.synchronized {
      m_accum.m_value = 0
      m_accum.m_count = 0
      m_accum.m_lastValue = 0
    }

  }

  /**
    * Set the center value of the accumulator.
    *
    * <p>The center value is subtracted from each value before it is added to the accumulator. This
    * is used for the center value of devices like gyros and accelerometers to make integration work
    * and to take the device offset into account when integrating.
    */
  def setAccumulatorCenter(center: Int): Unit = {
    if (m_accum == null) return
    m_accum.m_mutex.synchronized {
      m_accum.m_center = center
    }
  }

  /**
    * Set the accumulator's deadband.
    */
  def setAccumulatorDeadband(deadband: Int): Unit = {
    if (m_accum == null) return
    m_accum.m_mutex.synchronized {
      m_accum.m_deadband = deadband
    }

  }

  /**
    * Read the last value read by the accumulator engine.
    */
  def getAccumulatorLastValue: Int = {
    if (m_accum == null) return 0
    m_accum.m_mutex.synchronized {
      m_accum.update()
      m_accum.m_lastValue
    }
  }

  /**
    * Read the accumulated value.
    *
    * @return The 64-bit value accumulated since the last Reset().
    */
  def getAccumulatorValue: Long = {
    if (m_accum == null) return 0
    m_accum.m_mutex.synchronized {
      m_accum.update()
      m_accum.m_value
    }
  }

  /**
    * Read the number of accumulated values.
    *
    * <p>Read the count of the accumulated values since the accumulator was last Reset().
    *
    * @return The number of times samples from the channel were accumulated.
    */
  def getAccumulatorCount: Int = {
    if (m_accum == null) return 0
    m_accum.m_mutex.synchronized {
      m_accum.update()
      m_accum.m_count
    }
  }

  /**
    * Read the average of the accumulated value.
    *
    * @return The accumulated average value (value / count).
    */
  def getAccumulatorAverage: Double = {
    if (m_accum == null) return 0
    m_accum.m_mutex.synchronized {
      m_accum.update()
      if (m_accum.m_count == 0) return 0.0
      m_accum.m_value.toDouble / m_accum.m_count
    }
  }

  /**
    * Read the accumulated value and the number of accumulated values atomically.
    *
    * <p>This function reads the value and count atomically. This can be used for averaging.
    *
    * @param result AccumulatorResult object to store the results in.
    */
  def getAccumulatorOutput(result: AccumulatorResult): Unit = {
    if (result == null) throw new IllegalArgumentException("Null parameter `result'")
    if (m_accum == null) {
      result.value = 0
      result.count = 0
      return
    }
    m_accum.m_mutex.synchronized {
      m_accum.update()
      result.value = m_accum.m_value
      result.count = m_accum.m_count
    }
  }
}

/**
  * Represents a SPI bus port.
  */
object SPI {
  final class Port(val value: Int)
  object Port {
    val kOnboardCS0 = new Port(0)
    val kOnboardCS1 = new Port(1)
    val kOnboardCS2 = new Port(2)
    val kOnboardCS3 = new Port(3)
    val kMXP = new Port(4)
  }

  private var devices = 0
  private val kAccumulateDepth = 2048

  private class Accumulator private[wpilibj](val m_port: Int, val m_xferSize: Int // SPI transfer size, in bytes
                                             , val m_validMask: Int, val m_validValue: Int, val m_dataShift: Int // data field shift right amount, in bits
                                             , val dataSize: Int, val m_isSigned: Boolean // is data field signed?
                                             , val m_bigEndian: Boolean // is response big endian?
                                            ) { self =>
    private[wpilibj] def free() = m_notifier.stop()

    final private[wpilibj] var m_notifier = new Notifier(new Runnable {
      override def run(): Unit = self.update()
    })
    final private[wpilibj] var m_buf = ByteBuffer.allocateDirect(m_xferSize * kAccumulateDepth)
    final private[wpilibj] val m_mutex = new Object

    private[wpilibj] var m_value = 0L
    private[wpilibj] var m_count = 0
    private[wpilibj] var m_lastValue = 0

    private[wpilibj] var m_center = 0
    private[wpilibj] var m_deadband = 0

    final private[wpilibj] var m_dataMax = 1 << dataSize // one more than max data value

    final private[wpilibj] var m_dataMsbMask = 1 << (dataSize - 1) // data field MSB mask (for signed)

    private[wpilibj] def update(): Unit = {
      m_mutex.synchronized {
        var done = false
        while ( {
          !done
        }) {
          done = true
          // get amount of data available
          var numToRead = SPIJNI.spiReadAutoReceivedData(m_port, m_buf, 0, 0)
          // only get whole responses
          numToRead -= numToRead % m_xferSize
          if (numToRead > m_xferSize * kAccumulateDepth) {
            numToRead = m_xferSize * kAccumulateDepth
            done = false
          }
          if (numToRead == 0) return // no samples
          // read buffered data
          SPIJNI.spiReadAutoReceivedData(m_port, m_buf, numToRead, 0)
          // loop over all responses
          var off = 0
          while ( {
            off < numToRead
          }) { // convert from bytes
            var resp = 0
            if (m_bigEndian) {
              var i = 0
              while ( {
                i < m_xferSize
              }) {
                resp <<= 8
                resp |= m_buf.get(off + i).toInt & 0xff

                {
                  i += 1;
                  i
                }
              }
            }
            else {
              var i = m_xferSize - 1
              while ( {
                i >= 0
              }) {
                resp <<= 8
                resp |= m_buf.get(off + i).toInt & 0xff

                {
                  i -= 1;
                  i
                }
              }
            }
            // process response
            if ((resp & m_validMask) == m_validValue) { // valid sensor data; extract data field
              var data = resp >> m_dataShift
              data &= m_dataMax - 1
              // 2s complement conversion if signed MSB is set
              if (m_isSigned && (data & m_dataMsbMask) != 0) data -= m_dataMax
              // center offset
              data -= m_center
              // only accumulate if outside deadband
              if (data < -m_deadband || data > m_deadband) m_value += data
              m_count += 1
              m_lastValue = data
            }
            else { // no data from the sensor; just clear the last value
              m_lastValue = 0
            }

            off += m_xferSize
          }
        }
      }
    }
  }
}
