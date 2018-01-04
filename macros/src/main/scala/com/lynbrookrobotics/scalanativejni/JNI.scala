package com.lynbrookrobotics.scalanativejni

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

case class jnilib(library: String) extends scala.annotation.StaticAnnotation

object JNIMacrosImpl {
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
      if (param.typeSignature.typeSymbol.fullName == "java.lang.String") {
        q"val ${param.name.asInstanceOf[TermName]}: com.lynbrookrobotics.scalanativejni.JString"
      } else {
        q"val ${param.name.asInstanceOf[TermName]}: ${param.typeSignature}"
      }
    }

    val paramExprs = paramss.map { p =>
      if (p.typeSignature.typeSymbol.fullName == "java.lang.String") {
        q"_root_.com.lynbrookrobotics.scalanativejni.string2jString(${p.asTerm.name})"
      } else {
        q"${p.asTerm.name}"
      }
    }

    val origRetType = method.returnType
    val jniRetType = if (origRetType.typeSymbol.fullName == "java.lang.String") {
      tq"_root_.com.lynbrookrobotics.scalanativejni.JString"
    } else tq"${origRetType.typeSymbol}"

    val linkerObject =
      q"""
         @_root_.scala.scalanative.native.extern @_root_.scala.scalanative.native.link(${linkLibrary.tree}) object linker {
           @_root_.scala.scalanative.native.name(${Literal(Constant(jniName))})
           def native(env: _root_.com.lynbrookrobotics.scalanativejni.Env, cls: _root_.com.lynbrookrobotics.scalanativejni.Cls, ..$jniParams): $jniRetType = _root_.scala.scalanative.native.extern
         }
       """

    val coreRet = q"linker.native(_root_.com.lynbrookrobotics.scalanativejni.env, _root_.com.lynbrookrobotics.scalanativejni.cls, ..$paramExprs)"
    val ret = if (origRetType.typeSymbol.fullName == "java.lang.String") {
      q"_root_.com.lynbrookrobotics.scalanativejni.jString2String($coreRet)"
    } else coreRet

    c.Expr[T](
      q"""
         $linkerObject
         $ret
       """)
  }
}
