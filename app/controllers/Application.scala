package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def page( file: String ) = Action {
  	// controllers.Assets.at(path="/public", file)
    Ok(views.html.index( file ))
  }
  
}
