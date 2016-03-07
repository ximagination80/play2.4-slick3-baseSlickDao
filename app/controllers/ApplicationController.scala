package controllers

import javax.inject.Inject

import dao.CatDAO
import models.Cat
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json

class ApplicationController @Inject()(val dao: CatDAO, val messages: MessagesApi)
  extends BaseController[Long, Cat, CatDAO] {

  val formatE = Json.format[Cat]
  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "color" -> nonEmptyText
    )(Cat.apply)(Cat.unapply)
  )
}

