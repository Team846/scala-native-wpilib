package com.lynbrookrobotics

import java.nio.{ByteBuffer, DirectBufferAccess}
import java.nio.charset.Charset

import scala.scalanative.native._
import scala.language.experimental.macros

package object scalanativejni {
  class _env
  type Env = Ptr[_env]

  class _vm
  type VM = Ptr[_vm]

  class _cls
  type Cls = Ptr[_cls]

  def setShortArrayRegion(env: Env, to: Array[Short], start: Int, len: Int, buf: Ptr[Short]): Unit = {
    (0 until len).foreach { fromIndex =>
      to(start + fromIndex) = !(buf + fromIndex)
    }
  }

  def setFloatArrayRegion(env: Env, to: Array[Float], start: Int, len: Int, buf: Ptr[Float]): Unit = {
    (0 until len).foreach { fromIndex =>
      to(start + fromIndex) = !(buf + fromIndex)
    }
  }

  val env: Env = MockJNI.createEnv((env: Env, to: Array[Short], start: Int, len: Int, buf: Ptr[Short]) => {
    setShortArrayRegion(env, to, start, len, buf)
  }, (env: Env, to: Array[Float], start: Int, len: Int, buf: Ptr[Float]) => {
    setFloatArrayRegion(env, to, start, len, buf)
  }, (env: Env, arr: Array[_]) => {
    arr.length
  })

  val vm: VM = MockJNI.createVM(env)
  val cls: Cls = null

  def jni[T]: T = macro JNIMacrosImpl.jniImpl[T]

  @extern
  object MockJNI {
    def createEnv(setShortArrayRegion: CFunctionPtr5[Env, Array[Short], Int, Int, Ptr[Short], Unit],
                  setFloatArrayRegion: CFunctionPtr5[Env, Array[Float], Int, Int, Ptr[Float], Unit],
                  getArrayLength: CFunctionPtr2[Env, Array[_], Int]): Env = extern
    def createVM(env: Env): VM = extern
    def testVM(vm: VM, env: Env): Unit = extern
    def strlen16(str: JString): Int = extern
  }

  class _jstring
  type JString = Ptr[_jstring]

  def string2jString(string: String): JString = {
    val bytes = string.getBytes(Charset.forName("UTF-16"))
    val cstr  = stdlib.malloc(bytes.length + 2)

    var c = 0
    while (c < bytes.length) {
      !(cstr + c + 1) = bytes(c) // reversed because little endian vs big endian lol
      !(cstr + c) = bytes(c + 1)
      c += 2
    }

    !(cstr + bytes.length) = 0.toByte // NUL
    !(cstr + (bytes.length + 1)) = 0.toByte // NUL

    cstr.asInstanceOf[JString]
  }

  def jString2String(jstring: JString): String = {
    val bytesCount = MockJNI.strlen16(jstring) * 2
    val cstr = jstring.asInstanceOf[CString]
    val bytes = new Array[Byte](bytesCount)

    var c = 0 // pad the left with a zero
    while (c < bytesCount) {
      bytes(c) = !(cstr + c + 1) // reversed because little endian vs big endian lol
      bytes(c + 1) = !(cstr + c)
      c += 2
    }

    val ret = new String(bytes, Charset.forName("UTF-16"))
    stdlib.free(jstring.asInstanceOf[Ptr[Byte]])
    ret
  }

  type JDirectByteBuffer = Ptr[CStruct2[Ptr[Byte], Int]]

  def byteBuffer2JDirectByteBuffer(bb: ByteBuffer): JDirectByteBuffer = {
    assert(bb.isDirect)
    val dbb = stdlib.malloc(sizeof[CStruct2[Ptr[Byte], Int]]).asInstanceOf[JDirectByteBuffer]
    !dbb._1 = DirectBufferAccess.getAttachment(bb).cast[Ptr[Byte]]
    !dbb._2 = bb.capacity()
    dbb
  }

  def jDirectByteBuffer2ByteBuffer(dbb: JDirectByteBuffer): ByteBuffer = {
    val ret = DirectBufferAccess.createFromPointer(!dbb._1, !dbb._2)
    stdlib.free(dbb.asInstanceOf[Ptr[Byte]])
    ret
  }
}
