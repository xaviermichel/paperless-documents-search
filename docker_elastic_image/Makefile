# Variables
# Could be override from commandline
IMAGE_NAME = paperless-documents-search-db

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

dstart:
	docker run --name paperless-documents-search-db-test -d -p 9200:9200 -p 9300:9300 paperless-documents-search-db

dstop:
	docker stop paperless-documents-search-db-test

drm: stop
	docker rm paperless-documents-search-db-test

dlogs:
	docker logs -f paperless-documents-search-db-test

