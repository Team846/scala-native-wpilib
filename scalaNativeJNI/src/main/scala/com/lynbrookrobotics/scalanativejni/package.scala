package com.lynbrookrobotics

import java.nio.{ByteBuffer, DirectBufferAccess}
import java.nio.charset.Charset

import scala.collection.mutable
import scala.scalanative.native._
import scala.language.experimental.macros

package object scalanativejni {
  class _env
  type Env = Ptr[_env]

  class _vm
  type VM = Ptr[_vm]

  class _cls
  type Cls = Ptr[_cls]

  case class JClass(name: String, methods: Map[(String, String), JMethodID])
  abstract class JMethodID {
    def run(args: Ptr[Byte]): Any
  }

  private val knownClasses = mutable.Map[String, JClass]()
  def registerClass(cls: JClass): Unit = {
    knownClasses(cls.name) = cls
  }

  def autoClass[T]: JClass = macro JNIMacrosImpl.autoClassImpl[T]

  def newString(env: Env, cstr: CString, len: Int): String = {
    val bytesCount = len * 2
    val bytes = new Array[Byte](bytesCount)

    var c = 0
    while (c < bytesCount) {
      bytes(c) = !(cstr + c + 1) // reversed because little endian vs big endian
      bytes(c + 1) = !(cstr + c)
      c += 2
    }

    new String(bytes, Charset.forName("UTF-16"))
  }

  def getStringCritical(env: Env, str: String): Ptr[Byte] = {
    val bytes = str.getBytes(Charset.forName("UTF-16"))
    val cstr  = stdlib.malloc(bytes.length + 2)

    var c = 0
    while (c < bytes.length) {
      !(cstr + c + 1) = bytes(c) // reversed because little endian vs big endian
      !(cstr + c) = bytes(c + 1)
      c += 2
    }

    !(cstr + bytes.length) = 0.toByte // NUL
    !(cstr + (bytes.length + 1)) = 0.toByte // NUL

    cstr
  }

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

  def newDirectByteBuffer(env: Env, address: Ptr[Byte], capacity: Long): ByteBuffer = {
    DirectBufferAccess.createFromPointer(address, capacity.toInt)
  }

  def findClass(env: Env, cname: CString): JClass = {
    val name = fromCString(cname)
    knownClasses.getOrElse(
      name,
      {
        println(s"WARNING: Stubbing JClass for $name")
        JClass(name, Map.empty)
      }
    )
  }

  def getMethodID(env: Env, clazz: JClass, cname: CString, csig: CString): JMethodID = {
    val name = fromCString(cname)
    val sig = fromCString(csig)

    clazz.methods.getOrElse(
      (name, sig),
      {
        println(s"WARNING Stubbing JMethodID for method $name with signature $sig in class $clazz")
        new JMethodID() {
          override def run(args: Ptr[Byte]): Any = null
        }
      }
    )
  }

  val env: Env = MockJNI.createEnv(
    findClass = (env: Env, cname: CString) => {
      findClass(env, cname)
    },
    newGlobalRef = (env: Env, obj: Object) => {
      println(s"creating new $obj global ref")
      obj
    },
    deleteLocalRef = (env: Env, obj: Object) => {
      println(s"deleting $obj local ref")
    },
    deleteGlobalRef = (env: Env, obj: Object) => {
      println(s"deleting $obj global ref")
    },
    newObjectV = (env: Env, cls: JClass, constructor: JMethodID, args: Ptr[Byte]) => {
      constructor.run(args).asInstanceOf[Object]
    },
    getMethodID = (env: Env, clazz: JClass, name: CString, sig: CString) => {
      getMethodID(env, clazz, name, sig)
    },

    _throw = (env: Env, obj: Throwable) => {
      throw obj
    },
    throwNew = (env: Env, cls: JClass, msg: CString) => {
      throw getMethodID(env, cls, c"<init>", c"").run(msg.cast[Ptr[Byte]]).asInstanceOf[Throwable]
    },

    (env: Env, nativeString: CString, length: Int) => {
      newString(env, nativeString, length)
    },
    (env: Env, str: String) => str.length,
    (env: Env, str: String) => getStringCritical(env, str),
    (env: Env, str: String, cStr: Ptr[Byte]) => stdlib.free(cStr),

    (env: Env, to: Array[Short], start: Int, len: Int, buf: Ptr[Short]) => {
      setShortArrayRegion(env, to, start, len, buf)
    },
    (env: Env, to: Array[Float], start: Int, len: Int, buf: Ptr[Float]) => {
      setFloatArrayRegion(env, to, start, len, buf)
    },
    (env: Env, arr: Array[_]) => {
      arr.length
    },

    (env: Env, address: Ptr[Byte], capacity: Long) => {
      newDirectByteBuffer(env, address, capacity)
    },
    (env: Env, buffer: ByteBuffer) => {
      DirectBufferAccess.getAttachment(buffer).cast[Ptr[Byte]]
    },
    (env: Env, buffer: ByteBuffer) => {
      buffer.capacity().toLong
    }
  )

  val vm: VM = MockJNI.createVM(env)
  val cls: Cls = null

  def jni[T]: T = macro JNIMacrosImpl.jniImpl[T]

  @extern
  object MockJNI {
    def createEnv(findClass: CFunctionPtr2[Env, CString, JClass],
                  newGlobalRef: CFunctionPtr2[Env, Object, Object],
                  deleteLocalRef: CFunctionPtr2[Env, Object, Unit],
                  deleteGlobalRef: CFunctionPtr2[Env, Object, Unit],
                  newObjectV: CFunctionPtr4[Env, JClass, JMethodID, Ptr[Byte], Object],
                  getMethodID: CFunctionPtr4[Env, JClass, CString, CString, JMethodID],

                  _throw: CFunctionPtr2[Env, Throwable, Int],
                  throwNew: CFunctionPtr3[Env, JClass, CString, Int],

                  newString: CFunctionPtr3[Env, CString, Int, String],
                  getStringLength: CFunctionPtr2[Env, String, Int],
                  getStringCritical: CFunctionPtr2[Env, String, Ptr[Byte]],
                  releaseStringCritical: CFunctionPtr3[Env, String, Ptr[Byte], Unit],

                  setShortArrayRegion: CFunctionPtr5[Env, Array[Short], Int, Int, Ptr[Short], Unit],
                  setFloatArrayRegion: CFunctionPtr5[Env, Array[Float], Int, Int, Ptr[Float], Unit],
                  getArrayLength: CFunctionPtr2[Env, Array[_], Int],

                  newDirectByteBuffer: CFunctionPtr3[Env, Ptr[Byte], Long, ByteBuffer],
                  getDirectBufferAddress: CFunctionPtr2[Env, ByteBuffer, Ptr[Byte]],
                  getDirectBufferCapacity: CFunctionPtr2[Env, ByteBuffer, Long]): Env = extern
    def createVM(env: Env): VM = extern
    def testVM(vm: VM, env: Env, buf: ByteBuffer): Unit = extern
  }
}
