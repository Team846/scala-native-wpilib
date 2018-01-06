package java.nio

import scala.scalanative.native.Ptr

object DirectBufferAccess {
  def getAttachment(bb: ByteBuffer): Object = bb.asInstanceOf[DirectByteBuffer].attachment()

  def createFromPointer(pointer: Ptr[Byte], size: Int): ByteBuffer = new DirectByteBuffer(0, size, pointer)
}
