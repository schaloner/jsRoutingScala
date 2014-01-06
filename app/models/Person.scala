package models

import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json._
import anorm.~

case class Person(id: Pk[Long] = NotAssigned, name: String)

object Person {

  val simple = {
    get[Pk[Long]]("person.id") ~
      get[String]("person.name") map {
      case id~name => Person(id, name)
    }
  }

  def insert(person: Person) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into person values (
            (select next value for person_seq),
            {name}
          )
        """
      ).on(
        'name -> person.name
      ).executeInsert()
    } match {
      case Some(long) => long
      case None       => -1
    }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from person where id = {id}").on('id -> id).executeUpdate()
    }
  }

  def getAll: Seq[Person] = {
    DB.withConnection { implicit connection =>
      SQL("select * from person").as(Person.simple *)
    }
  }

  implicit object PersonReads extends Reads[Person] {

    def reads(json: JsValue): JsResult[Person] =
      JsSuccess[Person](Person(NotAssigned, (json \"name").as[String]), JsPath())
  }

  implicit object PersonWrites extends Writes[Person] {
    def writes(person: Person) = Json.obj(
      "id" -> Json.toJson(person.id.get),
      "name" -> Json.toJson(person.name)
    )
  }
}