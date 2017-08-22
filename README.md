[![Build Status](https://travis-ci.org/xaviermichel/paperless-documents-search.png?branch=master)](https://travis-ci.org/xaviermichel/paperless-documents-search)

Paperless Documents Search
==========================

**Features**

This program is used in order to tidy your documents and provide an unique entry point to search in your files :

- Use scheduler for download and tidy your monthly documents ;
- Use smart engine for (auto-)tidying your scanned documents ;
- Index your documents from from your local drive (or anywhere else) and take benefice of OCR ;
- Search in your files content with a powerfull search engine !


Screenshot
----------

### Frontend, search in your documents !

![application web](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/master/screenshots/edm_webapp.png)

### Backend (optionnal), manage document indexation, automatic crawling, ...

![gestionnaire d'indexation](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/master/screenshots/edm_jenkins.png)


Quick start
-----------

**Software**

You can find the [latest release here](https://github.com/xaviermichel/paperless-documents-search/releases).

1. Download the zip, extract it and launch `start.bat` to start application !
2. Start an elasticsearch (version 2.0.2) node (cluster `simple_data_search`) with plugin [`mapper-attachments`](https://github.com/elastic/elasticsearch-mapper-attachments) (version 3.0.4).
3. Index your documents. The fastest way is to open you browser, and go to `http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\Documents` (adapt the path ;))
4. Open you browser on `http://127.0.0.1:8053`, you can now search in your documents !


Solution stack
--------------

![Solution stack](https://docs.google.com/drawings/d/1TRDdSgP6r0zwp2dezgcPhncy-NdKfb9r6bKF52U0QUE/pub?w=939&amp;h=643)


**Compilation**

Wanna compile and run it in two minutes ? You just need [maven](http://maven.apache.org/download.cgi) and docker (if you don't want to install elasticsearch).

First, clone the project
```bash
git clone https://github.com/xaviermichel/paperless-documents-search.git
cd paperless-documents-search
```

Prepare the elastic database :
```bash
cd docker_elastic_image
make build
cd -
```

Then, you can compile the project core and run it !
```bash
mvn clean install -DskipTests
cd edm-webapp
mvn package docker:build -DskipTests
cd -
```

Great ! Now you can launch everythings :
```bash
docker-compose up
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

You should use the jenkins job `create-elastic-mapping` which is handling aliases.
If you can't just take a look to the configuration (`automatic_document_managment/jenkins/jobs/create-elastic-mapping/config.xml`).


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
