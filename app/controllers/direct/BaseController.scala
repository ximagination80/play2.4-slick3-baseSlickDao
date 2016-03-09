package controllers.direct

import controllers.JsonSupport
import dao.BaseDAO
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.json.Format
import play.api.mvc.Action._
import play.api.mvc.{Controller, Result}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

trait BaseController[ID, E, V <: BaseDAO[ID, E]] extends Controller with I18nSupport with JsonSupport {

  def dao: V
  def form: Form[E]

  implicit def formatE: Format[E]

  def byId(id: ID) = async(dao.byIdOpt(id).map(e => e.fold(notFound(id.toString))(ok(_))))
  def list = async(dao.seq().map(ok(_)))
  def add = handle(m => dao.insert(m).map(e => ok(e.toString)))
  def update = handle(m => dao.update(m).map(ok(_)))
  def delete(id: ID) = async(dao.delete(id) map (e => if (e > 0) notContent() else notFound(id.toString)))

  def handle(withModel: (E) => Future[Result]) =
    async(implicit r => form.bindFromRequest.fold(e => badRequest(e.errorsAsJson).future, withModel(_)))
}
