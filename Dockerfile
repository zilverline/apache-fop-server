FROM alpine:3.6

RUN apk --update add openjdk8-jre tar

RUN mkdir /fop
WORKDIR /fop

COPY target/apache-fop-server-2.1-bin.tar.gz /fop/

RUN tar xfvz /fop/apache-fop-server-2.1-bin.tar.gz

EXPOSE 9999
CMD ["java", "-cp", "apache-fop-server.jar:lib/*", "org.zilverline.fop.FopServer"]
