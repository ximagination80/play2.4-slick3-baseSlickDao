package controllers

import javax.inject.Inject

import dao.CatDAO
import models.Cat
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationController @Inject()(catDAO: CatDAO, messages: MessagesApi) extends Controller with I18nSupport {

  def messagesApi = messages
  implicit val formatCat = Json.format[Cat]

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "color" -> nonEmptyText
    )(Cat.apply)(Cat.unapply)
  )

  def byId(id: String) = Action.async { implicit request =>
    catDAO.byId(id) map { e=>
      Ok(Json.toJson(e))
    }
  }

  def cats = Action.async { implicit request =>
    catDAO.seq() map { e =>
      Ok(Json.toJson(e))
    }
  }

  def add = Action.async { implicit request =>
    form.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(Json.toJson(errorForm.errorsAsJson))),
      data => catDAO.insert(data).map { e =>
        Redirect(routes.ApplicationController.cats())
      }
    )
  }

  def delete(id: String) = Action.async { implicit request =>
    catDAO.delete(id) map { res =>
      Redirect(routes.ApplicationController.cats())
    }
  }
}

