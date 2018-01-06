package com.lynbrookrobotics.scalanativejni

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

case class jnilib(library: String) extends scala.annotation.StaticAnnotation

object JNIMacrosImpl {
  def autoClassImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Tree = {
    import c.universe._

    val targetType = implicitly[c.WeakTypeTag[T]].tpe

    def sigStringForType(cls: Type): String = {
      cls.typeSymbol.fullName match {
        case "boolean" => "Z"
        case "byte" => "B"
        case "char" => "C"
        case "short" => "S"
        case "int" => "I"
        case "long" => "J"
        case "float" => "F"
        case "double" => "D"
        case "scala.Unit" => "V"
        case "scala.Array" =>
          s"[${sigStringForType(cls.typeArgs.head)}"
        case o =>
          s"L${o.split('.').mkString("/")};"
      }
    }

    val methodIds = targetType.members.toList
      .filter(m => m.isMethod && m.isPublic && !m.isSynthetic
        && m.owner.fullName != "java.lang.Object" && m.owner.fullName != "scala.Any").map { member =>
      val paramsString = member.asMethod.paramLists.head.map(_.typeSignature).map(sigStringForType).mkString

      if (member.isConstructor) {
        val name = "<init>"
        val signatureString = s"($paramsString)V"
        val constructorParamsStrings = member.asMethod.paramLists.head.map { param =>
          s"""{
             |  val r = !(a + position).cast[_root_.scala.scalanative.native.Ptr[${param.typeSignature.typeSymbol.fullName}]]
             |  position += _root_.scala.scalanative.native.sizeof[${param.typeSignature.typeSymbol.fullName}]
             |  r
             |}""".stripMargin
        }

        s"""("$name", "$signatureString") -> new _root_.com.lynbrookrobotics.scalanativejni.JMethodID {
           |  override def run(a: _root_.scala.scalanative.native.Ptr[Byte]): Any = {
           |    var position = 0
           |    new ${targetType.typeSymbol.fullName}(
           |      ${constructorParamsStrings.mkString(",\n")}
           |    )
           |  }
           |}""".stripMargin
      } else {
        val name = member.name.decodedName
        val signatureString = s"($paramsString)${sigStringForType(member.typeSignature.resultType)}"
        s"""("$name", "$signatureString") -> new _root_.com.lynbrookrobotics.scalanativejni.JMethodID {
           |  override def run(a: _root_.scala.scalanative.native.Ptr[Byte]): Any = {
           |    null
           |  }
           |}""".stripMargin
      }
    }

    c.parse(
      s"""_root_.com.lynbrookrobotics.scalanativejni.JClass(
         |  name = "java/lang/RuntimeException",
         |  methods = Map(
         |    ${methodIds.mkString(",\n")}
         |  )
         |)""".stripMargin
    )

//    val constructorMethodIDs: Seq[Tree] = targetType.con.getDeclaredConstructors.toList.map { c =>
//      val paramsString = c.getParameterTypes.toList.map(sigStringForType).mkString

//      println(signatureString)
//      q"null"
//    }

//    q"null"
  }

  def jniImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[T] = {
    import c.universe._

    val method = c.internal.enclosingOwner.asMethod
    val paramss: List[Symbol] = method.paramLists.flatten

    val enclosingObject = method.owner
    val linkLibrary = c.Expr[String](enclosingObject.annotations.find { annot =>
      annot.tree.tpe.typeSymbol.fullName == "com.lynbrookrobotics.scalanativejni.jnilib"
    }.get.tree.children.last)

    val objectPath = enclosingObject.fullName
    val methodName = method.name.toString

    val jniName = s"Java_${objectPath.replace('.', '_')}_$methodName"

    val jniParams = paramss.map { param =>
      q"val ${param.name.asInstanceOf[TermName]}: ${param.typeSignature}"
    }

    val paramExprs = paramss.map { p =>
      q"${p.asTerm.name}"
    }

    val linkerObject =
      q"""
         @_root_.scala.scalanative.native.extern @_root_.scala.scalanative.native.link(${linkLibrary.tree}) object linker {
           @_root_.scala.scalanative.native.name(${Literal(Constant(jniName))})
           def native(env: _root_.com.lynbrookrobotics.scalanativejni.Env,
                      cls: _root_.com.lynbrookrobotics.scalanativejni.Cls,
                      ..$jniParams): ${method.returnType.typeSymbol} =
             _root_.scala.scalanative.native.extern
         }
       """

    c.Expr[T](
      q"""
         $linkerObject
         linker.native(_root_.com.lynbrookrobotics.scalanativejni.env, _root_.com.lynbrookrobotics.scalanativejni.cls, ..$paramExprs)
       """)
  }
}
