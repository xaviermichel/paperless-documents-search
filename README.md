[![Build Status](https://travis-ci.org/xaviermichel/paperless-documents-search.png?branch=develop)](https://travis-ci.org/xaviermichel/paperless-documents-search)

Paperless Documents Search
==========================

**Features**

This program is used in order to tidy your documents and provide an unique entry point to search in your files :

- Use scheduler for download and tidy your monthly documents ;
- Use smart engine for (auto-)tidying your scanned documents ;
- Index your documents from from your local drive (or anywhere else) and take benefice of OCR ;
- Search in your files content with a powerful search engine !

![application web](https://raw.githubusercontent.com/xaviermichel/paperless-documents-search/develop/.documentation-resources/edm_webapp.png)

Quick start
-----------

![Solution stack](https://docs.google.com/drawings/d/1TRDdSgP6r0zwp2dezgcPhncy-NdKfb9r6bKF52U0QUE/pub?w=939&amp;h=643)

**Compilation**

You want to compile and run it in two minutes ? You just need :
- [maven](http://maven.apache.org/download.cgi)
- [nodejs](https://nodejs.org/en/)
- [angular cli](https://cli.angular.io/)
- [docker, with docker-compose](https://docs.docker.com/install/).

You can compile the projects :
```bash
./scripts/run-build.sh
./scripts/run-build-images.sh
```

**Start the stack**

Great ! Now you can launch everything :
```bash
cd docker/edm_stack/
docker-compose up
```

**Index some documents**

```bash
java -jar edm-services/edm-filesystem-crawler/target/edm-filesystem-crawler-*.jar --edm.crawler.fs.edmServerHttpAddress=http://127.0.0.1:80 --edm.crawler.fs.rootPath=/media
```

**Explore your documents**

Open you browser on `http://localhost`, you can now search in your documents !


Similar Projects
----------------

You can also take a look at :
- https://github.com/danielquinn/paperless
- https://github.com/dadoonet/fscrawler

How to...
---------

To *enable OCR*, you need to install tesseract (`apt install -y tesseract-ocr tesseract-ocr-fra`) locally (is used by java app).

