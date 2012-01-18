package org.zilverline.fop

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.http.HttpServletResponse._
import org.apache.fop.apps.{FopFactory, Fop}
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import java.lang.String
import uk.co.opsb.butler.ButlerIO
import javax.xml.transform.{TransformerFactory, URIResolver, Transformer}
import org.apache.xmlgraphics.util.MimeConstants
import javax.xml.transform.sax.SAXResult
import java.io.{StringReader, File, OutputStream}
import org.apache.log4j.Logger


class PdfHandler(val configFileName: String = "fop/fop-config.xml") extends AbstractHandler {

  val Log = Logger.getLogger(classOf[PdfHandler])

  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    val xml = request.getParameter("xml")
    val xsl = request.getParameter("xsl")
    if (Log.isDebugEnabled) {
      Log.debug("xml: " + xml)
      Log.debug("xsl: " + xsl)
    }
    try {
      Log.info("Generating pdf.")
      response.setStatus(SC_OK)
      response.setContentType("application/pdf")
      response.getOutputStream.write(createOutput(xsl, xml))
      response.getOutputStream.flush()
      Log.info("Pdf generated!")
    } catch {
      case e =>
        Log.error("Exception creating pdf for xml: " + xml + "with xsl: " + xsl, e)
        response.setStatus(SC_INTERNAL_SERVER_ERROR)
        response.getOutputStream.write("See apache-fop-server/logs for more detailed error message.".getBytes("UTF-8"))
        response.getOutputStream.flush()
    }
  }

  private def createOutput(xsl: String, xml: String): Array[Byte] = {
    val bos = new java.io.ByteArrayOutputStream();
    val out = new java.io.BufferedOutputStream(bos);
    try {
      val fop = createFop(out, MimeConstants.MIME_PDF);
      val transformer = createTransformer(xsl);
      transform(xml, fop, transformer);
      return bos.toByteArray();
    } finally {
      IOUtils.closeQuietly(out);
      IOUtils.closeQuietly(bos);
    }
  }

  private def createFop(out: OutputStream, mimeType: String): Fop = {
    val fopFactory = createFopFactory();
    val foUserAgent = fopFactory.newFOUserAgent();
    return fopFactory.newFop(mimeType, foUserAgent, out);
  }

  private def createFopFactory(): FopFactory = {
    val fopFactory = FopFactory.newInstance();

    fopFactory.setURIResolver(new URIResolver {
      def resolve(href: String, base: String) = {
        new StreamSource(ButlerIO.inputStreamFrom(href))
      }
    })

    fopFactory.setUserConfig(new File(configFileName));
    return fopFactory;
  }

  private def transform(xml: String, fop: Fop, transformer: Transformer): Unit = {
    val src = new StreamSource(IOUtils.toInputStream(xml, "UTF-8"));
    val res = new SAXResult(fop.getDefaultHandler());
    transformer.transform(src, res);
  }


  private def createTransformer(xsl: String): Transformer = {
    // Setup XSLT
    val factory = TransformerFactory.newInstance();
    val transformer = factory.newTransformer(new StreamSource(new StringReader(xsl)));
    transformer.setParameter("versionParam", "2.0");
    return transformer;
  }


}