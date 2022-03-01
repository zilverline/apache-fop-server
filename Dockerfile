FROM alpine:3.6

RUN apk --update add openjdk8-jre tar

RUN mkdir /fop
WORKDIR /fop

COPY target/apache-fop-server-1.9-bin.tar.gz /fop/

RUN tar xfvz /fop/apache-fop-server-1.9-bin.tar.gz

ENV JAVA_TOOL_OPTIONS "-XX:MaxRAMPercentage=80.0"

EXPOSE 9999
CMD ["java", "-cp", "apache-fop-server.jar:lib/*", "-Dlog4j.configuration=file:conf/log4j.properties", "org.zilverline.fop.FopServer"]
