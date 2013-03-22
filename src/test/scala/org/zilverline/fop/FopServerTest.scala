package org.zilverline.fop

import dispatch._
import java.io.File
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.matchers.ShouldMatchers

/*
 * Test the FopServer using the http://dispatch.databinder.net/ HTTP
 * client library.
 */
@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class FopServerTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll {
  val server = unfiltered.jetty.Http.anylocal.plan(FopServer.plan)

  def BaseUrl = host("localhost", server.port)
  def IsAliveUrl = BaseUrl / "is-alive"
  def PdfUrl = BaseUrl / "pdf"

  lazy val xsl = FileUtils.readFileToString(new File("src/test/resources/invoice.xsl"))
  lazy val xml = FileUtils.readFileToString(new File("src/test/resources/invoice.xml"))

  override def beforeAll = server.start
  override def afterAll = server.stop

  test("should start a server") {
    val response = Http(IsAliveUrl OK as.String).apply()
    response should be("Ok")
  }

  test("should create a pdf") {
    val request = PdfUrl << Map("xsl" -> xsl, "xml" -> xml)

    val pdf = Http(request OK as.Bytes).apply()

    pdf.length should be > (0)
    FileUtils.writeByteArrayToFile(new File("target/generated-test.pdf"), pdf)
  }

  test("should respond with internal server error on exception") {
    val request = PdfUrl << Map("xsl" -> "bad", "xml" -> "worse")

    val response = Http(request).apply()

    response.getStatusCode() should be(500)
    response.getResponseBody() should be("See apache-fop-server/logs for more detailed error message.")
  }
}
