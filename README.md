[![Build Status](https://travis-ci.org/xaviermichel/simple-data-search.png?branch=master)](https://travis-ci.org/xaviermichel/simple-data-search)


Simple Data Search
==================

This program is an only entry point to search in your files.
You launch it, index one (or more) directory on your filesystem and you can search in the indexeds files.

**Features**

- easy to deploy (can be run with one jar)
- easy to use (it's a web application)
- powerful search engine
- scalability
- exposed REST api


Quick start
-----------

**Software**

You can find the [lastest release here](https://github.com/xaviermichel/simple-data-search/releases).

1. Download the zip, extract it and launch `simple-data-search.bat` to start application !
2. Index your documents. The fastest way is to open you brower, and go to `http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\Documents`
3. Open you brower on `http://127.0.0.1:8053`, you can now search in your documents !


Screenshot
----------

### Frontend, search in your documents !

![application web](https://raw.githubusercontent.com/xaviermichel/simple-data-search/master/screenshots/edm_webapp.png)

### Backend (optionnal), manage document indexation, automatic crawling, ...

![gestionnaire d'indexation](https://raw.githubusercontent.com/xaviermichel/simple-data-search/master/screenshots/edm_jenkins.png)


Solution stack
--------------

![Solution stack](https://docs.google.com/drawings/d/1TRDdSgP6r0zwp2dezgcPhncy-NdKfb9r6bKF52U0QUE/pub?w=939&amp;h=643)

**Compilation**

Wanna compile and run it in two minutes ? You need [nodejs](http://nodejs.org/) and [maven](http://maven.apache.org/download.cgi).

First, clone the project
```bash
git clone https://github.com/xaviermichel/simple-data-search.git
cd simple-data-search
```

Now, install node required modules
```bash
sudo npm install
sudo npm install -g gulp-cli
sudo npm install -g bower
```

Now, you can compile the project core and run it
```bash
mvn clean install -DskipTests
cd edm-webapp
mvn package -DskipTests
java -jar target/simple-data-search-webapp*.jar
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
3. reindex your documents !

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
3. create main category
```
curl -XPOST 'http://127.0.0.1:9200/documents_2/category' -H "Content-Type: application/json" -d "{
    \"name\" : \"Documents\",
    \"description\": \"Espace contenant mes documents\",
    \"edmNodeType\": \"CATEGORY\"
}"
```
4. reindex your documents, with bash crawler :
```bash
# launch the crawler...
scripts/crawlers/local_files/local_file_crawler.sh
# or use jenkins !
```
5. switch alias !
```bash
curl -XPOST 'http://127.0.0.1:9200/_aliases' -d '
{
    "actions" : [
        { "remove" : { "index" : "documents_1", "alias" : "documents" } },
        { "add" : { "index" : "documents_2", "alias" : "documents" } }
    ]
}'
```

Other
-----

**Code quality with sonar**

1. [Download sonar](http://www.sonarqube.org/downloads/)
2. Extract the zip and launch the adapted bin (you should have access to http://127.0.0.1:9000/)
3. In the "simple-data-search" directory, [launch sonar analysis](http://docs.codehaus.org/display/SONAR/Analyzing+with+Maven) :

```code:bash
mvn clean install
mvn sonar:sonar
```

**Debug mode (non minified js)**

Just add `?debug=true` in url : http://127.0.0.1:8053/?debug=true/#/


**Front tests with protractor**

You need to start a webdriver locally :
```code:bash
cd edm-webapp
./node_modules/protractor/bin/webdriver-manager update
./node_modules/protractor/bin/webdriver-manager start
```

On another shell, start edm-webapp on port 9053 :
```code:bash
mvn spring-boot:run -D"run.arguments=--server.port=9053"
```

You can now run front e2e tests !
```code:bash
gulp protractor
```


Commit Message Format (inspired from angular format)
----------------------------------------------------

Each message should respect this pattern :

    <type>: <subject>

### Type
Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing
  semi-colons, etc)
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **perf**: A code change that improves performance
* **test**: Adding missing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation
  generation
