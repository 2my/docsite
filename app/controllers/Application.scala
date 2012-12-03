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
  
	/** Default entry. */
  def index = Action { implicit request =>
    Ok(views.html.index( "Your new application is ready." ))
  }

	/** Show login form. */
  def login = Action { implicit request =>
	  Ok(views.html.login())
	}
	
	/** Gets user login request, dispatches to OpenId provider. */
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
	    }, {
      case (openid) =>
        Logger.info( "Log in using " + openid + " with redirect: " + routes.Application.verify.absoluteURL() )
        val attributes = Seq("email" -> "http://schema.openid.net/contact/email")
        // "firstName" -> "http://schema.openid.net/namePerson/first", -> "lastName", "http://schema.openid.net/namePerson/last")

        val returnToUrl = routes.Application.verify.absoluteURL()
        AsyncResult(
            OpenID.redirectURL(openid, returnToUrl, attributes)
	          .extend( _.value match {
	              case Redeemed(url) => Redirect(url)
	              case Thrown(t) => Redirect(routes.Application.login)
	          	}
	          )
          )
	    }
	  )
	}
	
	/** OpenId callback url. */
	def verify = Action { implicit request =>
	  AsyncResult(
	    OpenID.verifiedId.extend( _.value match {
	      case Redeemed(info) => {
	        Logger.info( info.id + "\n" + info.attributes.toString() )
	        val email	= info.attributes.get("email").get
	        Ok( views.html.index( "Welcome" + email ) ).withSession("email" -> email)
	      }
	      case Thrown(t) => {
	        Logger.error( "verify got error: ", t )
	        Redirect( routes.Application.login ).flashing( "error" -> t.getMessage() )
	      }
	    })
	  )
	}

	/** Logout and clean the session. */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing( "success" -> "You've been logged out" )
  }

}

/** Provide security features */
trait Secured {
  
  /** Action for authenticated users. */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = 
    Security.Authenticated(username, onUnauthorized) { user =>
	    Action(request => f(user)(request))
	  }

  /** Check if the connected user is a member. */
  def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) =
    IsAuthenticated { user => request =>
	    Results.Forbidden
	    /*
	    if(Project.isMember(project, user)) {
	      f(user)(request)
	    } else {
	      Results.Forbidden
	    }*/
	  }
  
  /** Retrieve the connected user email. */
  private def username(request: RequestHeader) = request.session.get("email")

  /** Redirect to login if the user in not authorized. */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

}
