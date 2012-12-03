package controllers

import java.io.File
import java.net.URL
import play.api._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.libs.ws.ResponseHeaders
import play.api.http.HeaderNames
import play.api.http.ContentTypes
import play.api.libs.MimeTypes

object PublicMarkDown extends MarkDown( "/public/content-external/" ) {
  def html( resourceName: String ) =	super.htmli( resourceName )
  def md( resourceName: String ) =		super.mdi( resourceName )
  def page( resourceName: String ) =	super.pagei( resourceName )
}

object PrivateMarkDown extends MarkDown( "/public/content-internal/" ) with Secured {
  def html( resourceName: String )	=
    IsAuthenticated { username => implicit request =>
      super.htmli( resourceName ).apply( request )
    }
  def md( resourceName: String )		=
    IsAuthenticated { username => implicit request =>
      super.mdi( resourceName ).apply( request )
    }
  def page( resourceName: String )	=
    IsAuthenticated { username => implicit request =>
      super.pagei( resourceName ).apply( request )
    }
}

class MarkDown( val contentRoot: String ) extends Controller with HeaderNames with ContentTypes {

  /** Html served normally if found, view with embedded md served if n md-file found. */
  private[controllers] def htmli( resourceName: String ) = Action {
    val htmlRes	= resource( resourceName, ".html" )	.map( url => enumeratorOverStream( url ) )
    val mdRes		= resource( resourceName, ".md" )		.map( url => new File(url.getFile) )
    (htmlRes, mdRes) match {
      case (None,None) => NotFound
      case (None,Some( file )) => Ok( views.html.mdview( file.getName ) )
      case (Some( enumerator ),_) => Ok.stream( enumerator() ).withHeaders( CONTENT_TYPE -> HTML )
    }
  }

  /** Streams md-file as text/plain */
  private[controllers] def mdi( resourceName: String ) = Action {
    Console.println( contentRoot + resourceName + ".md" )
    val mdRes	= resource( resourceName, ".md" )	.map( url => enumeratorOverStream( url ) )
    mdRes match {
      case None => NotFound
      case Some( enumerator ) => Ok.stream( enumerator() ).withHeaders( CONTENT_TYPE -> TEXT )	// md not in MimeTypes
    }
  }

  /** Streams other files as appropriate. */
  private[controllers] def pagei( resourceName: String ) = Action {
    val file	= resource( resourceName )	.map( url => enumeratorOverStream( url ) )
    file match {
      case None => NotFound
      case Some( enumerator ) => Ok.stream( enumerator() ).withHeaders( contentType( resourceName ) )
    }
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  private def resource( name: String ) = Play.resource( contentRoot + name )
  private def resource( name: String, extension: String ) = Play.resource( contentRoot + name + extension )

  // return a function
  private def enumeratorOverStream( url : URL ) = { () => { Enumerator.fromStream( url.openStream() ) } }

  private def contentType( fileName : String ) = CONTENT_TYPE -> MimeTypes.forFileName( fileName ).getOrElse( BINARY )

}
