#!/bin/bash
set -ev

push_image() {
    docker tag $1 localhost:5000/$1
    docker push localhost:5000/$1
}

docker run --name=minikube-registry-link --rm --network=host alpine ash -c "apk add socat && socat TCP-LISTEN:5000,reuseaddr,fork TCP:$(minikube ip):5000" &
sleep 3

push_image paperless-documents-search-elasticsearch
push_image edm-document-ingest-app
push_image edm-document-repository-app
push_image edm-ui
push_image edm-spring-boot-admin-app

docker stop minikube-registry-link

