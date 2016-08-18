#!/usr/bin/env bash

proxy_hostport=$(echo $HTTP_PROXY | awk -F/ '{print $3}')
proxy_host=$(echo $proxy_hostport | awk -F: '{print $1}')
proxy_port=$(echo $proxy_hostport | awk -F: '{print $2}')

    #===================#
    #  basic dev tools  #
    #===================#

apt-get -qq update
apt-get install -qq -y unzip vim telnet
echo "export PATH=/usr/local/bin:$PATH" > /etc/profile

    #===================#
    # docker extra conf #
    #===================#

if [ ! -z "${HTTP_PROXY}" ]; then
    mkdir -p /etc/systemd/system/docker.service.d
    touch /etc/systemd/system/docker.service.d/http-proxy.conf

	echo "[Service]" >> /etc/systemd/system/docker.service.d/http-proxy.conf
	echo "Environment=\"HTTP_PROXY=${HTTP_PROXY}/\"" >> /etc/systemd/system/docker.service.d/http-proxy.conf

    service docker restart
fi

    #===================#
    #      java 8       #
    #===================#

echo "java 8 installation"
apt-get install -qq --yes python-software-properties software-properties-common
add-apt-repository ppa:webupd8team/java
apt-get update -qq
echo debconf shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | /usr/bin/debconf-set-selections
apt-get install -qq --yes oracle-java8-installer
apt-get install -qq --yes oracle-java8-set-default

    #===================#
    #       maven       #
    #===================#

cd /home/vagrant
mkdir bin
cd bin
wget --quiet ftp://mirrors.ircam.fr/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar xvzf apache-maven-3.3.9-bin.tar.gz
echo 'export PATH=${PATH}:/home/vagrant/bin/apache-maven-3.3.9/bin' >> /home/vagrant/.bashrc
# proxy ?
if [ ! -z "${HTTP_PROXY}" ] ; then
    echo "Configuring proxy for maven..."
    mkdir /home/vagrant/.m2
    cat >/home/vagrant/.m2/settings.xml <<EOL
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd">
    <proxies>
        <proxy>
            <port>${proxy_port}</port>
            <host>${proxy_host}</host>
            <nonProxyHosts>127.0.0.1|localhost</nonProxyHosts>
        </proxy>
    </proxies>
</settings>
EOL
    chown -R vagrant:vagrant /home/vagrant/.m2
fi

    #===================#
    #       nodejs      #
    #===================#

apt-get install -qq -y python-software-properties python g++ make
add-apt-repository -y ppa:chris-lea/node.js
apt-get -qq update
apt-get install -qq -y nodejs

npm update -g npm
npm install -g bower gulp

# proxy ?
if [ ! -z "${HTTP_PROXY}" ] ; then
cat >/home/vagrant/.bowerrc <<EOL
{
    "proxy": "http://${proxy_host}:${proxy_port}",
    "https-proxy": "http://${proxy_host}:${proxy_port}"
}
EOL
chown vagrant:vagrant /home/vagrant/.bowerrc
fi

    #===========================#
    # resources for front tests #
    #===========================#

apt-get install -qq -y xvfb libfontconfig1 firefox
