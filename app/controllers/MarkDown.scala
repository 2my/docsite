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

object MarkDown extends Controller with HeaderNames with ContentTypes {

  def html( resourceName: String ) = Action {
    val htmlRes	= Play.resource( "/public/" + resourceName + ".html" )	.map( url => enumeratorOverStream( url ) )
    val mdRes		= Play.resource( "/public/" + resourceName + ".md" )		.map( url => new File(url.getFile) )
    (htmlRes, mdRes) match {
      case (None,None) => NotFound
      case (None,Some( file )) => Ok( views.html.mdview( file.getName ) )
      case (Some( enumerator ),_) => Ok.stream( enumerator() ).withHeaders( CONTENT_TYPE -> HTML )
    }
  }


  def md( resourceName: String ) = Action {
    val resource	= Play.resource( "/public/" + resourceName + ".md" )	.map( url => enumeratorOverStream( url ) )
    resource match {
      case None => NotFound
      case Some( enumerator ) => Ok.stream( enumerator() ).withHeaders( CONTENT_TYPE -> TEXT )	// md not in MimeTypes
    }
  }

  def page( resourceName: String ) = Action {
    val resource	= Play.resource( "/public/" + resourceName )	.map( url => enumeratorOverStream( url ) )
    resource match {
      case None => NotFound
      case Some( enumerator ) => Ok.stream( enumerator() ).withHeaders( contentType( resourceName ) )
    }
  }

  // return a function
  private def enumeratorOverStream( url : URL ) = { () => { Enumerator.fromStream( url.openStream() ) } }

  private def contentType( fileName : String ) = CONTENT_TYPE -> MimeTypes.forFileName( fileName ).getOrElse( BINARY )

}
