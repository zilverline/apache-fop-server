FROM alpine:3.6

RUN apk --update add openjdk7-jre tar

RUN mkdir /fop
WORKDIR /fop

COPY target/apache-fop-server-1.7-bin.tar.gz /fop/

RUN tar xfvz /fop/apache-fop-server-1.7-bin.tar.gz

EXPOSE 9999
CMD ["java", "-cp", "apache-fop-server.jar:lib/*", "org.zilverline.fop.FopServer"]
