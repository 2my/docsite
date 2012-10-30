package controllers

import java.io.File
import java.net.URL

import play.api._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
