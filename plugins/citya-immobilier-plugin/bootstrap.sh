#!/usr/bin/env bash

apt-get update
apt-get install -y vim curl git build-essential libxml2-dev libxslt1-dev libcurl4-openssl-dev libsqlite3-dev libyaml-dev zlib1g-dev ruby1.9.1-dev ruby1.9.1 fontconfig

# node, latest
sudo apt-get install -y python-software-properties python g++ make
sudo add-apt-repository -y ppa:chris-lea/node.js
sudo apt-get update
sudo apt-get install -y nodejs

# phantomjs
wget https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-i686.tar.bz2
tar jxf phantomjs-*.tar.bz2
rm -f phantomjs-*.tar.bz2
ln -s $(pwd)/phantomjs-*/bin/phantomjs /usr/local/bin/

# casperjs
git clone https://github.com/n1k0/casperjs.git
cd casperjs
ln -sf $(pwd)/bin/casperjs /usr/local/bin/
