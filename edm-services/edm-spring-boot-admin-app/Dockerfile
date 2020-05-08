FROM openjdk:11-jdk
EXPOSE 9002

ARG JAR_FILE=target/edm-spring-boot-admin-app-*.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
