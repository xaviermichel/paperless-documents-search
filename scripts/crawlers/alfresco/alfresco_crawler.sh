#!/bin/bash

#
# this crawler is sending all files in given alfresco site to simple-data-search
#

alfrescoUser=
alfrescoPass=
alfrescoHost=
alfrescoSite=

exclusionPattern="*.bz2|*.tar.gz|*.tif|*.zip|*.7z|*.jpg|*.jpeg|*.png"

edmsHost=http://127.0.0.1:8053
## You have to create the source with curl calls, for example
##     curl -XGET 'http://127.0.0.1:8053/category/name/Documents'
##    {"id":"iBhZrF8JTq6FEJvagwsjfQ","edmNodeType":"CATEGORY","parentId":null,"name":"Documents","description":""}
##     curl -XPOST 'http://127.0.0.1:8053/source' -H "Content-Type: application/json" -d '{"name" : "alfresco", "parentId" : "iBhZrF8JTq6FEJvagwsjfQ"}'
##    {"id":"dTdWqfXORq2aXiyMPcsnHA","edmNodeType":"SOURCE","parentId":"iBhZrF8JTq6FEJvagwsjfQ","name":"alfresco","description":null}
## So the parent is dTdWqfXORq2aXiyMPcsnHA
edmsSourceId=dTdWqfXORq2aXiyMPcsnHA
edmsSourceName=alfresco


function crawlDirectoryById() {

local id=$1
local ticket=$2

local tempFileName=curl.${id}.tmp.log

#echo "id=${id}, ticket=${ticket}"

curl -s -XGET "${alfrescoHost}/alfresco/service/api/node/workspace/SpacesStore/${id}/children?alf_ticket=${ticket}" > ${tempFileName}

local title=$(cat ${tempFileName} | grep 'title' | head -1 | sed 's,.*>\(.*\)</.*,\1,g')
local childCount=$(cat ${tempFileName} | grep '<opensearch:totalResults>' | head -1 | sed 's,.*>\(.*\)</.*,\1,g')

echo "Getting childrens of : ${title} (${childCount} childrens) [stored in ${tempFileName}]"

cat ${tempFileName} | grep '<id>' | tail -n +2 | while read line
do
	sleep 1 # be nice with the server

	docId=$(echo ${line} | sed 's,.*<id>urn:uuid:\(.*\)</id>,\1,g')
	docType=$(cat ${tempFileName} | grep "${docId}" -A 100 | grep 'cmis:objectTypeId' | head -1 | sed 's,.*<cmis:value>\(.*\)</cmis:value>.*,\1,g')

	if [[ "${docType}" == "cmis:document" ]] ; then
		url=$(echo ${line} | sed 's,.* src="\(.*\)"/>.*,\1,g')
		dlFile=$(cat ${tempFileName} | grep "${docId}" -A 100 | grep '<cmisra:pathSegment>' | head -1 | sed 's,.*<cmisra:pathSegment>\(.*\)</cmisra:pathSegment>.*,\1,g')
		docDate=$(cat ${tempFileName} | grep "${docId}" -A 100 | grep 'cmis:lastModificationDate' | head -1 | sed 's,.*<cmis:value>\(.*\)</cmis:value>.*,\1,g' | cut -c1-10)

		echo "cmis:document [${docId}], url = ${url} , dlFile = ${dlFile} , date = ${docDate}"

		matchingExclusions=$(echo ${dlFile} | grep -E "${exclusionPattern}" -c)
		if [ ${matchingExclusions} -ne 0 ] ; then
			echo "Match with exlusion pattern, skipping"
			continue
		fi

		# download file
		curl -u ${alfrescoUser}:${alfrescoPass} "${url}" -o "${dlFile}"

		# upload in edms
		temporyFileToken=$(curl -s -XPOST "${edmsHost}/document/upload" -F "file=@${dlFile}" | sed 's/.*temporaryFileToken":"\(.*\)".*/\1/g')

		# send doc informations
		curl -XPOST "${edmsHost}/document" -d "{
				\"date\" : \"${docDate}\",
				\"nodePath\"	: \"${url}\",
				\"edmNodeType\" : \"DOCUMENT\",
				\"name\" : \"${dlFile}\",
				\"temporaryFileToken\" : \"${temporyFileToken}\",
				\"parentId\" : \"${edmsSourceId}\"
		}" -H "Content-Type: application/json"


		rm "${dlFile}"

		continue
	fi

	if [[ "${docType}" == "cmis:folder" ]] ; then
		echo "cmis:folder [${docId}]"

		# recursive
		crawlDirectoryById "${docId}" "${ticket}"

		continue
	fi
done

echo "Deleting ${tempFileName}"
rm "${tempFileName}"

}

# login
ticket=$(curl -s -XGET "${alfrescoHost}/alfresco/service/api/login?u=${alfrescoUser}&pw=${alfrescoPass}" | grep '<ticket>' | sed 's,.*>\(.*\)</.*,\1,g')

if [ -z "${ticket}" ] ; then
	echo "Failed to login"
	exit
fi

echo "I got the ticket ${ticket}"

# get the root dir id
nodeId=$(curl -s "${alfrescoHost}/alfresco/service/api/sites/${alfrescoSite}?alf_ticket=${ticket}" | grep 'node' | cut -d: -f2 | sed 's,\\/,/,g' | sed 's,.*SpacesStore/\(.*\)".*,\1,g')
echo "Node id is ${nodeId}"

rootDirId=$(curl -s "${alfrescoHost}/alfresco/service/api/node/workspace/SpacesStore/${nodeId}/children?alf_ticket=${ticket}" | grep 'documentLibrary' -A 100 | grep 'cmis:objectId' | head -1 | sed 's,.*//SpacesStore/\(.*\)</cmis:value>.*,\1,g')
echo "Root dir id is ${rootDirId}"

# notify start crawling
curl -XGET "${edmsHost}/crawl/start?source=${edmsSourceName}"

# crawl
crawlDirectoryById "${rootDirId}" "${ticket}"

# notify end of crawling
curl -XGET "${edmsHost}/crawl/stop?source=${edmsSourceName}"

# logout
curl -u ${alfrescoUser}:${alfrescoPass} -XDELETE "${alfrescoHost}/alfresco/service/api/login/ticket/${ticket}"
echo "Is logout"
