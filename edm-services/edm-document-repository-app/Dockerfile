FROM openjdk:11-jdk
EXPOSE 9001

ARG JAR_FILE=target/edm-document-repository-app-*.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
