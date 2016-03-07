package controllers

import play.api.libs.json.{Json, Writes}
import play.api.mvc.Results

import scala.concurrent.Future

trait JsonSupport {
  this: Results =>

  val success = "response"
  val failure = "error"

  def ok[R](e: R)(implicit w: Writes[R]) = Ok(Json.obj(success -> Json.toJson(e)))
  def notFound[R](e: R)(implicit w: Writes[R]) = NotFound(Json.obj(failure -> Json.toJson(e)))
  def badRequest[R](e: R)(implicit w: Writes[R]) = BadRequest(Json.obj(failure -> Json.toJson(e)))
  def notContent() = NoContent

  implicit class FuturePimps[R](e: R) {
    def future: Future[R] = Future.successful(e)
  }
}
