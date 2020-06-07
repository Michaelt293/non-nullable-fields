package io.github.michaelt293

import scala.util.Try

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.TryValues

class nonNullableFieldsSpec extends AnyFlatSpec with Matchers with TryValues {
  import nonNullableFieldsSpec._

  "Constructing a Person" should "throw an AssertionError exception with a null name" in {
    personNullName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "throw an AssertionError exception with a null optional field" in {
    personNullNumberOfPets.failure.exception should have message
      "assertion failed: Field, numberOfPets, from Person cannot be null"
  }

  "Constructing a Person (with default)" should "throw an AssertionError exception with a null name" in {
    personDefaultNullName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "throw an AssertionError exception with a null optional field" in {
    personDefaultNullNumberOfPets.failure.exception should have message
      "assertion failed: Field, numberOfPets, from Person cannot be null"
  }

  "Constructing a Person (with companion object)" should "throw an AssertionError exception with a null name" in {
    personCompanionNullName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "throw an AssertionError exception with a null optional field" in {
    personCompanionNullNumberOfPets.failure.exception should have message
      "assertion failed: Field, numberOfPets, from Person cannot be null"
  }

  "Constructing a Person with ignore optional fields set to true" should
    "throw an AssertionError exception with a null name" in {
    personIgnoreOptionsNullName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "not throw an exception with a null optional field" in {
    noException should be thrownBy IgnoreOptions.Person("Luke Star", 55, null)
  }

  "Using copy with a Person" should "throw an AssertionError exception with a null name" in {
    personCopyName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "throw an AssertionError exception with a null optional field" in {
    personCopyNumberOfPets.failure.exception should have message
      "assertion failed: Field, numberOfPets, from Person cannot be null"
  }

  "Using copy with a Person with ignoreOptionalFields set to true" should
    "throw an AssertionError exception with a null name" in {
    personIgnoreOptionsCopyName.failure.exception should have message
      "assertion failed: Field, name, from Person cannot be null"
  }

  it should "not throw an exception with a null optional field" in {
    noException should be thrownBy
      IgnoreOptions.Person("Luke Star", 55, Some(1)).copy(numberOfPets = null)
  }

  "nonNullableFields macro" should "not compile when applied to a trait" in {
    "@nonNullableFields trait TestTrait" shouldNot compile
  }

  it should "compile when applied to a class" in {
    "@nonNullableFields class Teacher(name: String)" should compile
  }

  it should "compile when applied to a final case class" in {
    "@nonNullableFields final case class Teacher(name: String)" should compile
  }

  it should "not compile with non-named argument" in {
    "@nonNullableFields(true) case class Teacher(name: String)" shouldNot compile
  }

  it should "work with implicit classes" in {
    extensionMethodNullTest.failure.exception should have message
      "assertion failed: Field, str, from StringOps cannot be null"

    "5 */50621".clean shouldEqual "550621"
  }

  it should "not remove methods or alter from companion object" in {
    val result = Companion.Person.make("Bigfoot", 349)
    val expected = Some(Companion.Person("Bigfoot", 349, None))

    result shouldEqual expected
  }
}

object nonNullableFieldsSpec {
  @nonNullableFields(ignoreOptionalFields = false)
  case class Person(name: String, age: Int, numberOfPets: Option[Int])

  object IgnoreOptions {
    @nonNullableFields(ignoreOptionalFields = true)
    case class Person(name: String, age: Int, numberOfPets: Option[Int])
  }

  object Default {
    @nonNullableFields
    case class Person(name: String, age: Int, numberOfPets: Option[Int])
  }

  object Companion {
    @nonNullableFields
    case class Person(name: String, age: Int, numberOfPets: Option[Int])

    object Person {
      def make(name: String, age: Int): Option[Person] =
        if (name.nonEmpty && age >= 0) Some(Person(name, age, None)) else None
    }
  }

  @nonNullableFields
  implicit class StringOps(private val str: String) {
    def clean: String = str.filter(_.isLetterOrDigit)
  }

  val personNullName = Try(Person(null, 48, None))
  val personNullNumberOfPets = Try(Person("Charlie", 18, null))

  val personDefaultNullName = Try(Default.Person(null, 48, None))
  val personDefaultNullNumberOfPets = Try(Default.Person("Charlie", 18, null))

  val personCompanionNullName = Try(Companion.Person(null, 48, None))
  val personCompanionNullNumberOfPets =
    Try(Companion.Person("Charlie", 18, null))

  val personIgnoreOptionsNullName = Try(IgnoreOptions.Person(null, 48, None))

  val personCopyName =
    Try(Person("Charlie", 48, None).copy(name = null))

  val personCopyNumberOfPets =
    Try(Person("Charlie", 18, Some(5)).copy(numberOfPets = null))

  val personIgnoreOptionsCopyName =
    Try(IgnoreOptions.Person("Charlie", 48, None).copy(name = null))

  val nullString: String = null
  val extensionMethodNullTest = Try(nullString.clean)
}
