package controllers

import java.io.File
import java.net.URL
import play.api._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.libs.openid.OpenID
import play.api.libs.concurrent.Redeemed
import play.api.libs.concurrent.Thrown
import play.api._
import play.api.data._
import play.api.data.Forms._
import java.util.HashMap

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
	def login = Action {
	  Ok(views.html.login())
	  // Ok(views.html.index("dim"))
	}
	
	def auth = Action { implicit request =>
    val loginForm = Form(
      single(
	      "openid" -> nonEmptyText
      )
    )

	  loginForm.bindFromRequest.fold(
	    error => {
	      Logger.info("bad request " + error.toString)
	      BadRequest(error.toString)
	    },
	    {
      case (openid) =>
        Logger.info( "Log in using" + openid )
        Console.println( routes.Application.verify.absoluteURL() )
        val attributes = List[(String, String)] (
          ("Email", "http://schema.openid.net/contact/email"),
          ("FirstName", "http://schema.openid.net/namePerson/first"),
          ("LastName", "http://schema.openid.net/namePerson/last")
        )

        val returnToUrl = routes.Application.verify.absoluteURL()
        val redirectUrl = OpenID.redirectURL( openid, returnToUrl, attributes);
        AsyncResult(OpenID.redirectURL(openid, returnToUrl)
	          .extend( _.value match {
	              case Redeemed(url) => Redirect(url)
	              case Thrown(t) => Redirect(routes.Application.login)
	          }))
	    }
	  )
	}
	
	def verify = Action { implicit request =>
	  AsyncResult(
	    OpenID.verifiedId.extend( _.value match {
	      case Redeemed(info) => Ok(info.id + "\n" + info.attributes)
	      case Thrown(t) => {
	        // Here you should look at the error, and give feedback to the user
	        Redirect( routes.Application.login )
	      }
	    })
	  )
	}

}
