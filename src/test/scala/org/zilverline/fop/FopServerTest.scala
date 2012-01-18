package org.zilverline.fop

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.apache.http.impl.client.DefaultHttpClient
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.apache.http.client.methods.{HttpPost, HttpGet}
import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.xmlgraphics.util.MimeConstants
import org.apache.http.util.EntityUtils
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import scala.collection.JavaConverters._


@RunWith(classOf[JUnitRunner])
class FopServerTest extends FunSuite with ShouldMatchers with BeforeAndAfter {
  val server: FopServer = new FopServer()
  val client = new DefaultHttpClient();

  after {
    server.stopServer()
  }

  test("should start a server") {
    server.start();
    val get = new HttpGet("http://localhost:%d/is-alive" format FopServer.DefaultPort);
    val response = client.execute(get);
    EntityUtils.toString(response.getEntity()) should be("Ok")
  }

  test("should create a pdf") {
    server.start()
    val post = new HttpPost("http://localhost:%d/pdf" format FopServer.DefaultPort);
    val xsl = FileUtils.readFileToString(new File("src/test/resources/invoice.xsl"))
    val xml = FileUtils.readFileToString(new File("src/test/resources/invoice.xml"))

    val params: List[NameValuePair] = List[NameValuePair](new BasicNameValuePair("xsl", xsl), new BasicNameValuePair("xml", xml))
    post.setEntity(new UrlEncodedFormEntity(params.asJava, "UTF-8"))
    val response = client.execute(post);
    response.getStatusLine.getStatusCode should be(200)
    val result = response.getEntity
    EntityUtils.getContentMimeType(result) should be(MimeConstants.MIME_PDF)
    EntityUtils.toByteArray(result).length should be > (0)
  }
}