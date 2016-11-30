package org.zilverline.fop

import grizzled.slf4j.Logging
import java.io.{File, OutputStream, StringReader}
import javax.xml.transform.{Transformer, TransformerFactory, URIResolver}
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.apache.fop.apps.{FopConfParser, Fop, FopFactory}
import org.apache.xmlgraphics.util.MimeConstants
import uk.co.opsb.butler.ButlerIO

case class PdfDocument(xsl: String, xml: String, configFileName: String = "fop/fop-config.xml") extends Logging {
  def render: Array[Byte] = {
    debug(f"Generating PDF with XSL $xsl and XML $xml")

    val bos = new java.io.ByteArrayOutputStream()
    try {
      val start = System.currentTimeMillis();
      transform(createFop(bos))
      val time = System.currentTimeMillis() - start;
      info(f"PDF generated in $time ms")
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
    val parser = new FopConfParser(new File(configFileName))
    val builder = parser.getFopFactoryBuilder
    builder.build();
  }

  private def transform(fop: Fop): Unit = {
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
