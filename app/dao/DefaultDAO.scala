package dao

import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.defaultContext
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.{Rep, TableQuery}

import scala.concurrent.Future.successful
import scala.concurrent.{ExecutionContext, Future}

trait DefaultDAO[ID, E, Z <: Table[E]] extends BaseDAO[ID, E] with HasExecutionContext {
  this: HasDatabaseConfigProvider[JdbcProfile] =>

  // @formatter:off
  def q: TableQuery[Z]
  def filterIds(e: Seq[ID]): Query[Z, Z#TableElementType, Seq]
  def getId(e: E): Option[ID]
  def id(): Query[Rep[ID], ID, Seq]
  // @formatter:on

  def insertSeq(e: Seq[E]): Future[Seq[ID]] =
    if (e.nonEmpty) db.run(((q returning id()) ++= e).transactionally) else successful(Seq.empty[ID])

  def insertForce(e: Seq[E]): Future[Unit] =
    if (e.nonEmpty) db.run((q ++= e).transactionally) map (_ => ()) else successful((): Unit)

  def delete(): Future[Int] =
    db.run(q.delete.transactionally)

  def delete(ids: Seq[ID]): Future[Int] =
    if (ids.nonEmpty) db.run(filterIds(ids).delete.transactionally) else successful(0)

  def update(e: E): Future[Int] =
    getId(e).fold(successful(0))(id => db.run(filterIds(id :: Nil).update(e).transactionally))

  def seq(offset: Int, limit: Int): Future[Seq[E]] =
    db.run(q.drop(offset).take(limit).result.transactionally)

  def byId(e: Seq[ID]): Future[Seq[E]] =
    db.run(filterIds(e).result.transactionally)

  def count(): Future[Int] =
    db.run(q.countDistinct.result.transactionally)

  def whereOne(f: Z => Rep[Boolean]): Future[E] =
    whereSeq(f) map (_.head)

  def whereOpt(f: Z => Rep[Boolean]): Future[Option[E]] =
    whereSeq(f) map (_.headOption)

  def whereSeq(f: Z => Rep[Boolean]): Future[Seq[E]] =
    db.run(q.filter(f).result.transactionally)

  // play execution context by default
  implicit def executionContext: ExecutionContext = defaultContext
}