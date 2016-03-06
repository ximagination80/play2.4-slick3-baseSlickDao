package dao

import javax.inject.{Inject, Singleton}

import models.Cat
import play.api.db.slick.HasDatabaseConfigProvider
import slick.lifted.TableQuery

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext

@Singleton()
class CatDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DefaultDAO[String, Cat, CatDef] {

  override val q = TableQuery[CatDef]

  def getId(e: Cat) = e.name

  // TODO make this better
  def filterIds(e: Seq[String]) = q.filter(_.name inSet e)

  implicit def executor: ExecutionContext =
    play.api.libs.concurrent.Execution.Implicits.defaultContext
}

class CatDef(tag: Tag) extends Table[Cat](tag, "CAT") {
  def name = column[String]("NAME", O.PrimaryKey)
  def color = column[String]("COLOR")

  def * = (name, color) <>(Cat.tupled, Cat.unapply)
}