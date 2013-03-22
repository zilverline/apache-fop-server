package org.zilverline.fop

import grizzled.slf4j.Logging
import unfiltered.request._
import unfiltered.response._

/**
 * Standalone HTTP server using the http://unfiltered.databinder.net/
 * HTTP server toolkit.
 */
object FopServer extends Logging {
  val DefaultPort = 9999

  object Xsl extends Params.Extract("xsl", Params.first)
  object Xml extends Params.Extract("xml", Params.first)

  val plan = unfiltered.filter.Planify {
    case GET(Path("/is-alive"))                           => Ok ~> ResponseString("Ok")
    case POST(Path("/pdf")) & Params(Xsl(xsl) & Xml(xml)) => generatePdf(xsl, xml)
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "4000000")

    unfiltered.jetty.Http.local(DefaultPort).filter(plan).run { server =>
      sys.addShutdownHook(server.stop)
    }
  }

  private def generatePdf(xsl: String, xml: String) = try {
    val pdf = new PdfGenerator().createOutput(xsl, xml)
    Ok ~> ContentType("application/pdf") ~> ResponseBytes(pdf)
  } catch {
    case e: Exception =>
      error(f"Error creating PDF for XML $xml with XSL $xsl: $e", e)
      InternalServerError ~> ResponseString("See apache-fop-server/logs for more detailed error message.")
  }
}
