package controllers

import javax.inject.Inject

import dao.{CatDAO, DogDAO}
import models.{Cat, Dog}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json

class AnyController @Inject()(cat: CatDAO,
                              dog: DogDAO,
                              val messagesApi: MessagesApi) extends BatchController {
  val map = Map(
    "cat" -> DaoMetadata(cat, Form(
      mapping(
        "id" -> optional(longNumber),
        "color" -> nonEmptyText
      )(Cat.apply)(Cat.unapply)
    ), Json.format[Cat]),

    "dog" -> DaoMetadata(dog, Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText,
        "age" -> number(min = 0),
        "hate_cat_id" -> number(min = 1)
      )(Dog.apply)(Dog.unapply)
    ), Json.format[Dog])
  )
}