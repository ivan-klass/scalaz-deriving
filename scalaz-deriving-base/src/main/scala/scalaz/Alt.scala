// Copyright: 2017 - 2020 Sam Halliday
// License: https://opensource.org/licenses/BSD-3-Clause

package scalaz

import scala.inline
import scala.Predef.identity

////
// Copyright: 2018 Sam Halliday
// License: https://opensource.org/licenses/BSD-3-Clause

/**
 * https://hackage.haskell.org/package/semigroupoids-5.2.2/docs/Data-Functor-Alt.html
 */
////
trait Alt[F[_]] extends Applicative[F] with InvariantAlt[F] { self =>
  ////

  def alt[A](a1: =>F[A], a2: =>F[A]): F[A]

  /** One or none */
  def optional[A](fa: F[A]): F[Maybe[A]] =
    alt(map(fa)(Maybe.just(_)), pure(Maybe.empty))

  def altly1[Z, A1](a1: =>F[A1])(f: A1 => Z): F[Z] = map(a1)(f)
  def altly2[Z, A1, A2](a1: =>F[A1], a2: =>F[A2])(f: A1 \/ A2 => Z): F[Z] =
    map(alt(map(a1)(\/.left[A1, A2](_)), map(a2)(\/.right[A1, A2](_))))(f)

  def altly3[Z, A1, A2, A3](a1: =>F[A1], a2: =>F[A2], a3: =>F[A3])(
    f: A1 \/ (A2 \/ A3) => Z
  ): F[Z] = altly2(a1, altly2(a2, a3)(identity))(f)
  def altly4[Z, A1, A2, A3, A4](
    a1: =>F[A1],
    a2: =>F[A2],
    a3: =>F[A3],
    a4: =>F[A4]
  )(
    f: A1 \/ (A2 \/ (A3 \/ A4)) => Z
  ): F[Z] = altly2(a1, altly3(a2, a3, a4)(identity))(f)
  // ... altlyN

  // equivalent of tupleN
  def either2[A1, A2](a1: =>F[A1], a2: =>F[A2]): F[A1 \/ A2] =
    altly2(a1, a2)(identity)
  // ... eitherN

  final def altlying1[Z, A1](
    f: A1 => Z
  )(implicit a1: F[A1]): F[Z] =
    altly1(a1)(f)
  final def altlying2[Z, A1, A2](
    f: A1 \/ A2 => Z
  )(implicit a1: F[A1], a2: F[A2]): F[Z] =
    altly2(a1, a2)(f)
  final def altlying3[Z, A1, A2, A3](
    f: A1 \/ (A2 \/ A3) => Z
  )(implicit a1: F[A1], a2: F[A2], a3: F[A3]): F[Z] =
    altly3(a1, a2, a3)(f)
  final def altlying4[Z, A1, A2, A3, A4](
    f: A1 \/ (A2 \/ (A3 \/ A4)) => Z
  )(implicit a1: F[A1], a2: F[A2], a3: F[A3], a4: F[A4]): F[Z] =
    altly4(a1, a2, a3, a4)(f)
  // ... altlyingX

  override def xcoproduct1[Z, A1](a1: =>F[A1])(
    f: A1 => Z,
    g: Z => A1
  ): F[Z] = altly1(a1)(f)
  override def xcoproduct2[Z, A1, A2](a1: =>F[A1], a2: =>F[A2])(
    f: (A1 \/ A2) => Z,
    g: Z => (A1 \/ A2)
  ): F[Z] = altly2(a1, a2)(f)
  override def xcoproduct3[Z, A1, A2, A3](
    a1: =>F[A1],
    a2: =>F[A2],
    a3: =>F[A3]
  )(
    f: (A1 \/ (A2 \/ A3)) => Z,
    g: Z => (A1 \/ (A2 \/ A3))
  ): F[Z] = altly3(a1, a2, a3)(f)
  override def xcoproduct4[Z, A1, A2, A3, A4](
    a1: =>F[A1],
    a2: =>F[A2],
    a3: =>F[A3],
    a4: =>F[A4]
  )(
    f: (A1 \/ (A2 \/ (A3 \/ A4))) => Z,
    g: Z => (A1 \/ (A2 \/ (A3 \/ A4)))
  ): F[Z] = altly4(a1, a2, a3, a4)(f)

  // from Apply in scalaz 7.3
  final def applying1[Z, A1](f: A1 => Z)(
    implicit a1: F[A1]
  ): F[Z] = map(a1)(f)
  final def applying2[Z, A1, A2](
    f: (A1, A2) => Z
  )(implicit a1: F[A1], a2: F[A2]): F[Z] =
    apply2(a1, a2)(f)
  final def applying3[Z, A1, A2, A3](
    f: (A1, A2, A3) => Z
  )(implicit a1: F[A1], a2: F[A2], a3: F[A3]): F[Z] =
    apply3(a1, a2, a3)(f)
  final def applying4[Z, A1, A2, A3, A4](
    f: (A1, A2, A3, A4) => Z
  )(implicit a1: F[A1], a2: F[A2], a3: F[A3], a4: F[A4]): F[Z] =
    apply4(a1, a2, a3, a4)(f)
  //

  override def xproduct0[Z](z: =>Z): F[Z] = pure(z)
  override def xproduct1[Z, A1](a1: =>F[A1])(f: A1 => Z, g: Z => A1): F[Z] =
    xmap(a1, f, g)
  override def xproduct2[Z, A1, A2](a1: =>F[A1], a2: =>F[A2])(
    f: (A1, A2) => Z,
    g: Z => (A1, A2)
  ): F[Z] = apply2(a1, a2)(f)
  override def xproduct3[Z, A1, A2, A3](a1: =>F[A1], a2: =>F[A2], a3: =>F[A3])(
    f: (A1, A2, A3) => Z,
    g: Z => (A1, A2, A3)
  ): F[Z] = apply3(a1, a2, a3)(f)
  override def xproduct4[Z, A1, A2, A3, A4](
    a1: =>F[A1],
    a2: =>F[A2],
    a3: =>F[A3],
    a4: =>F[A4]
  )(
    f: (A1, A2, A3, A4) => Z,
    g: Z => (A1, A2, A3, A4)
  ): F[Z] = apply4(a1, a2, a3, a4)(f)

  trait AltLaw extends ApplicativeLaw {
    // <!> is associative:             (a <!> b) <!> c = a <!> (b <!> c)
    // <$> left-distributes over <!>:  f <$> (a <!> b) = (f <$> a) <!> (f <$> b)
  }
  def altLaw: AltLaw = new AltLaw {}

  ////
}

object Alt {
  @inline def apply[F[_]](implicit F: Alt[F]): Alt[F] = F

  ////

  ////
}
