package org.zilverline.fop

import org.eclipse.jetty.server.{Handler, Server}
import org.eclipse.jetty.server.handler.{ContextHandlerCollection, ContextHandler}


class FopServer {
  val server = new Server(FopServer.DefaultPort)

  def start() {
    val contextHandlers = new ContextHandlerCollection()
    val aliveContext = wrapContext(new AliveHandler(), "/is-alive")
    val pdfContext = wrapContext(new PdfHandler(), "/pdf")
    contextHandlers.setHandlers(Array[Handler](aliveContext, pdfContext))
    server setHandler contextHandlers
    stopOnJvmShutdown()
    server.start()

  }

  def stopServer() {
    server.stop()
  }

  private def stopOnJvmShutdown() {
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      def run() {
        stopServer()
      }
    }))
  }

  private def wrapContext(handler: Handler, contextPath: String) = {

    val contextHandler = new ContextHandler(contextPath)
    contextHandler.setHandler(handler)
    contextHandler.setAllowNullPathInfo(true)
    contextHandler
  }
}

object FopServer {
  val DefaultPort = 9999
  def main(args: Array[String]): Unit = {
    val server = new FopServer()
    server.start();
  }
}