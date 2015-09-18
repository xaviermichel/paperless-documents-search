#!/bin/bash

. ./xlog.sh

# path where are placed files before zipping them.
# /!\ The name will be root in the final zip
TMP_RELEASE_DIR=./simple-data-search

# the final zip file, which is to release
RELEASE_FINAL_FILE=./simple-data-search-{{VERSION}}.zip

# always skip tests
MAVEN_TEST=-DskipTests

# proxy
if [ ! -z "${HTTP_PROXY}" ]
then
	X_PROXY="`echo '-x'` `echo ${HTTP_PROXY}`"
	xlog INFO "Proxy is configured : ${X_PROXY}"
fi

read -p "Release version ? " RELEASE_VERSION
read -p "Next snapshot version ? " SNAPSHOT_VERSION

# replace version in vars
TMP_RELEASE_DIR=$(echo "${TMP_RELEASE_DIR}" | sed "s/{{VERSION}}/${RELEASE_VERSION}/g")
RELEASE_FINAL_FILE=$(echo "${RELEASE_FINAL_FILE}" | sed "s/{{VERSION}}/${RELEASE_VERSION}/g")

xlog DEBUG "Temporary release dir : ${TMP_RELEASE_DIR}"
xlog DEBUG "Released file : ${RELEASE_FINAL_FILE}"

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

cd edm-webapp
echo "Maven packaging..."
mvn package -Dmaven.test.skip=true > /dev/null
if [ $? -eq 0 ]
then
	xlog INFO "maven packaging success"
else
	xlog ERROR "maven packaging has failed. Please fix errors before release"
	exit 3
fi


cd ..

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


cd scripts
xlog INFO "Preparing release directory"

mkdir -p ${TMP_RELEASE_DIR}

# main jar
cp ../edm-webapp/target/*.jar ${TMP_RELEASE_DIR}/simple-data-search.jar

# embedded scripts
cp simple-data-search.bat ${TMP_RELEASE_DIR}
cp simple-data-search.sh ${TMP_RELEASE_DIR}

# create release file
zip -r ${RELEASE_FINAL_FILE} ${TMP_RELEASE_DIR}

# remove tmp dir
rm -fr ${TMP_RELEASE_DIR}

# merge on master
git co master
git merge develop

xlog INFO "${RELEASE_FINAL_FILE} is ready to be released"
xlog INFO "If all sounds goods, just run : git push ; git push --tags"


