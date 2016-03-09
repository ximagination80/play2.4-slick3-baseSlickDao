package controllers.direct

import javax.inject.Inject

import dao.CatDAO
import models.Cat
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.libs.json.Json

class CatController @Inject()(val dao: CatDAO, val messagesApi: MessagesApi)
  extends BaseController[Long, Cat, CatDAO] {

  val formatE = Json.format[Cat]
  val form = Form(
    mapping(
      "id" -> optional(longNumber),
      "color" -> nonEmptyText
    )(Cat.apply)(Cat.unapply)
  )
}

