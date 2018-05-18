#!/bin/bash

. ./xlog.sh

# always skip tests
MAVEN_TEST=-DskipTests

read -p "Release version ? " RELEASE_VERSION
read -p "Next snapshot version ? " SNAPSHOT_VERSION

cd ..

xlog INFO "Setting version to ${RELEASE_VERSION}"
mvn versions:set -DnewVersion=${RELEASE_VERSION} > /dev/null

xlog INFO "Maven compilation..."

mvn clean install ${MAVEN_TEST} | grep -B 1 -A 100 'Reactor Summary:'
if [ $? -eq 0 ]
then
	xlog INFO "maven compilation success"
else
	xlog ERROR "maven compilation has failed. Please fix errors before release"
	exit 2
fi

mvn package ${MAVEN_TEST} > /dev/null
if [ $? -eq 0 ]
then
	xlog INFO "maven packaging success"
else
	xlog ERROR "maven packaging has failed. Please fix errors before release"
	exit 3
fi

xlog INFO "Adding all pom.xml for release commit"
find . -name 'pom.xml' -exec git add {} \;
git commit -m "chore: release ${RELEASE_VERSION}"

xlog INFO "Adding git tag (${RELEASE_VERSION})"
git tag -a v${RELEASE_VERSION} -m "Version ${RELEASE_VERSION}"

xlog INFO "Setting version to ${SNAPSHOT_VERSION}"
mvn versions:set -DnewVersion=${SNAPSHOT_VERSION} > /dev/null

xlog INFO "Adding all pom.xml for snapshot commit"
find . -name 'pom.xml' -exec git add {} \;
git commit -m "chore: next snapshot (${SNAPSHOT_VERSION})"

# merge on master
git co master
git merge develop

xlog INFO "${RELEASE_VERSION} is ready to be released"
xlog INFO "If all sounds goods, just run : git push ; git push --tags"

