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
    def runOn(obj: Object, args: Ptr[Byte]): Any
  }

  private val knownClasses = mutable.Map[String, JClass]()
  def registerClass(cls: JClass): Unit = {
    knownClasses(cls.name) = cls
  }

  def autoClass[T]: JClass = macro JNIMacrosImpl.autoClassImpl[T]

  val utf16Charset = Charset.forName("UTF-16LE")

  def newString(env: Env, cstr: CString, len: Int): String = {
    val bytesCount = len * 2
    val bytes = new Array[Byte](bytesCount)

    var c = 0
    while (c < bytesCount) {
      bytes(c) = !(cstr + c)
      c += 1
    }

    new String(bytes, utf16Charset)
  }

  def getStringCritical(env: Env, str: String): Ptr[Byte] = {
    val bytes = str.getBytes(utf16Charset)
    val cstr  = stdlib.malloc(bytes.length + 2)

    var c = 0
    while (c < bytes.length) {
      !(cstr + c) = bytes(c)
      c += 1
    }

    !(cstr + bytes.length) = 0.toByte // NUL
    !(cstr + (bytes.length + 1)) = 0.toByte // NUL

    cstr
  }

  def setByteArrayRegion(env: Env, to: Array[Byte], start: Int, len: Int, buf: Ptr[Byte]): Unit = {
    (0 until len).foreach { fromIndex =>
      to(start + fromIndex) = !(buf + fromIndex)
    }
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
          override def run(args: Ptr[Byte]): Ptr[Byte] = null
          override def runOn(obj: Object, args: Ptr[CSignedChar]): Ptr[Byte] = null
        }
      }
    )
  }

  def getByteArrayElements(env: Env, byteArray: Array[Byte], something: Ptr[Boolean]) = {
    val ret = stdlib.malloc(byteArray.length)
    (0 until byteArray.length).foreach { i =>
      !(ret + i) = byteArray(i)
    }
    ret
  }

  private var globalRefs = List[Object]()

  def newGlobalRef(env: Env, obj: Object): Object = {
    globalRefs = obj :: globalRefs
    obj
  }

  def deleteGlobalRef(env: Env, obj: Object): Unit = {
    globalRefs = globalRefs.filterNot(_ eq obj)
  }

  val env: Env = MockJNI.createEnv(
    findClass = (env: Env, cname: CString) => {
      findClass(env, cname)
    },
    newGlobalRef = (env: Env, obj: Object) => {
      newGlobalRef(env, obj)
    },
    deleteLocalRef = (env: Env, obj: Object) => {
    },
    deleteGlobalRef = (env: Env, obj: Object) => {
      deleteGlobalRef(env, obj)
    },
    newObjectV = (env: Env, cls: JClass, constructor: JMethodID, args: Ptr[Byte]) => {
      constructor.run(args)
    },
    callMethodV = (env: Env, obj: Object, method: JMethodID, args: Ptr[Byte]) => {
      method.runOn(obj, args)
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

    (env: Env, to: Array[Byte], start: Int, len: Int, buf: Ptr[Byte]) => {
      setByteArrayRegion(env, to, start, len, buf)
    },
    (env: Env, to: Array[Short], start: Int, len: Int, buf: Ptr[Short]) => {
      setShortArrayRegion(env, to, start, len, buf)
    },
    (env: Env, to: Array[Float], start: Int, len: Int, buf: Ptr[Float]) => {
      setFloatArrayRegion(env, to, start, len, buf)
    },
    (env: Env, byteArray: Array[Byte], something: Ptr[Boolean]) => {
      getByteArrayElements(env, byteArray, something)
    },
    (env: Env, byteArray: Array[Byte], mem: Ptr[Byte], mode: Int) => {
      stdlib.free(mem)
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
    },
    (env: Env, size: Int, cls: JClass, obj: Object) => {
      new Array[Object](size)
    },
    (env: Env, arr: Array[Object], idx: Int, obj: Object) => {
      arr(idx) = obj
    }
  )

  val vm: VM = MockJNI.createVM(env)
  val cls: Cls = null

  def jni[T]: T = throw new IllegalStateException("bad")

  def loadJNILibrary(libFile: CString): Unit = {
    val fp = DL.dlsym(DL.dlopen(libFile, 0x002 /* RTLD_NOW */), c"JNI_OnLoad").cast[CFunctionPtr2[VM, Ptr[Unit], Unit]]
    fp(vm, null)
  }

  @extern
  object MockJNI {
    def createEnv(findClass: CFunctionPtr2[Env, CString, JClass],
                  newGlobalRef: CFunctionPtr2[Env, Object, Object],
                  deleteLocalRef: CFunctionPtr2[Env, Object, Unit],
                  deleteGlobalRef: CFunctionPtr2[Env, Object, Unit],
                  newObjectV: CFunctionPtr4[Env, JClass, JMethodID, Ptr[Byte], Any],
                  callMethodV: CFunctionPtr4[Env, Object, JMethodID, Ptr[Byte], Any],
                  getMethodID: CFunctionPtr4[Env, JClass, CString, CString, JMethodID],

                  _throw: CFunctionPtr2[Env, Throwable, Int],
                  throwNew: CFunctionPtr3[Env, JClass, CString, Int],

                  newString: CFunctionPtr3[Env, CString, Int, String],
                  getStringLength: CFunctionPtr2[Env, String, Int],
                  getStringCritical: CFunctionPtr2[Env, String, Ptr[Byte]],
                  releaseStringCritical: CFunctionPtr3[Env, String, Ptr[Byte], Unit],

                  setByteArrayRegion: CFunctionPtr5[Env, Array[Byte], Int, Int, Ptr[Byte], Unit],
                  setShortArrayRegion: CFunctionPtr5[Env, Array[Short], Int, Int, Ptr[Short], Unit],
                  setFloatArrayRegion: CFunctionPtr5[Env, Array[Float], Int, Int, Ptr[Float], Unit],
                  getByteArrayElements: CFunctionPtr3[Env, Array[Byte], Ptr[Boolean], Ptr[Byte]],
                  releaseByteArrayElements: CFunctionPtr4[Env, Array[Byte], Ptr[Byte], Int, Unit],
                  getArrayLength: CFunctionPtr2[Env, Array[_], Int],

                  newDirectByteBuffer: CFunctionPtr3[Env, Ptr[Byte], Long, ByteBuffer],
                  getDirectBufferAddress: CFunctionPtr2[Env, ByteBuffer, Ptr[Byte]],
                  getDirectBufferCapacity: CFunctionPtr2[Env, ByteBuffer, Long],
                  newObjectArray: CFunctionPtr4[Env, Int, JClass, Object, Array[Object]],
                  setObjectArrayElement: CFunctionPtr4[Env, Array[Object], Int, Object, Unit]): Env = extern
    def createVM(env: Env): VM = extern
    def testVM(vm: VM, env: Env): Unit = extern
  }
}
