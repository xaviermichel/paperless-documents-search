[![Build Status](https://travis-ci.org/xaviermichel/paperless-documents-search.png?branch=develop)](https://travis-ci.org/xaviermichel/paperless-documents-search)

Paperless Documents Search
==========================

**Features**

This program is used in order to tidy your documents and provide an unique entry point to search in your files :

- Use scheduler for download and tidy your monthly documents ;
- Use smart engine for (auto-)tidying your scanned documents ;
- Index your documents from from your local drive (or anywhere else) and take benefice of OCR ;
- Search in your files content with a powerful search engine !

Screenshot
----------

### Frontend, search in your documents !

![application web](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/develop/screenshots/edm_webapp.png)

### Backend (optionnal), manage document indexation, automatic crawling, ...

![gestionnaire d'indexation](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/develop/screenshots/edm_jenkins.png)


Quick start
-----------

![Solution stack](https://docs.google.com/drawings/d/1TRDdSgP6r0zwp2dezgcPhncy-NdKfb9r6bKF52U0QUE/pub?w=939&amp;h=643)

**Compilation**

You want to compile and run it in two minutes ? You just need [maven](http://maven.apache.org/download.cgi), a recent node version (with angular cli) and docker

You can compile the project core and run it !
```bash
mvn clean install -DskipTests
```

**Start the stack**

Great ! Now you can launch everything :
```bash
docker-compose up
```

**Index your documents**

It's time to index your documents ! If you don't want to add a external crawler, just use embedded filesystem crawler.
Hit `http://127.0.0.1:8054/crawl/filesystem/subdirectories?path=D:\data\docs\Documents` (adapt the path ;)).

**Explore your documents**

Open you browser on `http://127.0.0.1:8054`, you can now search in your documents !


Similar Projects
----------------

You can also take a look at :
- https://github.com/danielquinn/paperless
- https://github.com/dadoonet/fscrawler

Other
-----

**Code quality with sonar**

1. [Download sonar](http://www.sonarqube.org/downloads/)
2. Extract the zip and launch the adapted bin (you should have access to http://127.0.0.1:9000/)
3. In the "paperless-documents-search" directory, [launch sonar analysis](http://docs.codehaus.org/display/SONAR/Analyzing+with+Maven) :

```code:bash
mvn clean install
mvn sonar:sonar
```

**Useful commands**

To start an external elasticsearch :
```bash
cd edm-elasticsearch-docker-image/
make drun
cd -
```

To start a standalone webapp server :
```
docker run --rm --name paperless-documents-search-webapp --link paperless-documents-search-elasticsearch -p 8053:8053 -e 'SPRING_APPLICATION_JSON={"spring.data.elasticsearch.cluster-nodes": "paperless-documents-search-elasticsearch:9300" }' -v /media/documents/Documents:/media/documents:ro -d paperless-documents-search-webapp
```

Basic integration with curl :
```
curl -w '%{time_total}' 'http://127.0.0.1:8053/crawl/filesystem/subdirectories?path=/media/documents'
```

To work without integrate all documents content (which can be slow), you can activate `local` profile :
```code:bash
mvn spring-boot:run -Drun.profiles=local
# or
java -jar -Dspring.profiles.active=local target/paperless-documents-search-webapp*.jar
```

