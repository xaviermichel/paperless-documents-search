[![Build Status](https://travis-ci.org/xaviermichel/simple-data-search.png?branch=master)](https://travis-ci.org/xaviermichel/simple-data-search)


Simple Data Search
==================

This program is an only entry point to search in your files.
You launch it, index one (or more) directory on your filesystem and you can search in the indexeds files.

**Features**

- easy to use (it's like a web application)
- powerful search engine
- scalability (see scalability section)
- exposed REST api


Screenshot
----------

![application web](https://raw.githubusercontent.com/xaviermichel/simple-data-search/master/screenshots/edm_webapp.png)

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

Then, download and compress resources for production compilation
```bash
bower install
gulp minify-code	# will prepare webapp resources for release
```

Now, you can compile the project core and run it
```bash
mvn install -Dmaven.test.skip=true
cd edm-webapp
mvn package -Dmaven.test.skip=true
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


Downloads
---------

**Software**

You can find the [lastest release here](https://github.com/xaviermichel/simple-data-search/releases).


Mapping migration
-----------------

On standalone application (local node) :
- delete `edm` directory
- restart the application
- reindex your documents !

On application connected with external elastic (assuming that you use a `documents` alias which point `documents_1`, `documents_2` is index to update)
- delete `documents_2`
```bash
curl -XDELETE "http://127.0.0.1:9200/documents_2"
```
- re-create mapping for `documents_2` :
```bash
curl -XPUT "http://127.0.0.1:9200/documents_2" -d "@./documents.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/category" -d "@./documents/category.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/source" -d "@./documents/source.json"
curl -XPUT "http://127.0.0.1:9200/documents_2/_mapping/document_file" -d "@./documents/document_file.json"
```
- reindex your documents, with bash script, should looks like (the best way may be to copy and adapt `alfreso_crawler.sh`) :
```bash
???
```
- switch alias !
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

