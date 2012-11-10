package controllers

import java.io.File
import java.net.URL

import play.api._
import play.api.Play.current
import play.api.libs.iteratee.Enumerator
import play.api.mvc._

object MarkDown extends Controller {

  def html( resourceName: String ) = Action {
    val htmlRes	= Play.resource( "/public/" + resourceName + ".html" )	// .map( url => new File(url.getFile) )
    val mdRes		= Play.resource( "/public/" + resourceName + ".md" )		// .map( url => new File(url.getFile) )
    println( htmlRes )
    println( mdRes )
    (htmlRes, mdRes) match {
      case (None,None) => NotFound
      case (None,Some( url )) => Ok( views.html.mdview( new File( url.getFile ).getName ) )
      case (Some( url ),_) => Ok.stream( Enumerator.fromStream( url.openStream() ) ).withHeaders( "Content-Type"->"text/html" )
    }
  }


  def md( resourceName: String ) = Action {
    val resource	= Play.resource( "/public/" + resourceName + ".md" )	// .map( url => new File(url.getFile) )
    resource match {
      case None => NotFound
      case Some( url ) => Ok.stream( Enumerator.fromStream( url.openStream() ) ).withHeaders( "Content-Type"->"text/plain" )
    }
  }

  def page( resourceName: String ) = Action {
    val resource	=
      Play.resource( "/public/" + resourceName )
      .orElse( Play.resource( "/public/" + resourceName.replaceAll( "html\\z", "md") ) )
      .map( url => new File(url.getFile) )
    resource match {
      case None => NotFound
      case Some(file) if isMarkDownResult( file ) => markDownResult( file )
      case Some(file) => Ok.sendFile( file, true )
    }
  }

  private def isMarkDownResult( file: File ) = file.getName().endsWith( ".md" );

  /** Needed because markdown files not mime mapped */
  private def markDownResult( mdFile: File ) = SimpleResult(
	        header = ResponseHeader(OK, Map(
	          CONTENT_LENGTH -> mdFile.length.toString,
	          CONTENT_TYPE -> "text/plain"
	        )),
	        Enumerator.fromFile( mdFile )
	      )
	    ;

}
