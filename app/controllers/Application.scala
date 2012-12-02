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
import play.libs.Json

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
        Logger.info( "Log in using " + openid + " with redirect: " + routes.Application.verify.absoluteURL() )
        val attributes = Seq("email" -> "http://schema.openid.net/contact/email")
          // "FirstName", "http://schema.openid.net/namePerson/first"),
          // "LastName", "http://schema.openid.net/namePerson/last")

        val returnToUrl = routes.Application.verify.absoluteURL()
        AsyncResult(OpenID.redirectURL(openid, returnToUrl, attributes)
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
	      case Redeemed(info) => {
	        // Redirect(routes.Projects.index).withSession("email" -> user._1)
	        Logger.info( info.attributes.toString() )
	        Ok(info.id + "\n" + info.attributes)	//.withSession( "email" -> info.attributes.get("email") )
	      }
	      case Thrown(t) => {
	        // Here you should look at the error, and give feedback to the user
	        Redirect( routes.Application.login )
	      }
	    })
	  )
	}

}

/**
 * Provide security features
 */
trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /**
   * Check if the connected user is a member of this project.
   */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    Results.Forbidden
    /*
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }*/
  }

}
