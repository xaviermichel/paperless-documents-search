#!/bin/bash
set -ev

buildBack=true
buildFront=true
if [[ "$*" == *SKIP_BACK* ]]; then
    buildBack=false
elif [[ "$*" == *SKIP_FRONT* ]]; then
    buildFront=false
fi

if $buildBack; then
    for s in edm-contracts edm-utils edm-document-ingest-app edm-document-repository-app edm-filesystem-crawler
    do
        cd edm-services/$s
        mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V
        cd -
    done
fi

if $buildFront; then
    cd edm-ui
    npm install
    npm run build
fi

