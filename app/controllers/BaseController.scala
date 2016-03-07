package controllers

import dao.BaseDAO
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{Format, Json, Writes}
import play.api.mvc.{Action, Controller, Result, Results}

import scala.concurrent.Future

trait BaseController[ID, E, V <: BaseDAO[ID, E]] extends Controller with I18nSupport with JsonSupport {

  def dao: V
  def messages: MessagesApi
  def messagesApi = messages
  def form: Form[E]

  implicit def formatE: Format[E]

  def byId(id: ID) = Action.async { implicit r =>
    dao.byIdOpt(id).map(e => e.fold(notFound(e))(ok(_)))
  }

  def list = Action.async { implicit r =>
    dao.seq().map(ok(_))
  }

  def add = handleForm(m => dao.insert(m).map(e => ok(e.toString)))
  def update = handleForm(m => dao.update(m).map(ok(_)))

  def handleForm(withModel: (E) => Future[Result]) = Action.async { implicit r =>
    form.bindFromRequest.fold(
      e => badRequest(e.errorsAsJson).future,
      m => withModel(m))
  }

  def delete(id: ID) = Action.async { implicit r =>
    dao.delete(id) map (e => if (e > 0) notContent() else notFound(id.toString))
  }
}

trait JsonSupport {
  this: Results =>

  def ok[R](e: R)(implicit w: Writes[R]) = Ok(Json.toJson(e))
  def notFound[R](e: R)(implicit w: Writes[R]) = NotFound(Json.toJson(e))
  def badRequest[R](e: R)(implicit w: Writes[R]) = BadRequest(Json.toJson(e))
  def notContent() = NoContent

  implicit class FuturePimps[R](e: R) {
    def future: Future[R] = Future.successful(e)
  }
}