[![Build Status](https://travis-ci.org/xaviermichel/simple-data-search.png?branch=master)](https://travis-ci.org/xaviermichel/simple-data-search)


Simple Data Search
==================

This program is an only entry point to search in your files.
You launch it, index one (or more) directory on your filesystem and you can search in the indexeds files.

**Features (strongly based on simple-edm)**

- easy to use (it's like a web application)
- powerful search engine
- scalability (see scalability section)
- exposed REST api

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
sudo npm install -g grunt-cli
sudo npm install -g bower
```

Then, download and compress resources for production compilation
```bash
bower install
grunt			# will prepare webapp resources for release
```

Now, you can compile the project core and run it
```bash
mvn install -Dmaven.test.skip=true
cd edm-webapp
mvn package -Dmaven.test.skip=true
java -jar target/edm-webapp-*.jar
```

It's time to index your documents ! If you don't wan't to add a external crawler, just use embedded filesystem crawler :
```
http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\simple-edm\simple-edm\edm\files
// or with more informations (see EdmCrawlingController) :
http://127.0.0.1:8053/crawl/filesystem?path=D:\data\docs\simple-edm\simple-edm\edm\files&sourceName=Mes documents&categoryName=Documents
```

Downloads
---------

**Software**

You can find the [lastest release here](https://github.com/xaviermichel/simple-data-search/releases).


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


