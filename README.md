[![Build Status](https://travis-ci.org/xaviermichel/paperless-documents-search.png?branch=master)](https://travis-ci.org/xaviermichel/paperless-documents-search)

Paperless Documents Search
==================

> This program is an only entry point to search in your files. Just :
> - start it ;
> - index one (or more) directory from your local drive (or anywhere else) ;
> - search in your files content !

**Features**

- Out of the box
- Powerful search engine, easily customisable
- Frontend with shortcuts to search in your documents
- Exposed REST api
- OCR support : find text in your images !

Quick start
-----------

**Software**

You can find the [latest release here](https://github.com/xaviermichel/paperless-documents-search/releases).

1. Download the zip, extract it and launch `start.bat` to start application !
2. Index your documents. The fastest way is to open you browser, and go to `http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\Documents` (adapt the path ;))
3. Open you browser on `http://127.0.0.1:8053`, you can now search in your documents !


Screenshot
----------

### Frontend, search in your documents !

![application web](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/master/screenshots/edm_webapp.png)

### Backend (optionnal), manage document indexation, automatic crawling, ...

![gestionnaire d'indexation](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/master/screenshots/edm_jenkins.png)


Solution stack
--------------

![Solution stack](https://docs.google.com/drawings/d/1TRDdSgP6r0zwp2dezgcPhncy-NdKfb9r6bKF52U0QUE/pub?w=939&amp;h=643)

**Compilation**

Wanna compile and run it in two minutes ? You just need [maven](http://maven.apache.org/download.cgi).

First, clone the project
```bash
git clone https://github.com/xaviermichel/paperless-documents-search.git
cd paperless-documents-search
```

Then, you can compile the project core and run it !
```bash
mvn clean install -DskipTests
cd edm-webapp
mvn package -DskipTests
java -jar target/paperless-documents-search-webapp*.jar
```

It's time to index your documents ! If you don't wan't to add a external crawler, just use embedded filesystem crawler :
```
http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\Documents
// or with more informations (see EdmCrawlingController) :
http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\Documents&sourceName=Mes documents&categoryName=Documents
// you can also exclude some pattern
http://127.0.0.1:8053/crawl/filesystem?path=D:\data\dossier_personnel\github\alerts-app&exclusionRegex=\.git|\.vagrant
```

Mapping migration
-----------------

### On standalone application (local node, with downtime)
1. delete `edm` directory
2. restart the application
3. re-index your documents !

### On application connected with external elastic (without downtime)

Assuming that you use a `documents` alias which point `documents_1`, `documents_2` is index to update. Elastic has elasticsearch-mapper-attachments installed.

1. delete `documents_2`
```bash
curl -XDELETE "http://127.0.0.1:9200/documents_2"
```
2. re-create mapping for `documents_2` :
```bash
curl -XPUT "http://127.0.0.1:9200/documents_2" -d "@./documents.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/category" -d "@./documents/category.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/source" -d "@./documents/source.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/document_file" -d "@./documents/document_file.json"
```
3. reindex your documents, with bash crawler :
```bash
# launch the crawler...
scripts/crawlers/local_files/local_file_crawler.sh
# or use jenkins !
```
4. switch alias !
```bash
curl -XPOST 'http://127.0.0.1:9200/_aliases' -d '
{
    "actions" : [
        { "remove" : { "index" : "documents_1", "alias" : "documents" } },
        { "add" : { "index" : "documents_2", "alias" : "documents" } }
    ]
}'
```

Similar Projects
----------------

You can also take a look at https://github.com/danielquinn/paperless


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

**Debug mode (non minified js)**

To work with un-merged resources, you can activate `local` profile :
```code:bash
mvn spring-boot:run -Drun.profiles=local
# or
java -jar -Dspring.profiles.active=local target/paperless-documents-search-webapp*.jar
```

**Front tests with casperjs**

This tests are run on travis. If you wanna run them localy, you have to start the webapp (`mvn spring-boot:run`) and then run :
```code:bash
gulp test-casperjs
```

>>>>>>> feat-spring-boot-1.4
