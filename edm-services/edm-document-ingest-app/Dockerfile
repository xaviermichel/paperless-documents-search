FROM openjdk:11-jdk
EXPOSE 9002

RUN apt -qq update && \
    apt install -y tesseract-ocr tesseract-ocr-fra

ARG JAR_FILE=target/edm-document-ingest-app-*.jar
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
