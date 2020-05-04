#!/bin/bash
# generate some resources used for tests

# for ocr
convert -size 400x100 -pointsize 72 -gravity center label:bonjour demo.png
