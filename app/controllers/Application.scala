package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import models.Person

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getAll = Action {
    Ok(Json.toJson(Person.getAll))
  }

  def delete(id: Long) = Action {
    Person.delete(id)
    Ok("Deleted " + id)
  }

  def create = Action { implicit request =>

    request.body.asJson match {
      case None => BadRequest
      case Some(json: JsValue) => {
        val person: Person = json.as[Person]
        val id = Person.insert(person)
        Ok(Json.obj(
          "id" -> id,
          "name" -> person.name
        ))
      }
    }
  }

  def jsRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("appRoutes")(
        controllers.routes.javascript.Application.create,
        controllers.routes.javascript.Application.delete,
          controllers.routes.javascript.Application.getAll
      )
    ).as("text/javascript")
  }
}