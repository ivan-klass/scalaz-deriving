// Copyright: 2017 Sam Halliday
// License: https://opensource.org/licenses/BSD-3-Clause

package scalaz

import java.lang.String

import scala.{ inline, Boolean, Int }

// a simple covariant typeclass
trait Default[A] {
  def default: A
}
object Default {
  @inline def apply[A](implicit i: Default[A]): Default[A] = i
  @inline def instance[A](a: => A): Default[A] = new Default[A] {
    override def default: A = a
  }

  implicit val int: Default[Int]         = instance(0)
  implicit val string: Default[String]   = instance("")
  implicit val boolean: Default[Boolean] = instance(false)

  implicit val Derived: Derived[Default] =
    new CovariantDerived[Default] {
      override def point[A](a: => A): Default[A] = instance(a)
      override def apply2[A1, A2, Z](a1: => Default[A1], a2: => Default[A2])(
        f: (A1, A2) => Z
      ): Default[Z] = instance(f(a1.default, a2.default))

      override def coapply1[Z, A1](a1: => Default[A1])(f: A1 => Z): Default[Z] =
        instance(f(a1.default))
      override def coapply2[Z, A1, A2](a1: => Default[A1], a2: => Default[A2])(
        f: A1 \/ A2 => Z
      ): Default[Z] = instance(f(-\/(a1.default)))

    }

}
