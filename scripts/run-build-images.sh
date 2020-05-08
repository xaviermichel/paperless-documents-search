#!/bin/bash
set -ev

buildTools=true
buildBack=true
buildFront=true
if [[ "$*" == *SKIP_BACK* ]]; then
    buildBack=false
elif [[ "$*" == *SKIP_FRONT* ]]; then
    buildFront=false
elif [[ "$*" == *SKIP_TOOLS* ]]; then
    buildTools=false
fi

if $buildTools; then
    cd docker/development_tools/elasticsearch
    make build
    cd -
fi

if $buildBack; then
    cd edm-services/edm-document-repository-app
    docker build -t edm-document-repository-app .
    cd -

    cd edm-services/edm-document-ingest-app
    docker build -t edm-document-ingest-app .
    cd -

    cd edm-services/edm-spring-boot-admin-app
    docker build -t edm-spring-boot-admin-app .
    cd -
fi

if $buildFront; then
    cd edm-ui
    docker build -t edm-ui .
    cd -
fi
