# Variables
# Could be override from commandline
IMAGE_NAME = paperless-documents-search-db

.PHONY: all build clean help

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
