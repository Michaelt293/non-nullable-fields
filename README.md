# non-nullable-fields

This library adds `null`-check assertions to class definitions to ensure that class fields do not contain `null`s. This may be desirable when using Java libraries or Spark.

## Quick start

Add the following to to your `build.sbt` file -

```
libraryDependencies += "io.github.michaelt293" % "non-nullable-fields_$scalaVersion" % "0.1.0"
```

## Example usage

To add `null`-check assertions to class definitions, use the `nonNullableFields` annotation macro.

```scala
import io.github.michaelt293.nonNullableFields

@nonNullableFields
case class Person(name: String, age: Int, numberOfPets: Option[Int])
```

If `null`-check assertions are not required for optional fields, set the `ignoreOptionalFields` parameter to `true`.

```scala
@nonNullableFields(ignoreOptionalFields = true)
case class Person(name: String, age: Int, numberOfPets: Option[Int])
```

The `nonNullableFields` annotation macro can also be used on implicit classes.

```scala
@nonNullableFields
implicit class StringOps(private val str: String) {
  def clean: String = str.filter(_.isLetterOrDigit)
}
```

## Macro expansion

At compile time, the macro annotations above generate the following code. Note, a `null`-check assertion is not required for the `age` field since `Int` is a value class and therefore cannot be `null`.

```scala
case class Person(name: String, age: Int, numberOfPets: Option[Int]) {
  assert(name.!=(null), "Field, name, from Person cannot be null");
  assert(numberOfPets.!=(null), "Field, numberOfPets, from Person cannot be null")
}

case class Person(name: String, age: Int, numberOfPets: Option[Int]) {
  assert(name.!=(null), "Field, name, from Person cannot be null")
}

implicit class StringOps(private val str: String) {
  assert(str.!=(null), "Field, str, from StringOps cannot be null");
  def clean: String = str.filter(((x$1) => x$1.isLetterOrDigit))
}
```
