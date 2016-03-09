package dao

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.{Rep, TableQuery}

import scala.concurrent.Future
import scala.concurrent.Future.successful

trait DefaultDAO[ID, E, Z <: Table[E]] extends BaseDAO[ID, E] {
  this: HasDatabaseConfigProvider[JdbcProfile] with HasExecutionContext =>

  // @formatter:off
  def q: TableQuery[Z]
  def filterIds(e: Seq[ID]): Query[Z, Z#TableElementType, Seq]
  def getId(e: E): ID
  def id(): Query[Rep[ID], ID, Seq]
  // @formatter:on

  def insertSeq(e: Seq[E]): Future[Seq[ID]] =
    if (e.nonEmpty) db.run((q returning id()) ++= e) else successful(Seq.empty[ID])

  def insertForce(e: Seq[E]): Future[Unit] =
    if (e.nonEmpty) db.run(q ++= e) map (_ => ()) else successful((): Unit)

  def delete(): Future[Int] =
    db.run(q.delete)

  def delete(ids: Seq[ID]): Future[Int] =
    if (ids.nonEmpty) db.run(filterIds(ids).delete) else successful(0)

  def update(e: E): Future[Int] =
    db.run(filterIds(getId(e) :: Nil).update(e))

  def seq(offset: Int, limit: Int): Future[Seq[E]] =
    db.run(q.drop(offset).take(limit).result)

  def byId(e: Seq[ID]): Future[Seq[E]] =
    db.run(filterIds(e).result)

  def count(): Future[Int] =
    db.run(q.countDistinct.result)

  def whereOne(f: Z => Rep[Boolean]): Future[E] =
    whereSeq(f) map (_.head)

  def whereOpt(f: Z => Rep[Boolean]): Future[Option[E]] =
    whereSeq(f) map (_.headOption)

  def whereSeq(f: Z => Rep[Boolean]): Future[Seq[E]] =
    db.run(q.filter(f).result)

}