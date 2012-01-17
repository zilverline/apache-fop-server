package org.zilverline.fop

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import org.scalatest.{BeforeAndAfter, FunSuite}


@RunWith(classOf[JUnitRunner])
class FopServerTest extends FunSuite with ShouldMatchers with BeforeAndAfter {
  val server: FopServer = new FopServer()

  after {
    server.stopServer()
  }
  
  test("should start a server") {
    server.start();
    val client = new DefaultHttpClient();
    val get = new HttpGet("http://localhost:%d/" format FopServer.DefaultPort);
    val response = client.execute(get);
    EntityUtils.toString(response.getEntity()) should be("Ok")
  }
  
}