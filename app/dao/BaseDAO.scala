package dao

import scala.concurrent.{ExecutionContext, Future}

trait HasExecutionContext {
  implicit def executionContext: ExecutionContext
}

trait InsertDAO[ID, E] {
  this: HasExecutionContext =>

  def insertSeq(e: Seq[E]): Future[Seq[ID]]
  def insertForce(e: Seq[E]): Future[Unit]

  def insert(e: E): Future[ID] =
    insertSeq(e :: Nil) map (_.head)
}

trait UpdateDAO[ID, E] {
  def update(e: E): Future[Int]
}

trait DeleteDAO[ID, E] {
  this: IdExtractor[ID, E] =>

  def delete(ids: Seq[ID]): Future[Int]
  def delete(): Future[Int]

  def delete(id: ID): Future[Int] =
    delete(id :: Nil)

  def deleteObject(e: E): Future[Int] =
    getId(e).fold(Future.successful(0))(delete)
}

trait QueryDAO[ID, E] {
  this: HasExecutionContext =>

  def byId(ids: Seq[ID]): Future[Seq[E]]
  def seq(offset: Int, limit: Int): Future[Seq[E]]

  def byId(id: ID): Future[E] =
    byIdOpt(id) map (_.get)

  def byIdOpt(id: ID): Future[Option[E]] =
    byId(id :: Nil) map (_.headOption)

  def seq(limit: Int = Int.MaxValue): Future[Seq[E]] =
    seq(offset = 0, limit)
}

trait Countable {
  def count(): Future[Int]
}

trait IdExtractor[ID, E] {
  def getId(e: E): Option[ID]
}

trait BaseDAO[ID, E]
  extends InsertDAO[ID, E]
  with UpdateDAO[ID, E]
  with DeleteDAO[ID, E]
  with QueryDAO[ID, E]
  with IdExtractor[ID, E]
  with Countable
  with HasExecutionContext
