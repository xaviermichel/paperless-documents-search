#!/bin/bash

#
# Xavier - Deploiement sur repo maven github
#

. ./xlog.sh

REPO_LOCATION=../maven-repo


current_version=$(grep '<version>' ../pom.xml | head -1 | sed 's/^.*>\(.*\)<.*$/\1/')

deployment=snapshot
if [[ ${current_version} == *SNAPSHOT* ]]
then
	xlog INFO "Making a SNAPSHOT deployment"
else
	xlog INFO "Making a RELEASE deployment"
	deployment=release
fi

cd ..

for subProject in edm-contracts edm-mapping-migration edm-embedded-crawler
do
	cd ${subProject}
	mvn -DaltDeploymentRepository=${deployement}-repo::default::file:../${REPO_LOCATION}/${deployment}s deploy -Dmaven.test.skip=true
	cd -
done
# and release parent pom
mvn -DaltDeploymentRepository=${deployment}-repo::default::file:../maven-repo/${deployment}s deploy -N

cd scripts

if [ $? -eq 0 ]
then
	xlog INFO "${deployment} deployment success, you just need to add, commit and push the git repo now"
else
	xlog ERROR "${deployment} deployment failure, fix it and try again !"
	exit
fi

