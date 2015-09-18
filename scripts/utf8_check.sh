#!/bin/sh

echo "Non UTF-8 detected files :"
echo "--------------------------"
find ../ -type f -not -path "./.git/*" -name "*.properties" -name "*.html" -name "*.js" -name "*.java" | xargs -I {} bash -c "iconv -f utf-8 -t utf-16 {} &>/dev/null || echo {}"

