package dao

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._
import slick.lifted.{TableQuery, Rep}

import scala.concurrent.{ExecutionContext, Future}

trait DefaultDAO[ID, E, Z <: Table[E]] extends BaseDAO[ID, E] {
  this: HasDatabaseConfigProvider[JdbcProfile] with HasExecutionContext =>

  // @formatter:off
  def q: TableQuery[Z]
  def filterIds(e: Seq[ID]): Query[Z, Z#TableElementType, Seq]
  def getId(e: E): ID
  def id(): Query[Rep[ID], ID, Seq]
  // @formatter:on

  def insert(e: Seq[E]): Future[Seq[ID]] = e.nonEmpty match {
    case true => db.run((q returning id()) ++= e)
    case false => Future.successful(Seq.empty[ID])
  }

  def delete(): Future[Long] =
    db.run(q.delete) map (_.toLong)

  def delete(pks: Seq[ID]): Future[Long] = pks.nonEmpty match {
    case true => db.run(filterIds(pks).delete) map (_.toLong)
    case false => Future.successful(0)
  }

  def update(e: E): Future[Long] =
    db.run(filterIds(getId(e) :: Nil).update(e)) map (_.toLong)

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