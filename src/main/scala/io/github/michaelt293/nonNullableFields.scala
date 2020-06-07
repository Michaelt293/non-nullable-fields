package io.github.michaelt293

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

@compileTimeOnly("enable macro paradise to expand macro annotations")
class nonNullableFields(ignoreOptionalFields: Boolean = false)
    extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro nonNullableFieldsMacro.impl
}

object nonNullableFieldsMacro {
  val valueClasses: Set[String] =
    Set(
      "Float",
      "Double",
      "Byte",
      "Short",
      "Int",
      "Long",
      "Boolean",
      "Char",
      "Unit"
    )

  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val ignoreOptionalFields: Boolean = c.prefix.tree match {
      case q"new nonNullableFields(ignoreOptionalFields = $value)" =>
        c.eval[Boolean](c.Expr(value))
      case q"new nonNullableFields()" => false
      case _ =>
        c.abort(
          c.enclosingPosition,
          "use named arguments with the @nonNullableFields macro annotation to override default"
        )
    }

    def isValueClass(ty: String): Boolean =
      valueClasses.contains(ty)

    def isOptional(ty: String): Boolean =
      ty.startsWith("Option[")

    def createAssertion(typeName: TypeName, valDef: ValDef) =
      valDef match {
        case q"$mods val $tname: $tpt = $expr" =>
          if ((!isValueClass(tpt.toString) && !isOptional(tpt.toString))
              || (isOptional(tpt.toString) && !ignoreOptionalFields)) {
            val message = s"Field, $tname, from $typeName cannot be null"
            Some(q"assert($tname != null, $message)")
          } else None
        case _ => c.abort(c.enclosingPosition, "unexpected annotation pattern!")
      }

    val result: List[Tree] =
      annottees.map(_.tree).toList match {
        case q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" :: tail =>
          val assertions = paramss.flatten
            .flatMap(parameter => createAssertion(tpname, parameter))

          if (assertions.isEmpty)
            c.warning(
              c.enclosingPosition,
              s"no assertions added to ${tpname.toString}"
            )

          q"$mods class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..${assertions ++ stats} }" :: tail

        case tree =>
          c.error(
            tree.head.pos,
            "@nonNullableFields can only be used with classes"
          )
          tree
      }

    c.Expr[Any](Block(result, Literal(Constant(()))))
  }
}
