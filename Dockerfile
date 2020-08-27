FROM openjdk:14-alpine
RUN apk --no-cache add curl
COPY target/rawdata-converter-app-bong-*.jar rawdata-converter-app-bong.jar
EXPOSE 8080
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-Dcom.sun.management.jmxremote", "--enable-preview", "-Xmx1g", "-jar", "rawdata-converter-app-bong.jar"]
