#!/bin/bash

. ./xlog.sh

# path where are placed files before zipping them.
# /!\ The name will be root in the final zip
TMP_RELEASE_DIR=./simple-edm

# the final zip file, which is to release
RELEASE_FINAL_FILE=./simple-data-search-{{VERSION}}.zip

# always skip tests
MAVEN_TEST=-Dmaven.test.skip=true

# proxy
if [ ! -z "${HTTP_PROXY}" ]
then
	X_PROXY="`echo '-x'` `echo ${HTTP_PROXY}`"
	xlog INFO "Proxy is configured : ${X_PROXY}"
fi


#
# Vérifie que les version entre le code et maven sont identiques
#
# @param 1 Version du code
# @param 2 Version de maven
# @param 3 Module concerné
#
# Quitte en cas d'erreur
#
check_maven_code_version() {
	if [ "$1" != "$2" ]
	then
		xlog ERROR "Versions are different beetwen code and maven plugin : ${3}"
		xlog ERROR "You have to fix that before releasing file ! Arboring now"
		exit 1
	fi
}



cd ..


# version check
EDM_CONSTANT_VERSION=$(grep 'APPLICATION_VERSION' 'edm-webapp/src/main/resources/properties/constants.properties' | sed 's/^.*=\(.*\)$/\1/')

EDM_PARENT_VERSION=$(grep '<version>' 'pom.xml' | head -1 | sed 's/^.*>\(.*\)<.*$/\1/')
EDM_CONTRACT_VERSION=$(grep 'version' 'edm-contracts/pom.xml' | head -1 | sed 's/^.*>\(.*\)<.*$/\1/')
EDM_WEBAPP_VERSION=$(grep '<edm.version>' 'edm-webapp/pom.xml' | head -1 | sed 's/^.*>\(.*\)<.*$/\1/')
EDM_MIGRATION_VERSION=$(grep '<edm.version>' 'edm-mapping-migration/pom.xml' | head -1 | sed 's/^.*>\(.*\)<.*$/\1/')

xlog DEBUG "Parent version        : ${EDM_PARENT_VERSION}"
xlog DEBUG "Contracts version     : ${EDM_CONTRACT_VERSION}"
xlog DEBUG "Webapp version        : ${EDM_WEBAPP_VERSION}"
xlog DEBUG "Migration version     : ${EDM_MIGRATION_VERSION}"
xlog DEBUG "Constant code version : ${EDM_CONSTANT_VERSION}"

check_maven_code_version ${EDM_CONSTANT_VERSION} ${EDM_PARENT_VERSION} "simple-edm parent"
check_maven_code_version ${EDM_CONSTANT_VERSION} ${EDM_CONTRACT_VERSION} "simple-edm contracts"
check_maven_code_version ${EDM_CONSTANT_VERSION} ${EDM_WEBAPP_VERSION} "simple-edm webapp"
check_maven_code_version ${EDM_CONSTANT_VERSION} ${EDM_MIGRATION_VERSION} "simple-edm migration"

xlog INFO "Versions sounds pretty good !"

# replace version in vars
TMP_RELEASE_DIR=$(echo "${TMP_RELEASE_DIR}" | sed "s/{{VERSION}}/${EDM_CONSTANT_VERSION}/g")
RELEASE_FINAL_FILE=$(echo "${RELEASE_FINAL_FILE}" | sed "s/{{VERSION}}/${EDM_CONSTANT_VERSION}/g")

xlog DEBUG "Temporary release dir : ${TMP_RELEASE_DIR}"
xlog DEBUG "Released file : ${RELEASE_FINAL_FILE}"

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
xlog INFO "Resolving web libs (bower/grunt)"
bower install
grunt


cd scripts

xlog INFO "Preparing release directory"

mkdir -p ${TMP_RELEASE_DIR}

# main jar
cp ../edm-webapp/target/*.jar ${TMP_RELEASE_DIR}/simple-edm.jar

# webapp resources (exclude dev ressources)
mkdir -p ${TMP_RELEASE_DIR}/src/main/webapp
for directory in $(ls ../edm-webapp/src/main/webapp/resources) ; do
	if [ "${directory}" = "build" -o "${directory}" = "views" -o "${directory}" = "images" ] ; then
		cp -r ../edm-webapp/src/main/webapp/resources/${directory} ${TMP_RELEASE_DIR}/src/main/webapp/resources/
	fi
done

# embedded scripts
cp simple-edm-windows-starter.cmd ${TMP_RELEASE_DIR}

# create release file
zip -r ${RELEASE_FINAL_FILE} ${TMP_RELEASE_DIR}

# remove tmp dir
rm -fr ${TMP_RELEASE_DIR}

xlog INFO "${RELEASE_FINAL_FILE} is ready to be released"
xlog WARN "Do not forget to update your poms now !"


