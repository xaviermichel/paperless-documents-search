#!/bin/bash

git config --global core.autocrlf true

export PATH=${PATH}:/home/vagrant/bin/apache-maven-3.3.9/bin

cd /vagrant/edm-webapp/src/main/resources/elastic_settings
make build

# see https://github.com/npm/npm/issues/3565
unlink /vagrant/edm-webapp/node_modules
mkdir /home/vagrant/node_modules
ln -sf /home/vagrant/node_modules /vagrant/edm-webapp/node_modules
# /

cd /vagrant
mvn clean install -DskipTests

cd /vagrant/edm-webapp
mvn package docker:build -DskipTests

# if you wanna run "gulp test-casperjs" :
echo 'export PATH=${PATH}:/vagrant/edm-webapp/node_modules/phantomjs-prebuilt/bin:/vagrant/edm-webapp/node_modules/casperjs/bin' >> ~/.bashrc
