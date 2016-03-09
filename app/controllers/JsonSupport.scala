package controllers

import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.Results

import scala.concurrent.Future

trait JsonSupport {
  this: Results =>

  val success = "response"
  val failure = "error"

  def ok[R](e: R)(implicit w: Format[R]) = Ok(Json.obj(success -> e))
  def notFound[R](e: R)(implicit w: Format[R]) = NotFound(Json.obj(failure -> e))
  def badRequest[R](e: R)(implicit w: Format[R]) = BadRequest(Json.obj(failure -> e))
  def notContent() = NoContent

  implicit class FuturePimps[R](e: R) {
    def future: Future[R] = Future.successful(e)
  }
}
