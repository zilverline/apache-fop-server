# Apache FOP Server

Simple embedded HTTP server that provides creating PDF's using [apache fop](http://xmlgraphics.apache.org/fop/).

The server uses the [Unfiltered](http://unfiltered.databinder.net/) Scala HTTP server toolkit and Jetty.

Testing is done using the [Dispatch](http://dispatch.databinder.net/) HTTP client library.

### How to build

    mvn assembly:assembly

### How to use
Download one of the tar.gz from the downloads page
    
    tar xvzf apache-fop-server-[version].tar.gz
    java -cp apache-fop-server.jar:lib/* org.zilverline.fop.FopServer

This will start the server on port 9999.

To generate a pdf one should POST to `http://localhost:9999/pdf` with 2 parameters: `xml` containing the XML representation of the pdf and `xsl` as the stylesheet to be used.

Example on how to use in Ruby using httpclient

    require 'httpclient'

    module Adapters::ApacheFopAdapter

      FOP_URL = "http://localhost"
      FOP_PORT = 9999

      def get_pdf(xml, xsl)
        body = {xsl: IO.read(xsl), xml: xml}
        client = HTTPClient.new
        result = client.post "#{FOP_URL}:#{FOP_PORT}/pdf", body
        result.body
      end

      module_function :get_pdf
    end

An example upstart script for installing this on the server could be

    # apache-fop-server - simple webserver to enable generation of pdfs
    #
    # Simple embedded Jetty server to enable generation of pdfs using apache-fop

    description     "webserver to enable generation of pdfs using apache-fop"

    start on runlevel [2345]
    stop on runlevel [!2345]

    expect fork

    script
      chdir /opt/apps/apache-fop-server
      exec java -cp apache-fop-server.jar:lib/* org.zilverline.fop.FopServer
    end script

Fork and patch to contribute!
