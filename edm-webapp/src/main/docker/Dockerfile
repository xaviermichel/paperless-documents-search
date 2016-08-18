FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD paperless-documents-search-webapp-*.jar /paperless-documents-search-webapp.jar
RUN sh -c 'touch /paperless-documents-search-webapp.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/paperless-documents-search-webapp.jar"]
EXPOSE 8053
