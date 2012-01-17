package org.zilverline.fop

import org.eclipse.jetty.server.handler.AbstractHandler
import java.lang.String
import org.eclipse.jetty.server.Request
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.http.HttpServletResponse.SC_OK



class AliveHandler extends AbstractHandler {
  def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
    response.setStatus(SC_OK)
    response.getOutputStream.write("Ok".getBytes("UTF-8"))
    response.getOutputStream.flush()
  }
}