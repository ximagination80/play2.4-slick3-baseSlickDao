package dao

import javax.inject.{Inject, Singleton}

import models.Dog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.lifted.TableQuery

@Singleton()
class DogDAO @Inject()(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with DefaultDAO[Long, Dog, DogDef] {

  val q = TableQuery[DogDef]

  // TODO make this better
  def getId(e: Dog) = e.id
  def id() = q.map(_.id)
  def filterIds(e: Seq[Long]) = q.filter(_.id inSet e)

}

class DogDef(tag: Tag) extends Table[Dog](tag, "DOG") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def age = column[Int]("AGE")
  def hateCatId = column[Int]("HATE_CAT_ID")

  def * = (id.?, name, age, hateCatId) <>(Dog.tupled, Dog.unapply)
}