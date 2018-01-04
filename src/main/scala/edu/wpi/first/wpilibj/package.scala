package edu.wpi.first

import java.nio.charset.Charset

import scala.scalanative.native.{CString, Ptr, extern, stdlib}

package object wpilibj {
  private[wpilibj] val env = Test.createEnv()
  private[wpilibj] val vm = Test.createVM(env)
  private[wpilibj] val cls: Ptr[Unit] = null

  @extern
  object Test {
    def createEnv(): Ptr[Unit] = extern
    def createVM(env: Ptr[Unit]): Ptr[Unit] = extern
    def testVM(vm: Ptr[Unit], env: Ptr[Unit]): Unit = extern
    def strlen16(str: JString): Int = extern
  }

  type JString = Ptr[Ptr[Ptr[Unit]]] // some weird type that won't show up elsewhere
  implicit def string2JString(string: String): JString = {
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

    cstr.asInstanceOf[Ptr[Ptr[Ptr[Unit]]]]
  }

  implicit def jString2String(jstring: JString): String = {
    val bytesCount = Test.strlen16(jstring) * 2
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
}
