# Variables
# Could be override from commandline
IMAGE_NAME = paperless-documents-search-elasticsearch
CONTAINER_NAME = paperless-documents-search-elasticsearch

.PHONY: all build clean help start stop remove logs

# By default make command, without argument, call the first target in Makefile. In this case: help
help:
	@echo "Please use \`make <target>' where <target> is one of"
	@echo "    all      to build docker image $(IMAGE_NAME) - Present by convention, call build target"
	@echo "    build    to build docker image $(IMAGE_NAME)"
	@echo "    clean    to remove docker image $(IMAGE_NAME)"

clean:
	docker rmi $(IMAGE_NAME)

all: build

build:
	docker build --rm -t $(IMAGE_NAME) .

drun:
	docker run --name $(CONTAINER_NAME) -d -p 9200:9200 -p 9300:9300 $(IMAGE_NAME)

dstart:
	docker start $(CONTAINER_NAME)

dstop:
	docker stop $(CONTAINER_NAME)

drm: dstop
	docker rm $(CONTAINER_NAME)

dlogs:
	docker logs -f $(CONTAINER_NAME)

