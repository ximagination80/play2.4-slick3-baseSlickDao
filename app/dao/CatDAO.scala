package dao

import javax.inject.{Inject, Singleton}

import models.Cat
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

@Singleton()
class CatDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DefaultDAO[Long, Cat, CatDef] {

  val q = TableQuery[CatDef]

  // TODO make this better
  def getId(e: Cat) = e.id.get
  def id() = q.map(_.id)
  def filterIds(e: Seq[Long]) = q.filter(_.id inSet e)
}

class CatDef(tag: Tag) extends Table[Cat](tag, "CAT") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def color = column[String]("COLOR")

  def * = (id.?, color) <>(Cat.tupled, Cat.unapply)
}