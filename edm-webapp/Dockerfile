FROM openjdk:8

VOLUME /tmp

RUN apt -qq update && \
    apt install -y tesseract-ocr tesseract-ocr-fra
ADD target/paperless-documents-search-webapp-*.jar /webapp/paperless-documents-search-webapp.jar

RUN sh -c 'touch /webapp/paperless-documents-search-webapp.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/webapp/paperless-documents-search-webapp.jar"]

EXPOSE 8053
