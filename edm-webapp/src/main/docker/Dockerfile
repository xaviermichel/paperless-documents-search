FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD simple-data-search-webapp-*.jar /simple-data-search-webapp.jar
RUN sh -c 'touch /simple-data-search-webapp.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/simple-data-search-webapp.jar"]
EXPOSE 8053
