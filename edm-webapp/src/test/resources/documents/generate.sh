#!/bin/bash
# generate some resources used for tests

# for ocr
convert -size 400x100 -pointsize 72 -gravity center label:bonjour hola.png

# for auto tidy suggestion
FILE_NAME=some_bill.pdf
convert -depth 8 -density 300 -alpha Off ${FILE_NAME} ${FILE_NAME}.tiff
tesseract ${FILE_NAME}.tiff -l fra - | sed '/^[[:space:]]*\$/d' > ${FILE_NAME}.txt
rm -v ${FILE_NAME}.tiff

