package controllers

import dao.BaseDAO
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{Format, JsArray}
import play.api.mvc.Action.async
import play.api.mvc.{Controller, Request, Result}

import scala.concurrent.Future

trait BatchController extends Controller with I18nSupport with JsonSupport {

  def map: Map[String, DaoMetadata[_, _ <: BaseDAO[Long, _]]]

  def byId(name: String, id: Long) = withDao(name, { (md, req) =>
    md.dao.byIdOpt(id).map { e =>
      e.fold(notFound(id.toString)) { x =>
        ok(x.asInstanceOf[md.MODEL])(md.format.asInstanceOf[Format[md.MODEL]])
      }
    }
  })

  def list(name: String) = withDao(name, { (md, req) =>
    md.dao.asInstanceOf[BaseDAO[Long, md.MODEL]].seq().map { e =>
      val format = md.format.asInstanceOf[Format[md.MODEL]]
      ok(JsArray(e.map(format.writes)))
    }
  })

  def delete(name: String, id: Long) = withDao(name, { (md, req) =>
    md.dao.delete(id) map (e => if (e > 0) notContent() else notFound(id.toString))
  })

  def add(name: String) = withDao(name, { (md, req) =>
    handle[md.MODEL](md.form.asInstanceOf[Form[md.MODEL]], req, { m =>
      md.dao.asInstanceOf[BaseDAO[Long, md.MODEL]].insert(m).map(e => ok(e.toString))
    })
  })

  def update(name: String) = withDao(name, { (md, req) =>
    handle[md.MODEL](md.form.asInstanceOf[Form[md.MODEL]], req, { m =>
      md.dao.asInstanceOf[BaseDAO[Long, md.MODEL]].update(m).map(ok(_))
    })
  })

  def handle[E](form: Form[E], req: Request[_], withModel: (E) => Future[Result]) =
    form.bindFromRequest()(req).fold(
      e => badRequest(e.errorsAsJson).future,
      m => withModel(m)
    )

  def withDao(name: String, withDao: (DaoMetadata[_, _ <: BaseDAO[Long, _]], Request[_]) => Future[Result]) = async { r =>
    map.get(name).fold(badRequest(s"Dao not exists '$name'").future) { e =>
      withDao(e, r)
    }
  }

  case class DaoMetadata[M, D <: BaseDAO[Long, M]](dao: D, form: Form[M], format: Format[M]) {
    final type MODEL = M
  }
}