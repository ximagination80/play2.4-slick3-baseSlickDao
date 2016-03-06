package dao

import scala.concurrent.{ExecutionContext, Future}

trait HasExecutionContext {
  implicit def executionContext: ExecutionContext
}

trait InsertDAO[ID, E] {
  this: HasExecutionContext =>

  def insert(e: Seq[E]): Future[Seq[ID]]

  def insert(e: E): Future[ID] =
    insert(e :: Nil) map (_.head)
}

trait UpdateDAO[ID, E] {
  def update(e: E): Future[Long]
}

trait DeleteDAO[ID, E] {
  this: IdExtractor[ID, E] =>

  def delete(ids: Seq[ID]): Future[Long]
  def delete(): Future[Long]

  def delete(id: ID): Future[Long] =
    delete(id :: Nil)

  def deleteObject(e: E): Future[Long] =
    delete(getId(e))
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
  def getId(e: E): ID
}

trait BaseDAO[ID, E]
  extends InsertDAO[ID, E]
  with UpdateDAO[ID, E]
  with DeleteDAO[ID, E]
  with QueryDAO[ID, E]
  with IdExtractor[ID, E]
  with Countable
  with HasExecutionContext
