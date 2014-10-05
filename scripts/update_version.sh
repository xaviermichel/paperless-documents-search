#!/bin/bash

. ./xlog.sh

OLD_VERSION=1.1-SNAPSHOT
NEW_VERSION=1.0-SNAPSHOT

VERSION_OCCURENCE_COUNT=9

xlog INFO "Updating from ${OLD_VERSION} to ${NEW_VERSION}"

cd ..

mvn clean > /dev/null

version_count=$(find . \( -name 'pom.xml' -o -name 'constants.properties' \) -exec grep -Hn "${OLD_VERSION}" {} \; | wc -l)
if [ ! ${VERSION_OCCURENCE_COUNT} -eq ${version_count} ]; then
	xlog ERROR "Version count conflict, won't update versions (expected ${VERSION_OCCURENCE_COUNT}, had ${version_count})"
	exit 1
fi

find . \( -name 'pom.xml' -o -name 'constants.properties' \) -exec sed -i "s/${OLD_VERSION}/${NEW_VERSION}/g" {} \;

cd -

xlog INFO "Versions number have been updated to ${NEW_VERSION}"

