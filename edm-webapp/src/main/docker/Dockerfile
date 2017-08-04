FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD paperless-documents-search-webapp-*.jar /webapp/paperless-documents-search-webapp.jar
RUN sh -c 'touch /webapp/paperless-documents-search-webapp.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/webapp/paperless-documents-search-webapp.jar"]
EXPOSE 8053
