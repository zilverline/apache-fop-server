package org.zilverline.fop

import grizzled.slf4j.Logging
import java.io.{File, OutputStream, StringReader}
import javax.xml.transform.{Transformer, TransformerFactory, URIResolver}
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.apache.fop.apps.{Fop, FopFactory}
import org.apache.xmlgraphics.util.MimeConstants
import uk.co.opsb.butler.ButlerIO

class PdfGenerator(configFileName: String = "fop/fop-config.xml") extends Logging {

  def createOutput(xsl: String, xml: String): Array[Byte] = {
    debug(f"Generating PDF with XSL $xsl and XML $xml")

    val bos = new java.io.ByteArrayOutputStream()
    try {
      info("Generating PDF")
      transform(xsl, xml, createFop(bos))
      info("PDF generated!")
      bos.toByteArray()
    } finally {
      IOUtils.closeQuietly(bos)
    }
  }

  private def createFop(out: OutputStream): Fop = {
    val fopFactory = createFopFactory()
    val foUserAgent = fopFactory.newFOUserAgent()
    fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out)
  }

  private def createFopFactory(): FopFactory = {
    val fopFactory = FopFactory.newInstance()

    fopFactory.setURIResolver(new URIResolver {
      def resolve(href: String, base: String) = {
        new StreamSource(ButlerIO.inputStreamFrom(href))
      }
    })

    fopFactory.setUserConfig(new File(configFileName))
    fopFactory
  }

  private def transform(xsl: String, xml: String, fop: Fop): Unit = {
    val src = new StreamSource(IOUtils.toInputStream(xml, "UTF-8"))
    val res = new SAXResult(fop.getDefaultHandler())
    createTransformer(xsl).transform(src, res)
  }

  private def createTransformer(xsl: String): Transformer = {
    // Setup XSLT
    val factory = TransformerFactory.newInstance()
    val transformer = factory.newTransformer(new StreamSource(new StringReader(xsl)))
    transformer.setParameter("versionParam", "2.0")
    transformer
  }
}
