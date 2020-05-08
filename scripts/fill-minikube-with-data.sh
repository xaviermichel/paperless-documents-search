#!/bin/bash

java -jar edm-services/edm-filesystem-crawler/target/edm-filesystem-crawler-*.jar --edm.crawler.fs.edmServerHttpAddress=http://edm.local --edm.crawler.fs.rootPath=/home/$USER/Bureau

