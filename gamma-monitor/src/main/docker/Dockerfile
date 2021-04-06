FROM openjdk:8-jdk-alpine
MAINTAINER "heshixiong"<1466243240@qq.com>
VOLUME /tmp
ADD *.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]