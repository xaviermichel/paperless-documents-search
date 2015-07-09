#!/bin/bash

#
# this crawler is sending all files under rootDirectory to elastic
#

exclusionPattern="*.bz2|*.tar.gz|*.tif|*.zip|*.7z|*.git*"

rootDirectory="/d/data/docs/Documents"

# if you leave sourceId empty, one will be generated for you
edmsSourceId=

elasticHost=http://127.0.0.1:9200
elasticIndex=documents_2


# if source id is empty, create source
if [ -z "${edmsSourceId}" ]; then
    sourceName=local_crawler
    echo "Creating source with name ${sourceName}..."

    edmCategoryId=$(curl -XGET "${elasticHost}/${elasticIndex}/category/_search?q=name:Documents" | grep '_id' | sed 's/.*_id":"\(.*\)",.*/\1/g')

    # does source already exists ?
    edmsSourceId=$(curl -XGET "${elasticHost}/${elasticIndex}/source/_search?q=name:${sourceName}" | grep '_id' | head -1 | sed 's/.*_id":"\(.*\)",.*/\1/g')

    # if not, create it
    if [ -z "${edmsSourceId}" ]; then
        curl -XPOST "${elasticHost}/${elasticIndex}/source" -H "Content-Type: application/json" -d "{
            \"name\" : \"${sourceName}\",
            \"description\": \"Documents recuperes par le crawler local\",
            \"edmNodeType\": \"SOURCE\",
            \"parentId\": \"${edmCategoryId}\"
        }"

        edmsSourceId=$(curl -XGET "${elasticHost}/${elasticIndex}/source/_search?q=name:${sourceName}" | grep '_id' | head -1 | sed 's/.*_id":"\(.*\)",.*/\1/g')
    fi
fi

echo "Source id : ${edmsSourceId}"

# crawling !
find "${rootDirectory}" -type f | while read fullPath
do
    sleep 1 # be nice with the server
    echo "Working on ${fullPath}"

    matchingExclusions=$(echo "${fullPath}" | grep -E "${exclusionPattern}" -c)
    if [ ${matchingExclusions} -ne 0 ] ; then
        echo "Match with exlusion pattern, skipping"
        continue
    fi

    fileContent=$(base64 "${fullPath}" | sed ':a;N;$!ba;s/\n//g')
    fileDate=$(date -r "${fullPath}" +%F)
    fileBaseName=$(basename "${fullPath}")
    fileExtension=$(echo "${fullPath}" | awk -F'[.]' '{print $NF}')

    curl -XPOST ${elasticHost}/${elasticIndex}/document_file -d "{
        \"file\"            : {
            \"_content\" : \"${fileContent}\",
            \"_language\" : \"fr\"
        },
        \"date\"            : \"${fileDate}\",
        \"nodePath\"        : \"${fullPath}\",
        \"edmNodeType\"     : \"DOCUMENT\",
        \"name\"            : \"${fileBaseName}\",
        \"parentId\"        : \"${edmsSourceId}\",
        \"fileExtension\"   : \"${fileExtension}\",
        \"fileContentType\" : \"${fileExtension}\"
    }" -H "Content-Type: application/json"
done
