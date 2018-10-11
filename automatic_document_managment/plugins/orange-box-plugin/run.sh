#!/bin/bash

. ../commons.sh
. ./configuration.cfg

cp -v konnector-dev-config.json cozy-konnector-orange/
cd cozy-konnector-orange

# remove potential old files
if [ -d ./data ] ; then
    rm -fvr ./data/
fi

# init
yarn

# download invoice
yarn standalone

cd -

SYNTHESE_TMP_FILE=$(find ./cozy-konnector-orange/data -name '*.pdf')

if grep -qv "%PDF" <<< $(head -c 6 "${SYNTHESE_TMP_FILE}" 2>&1 | tail -1); then
    echo "${SYNTHESE_TMP_FILE} ne semble pas etre un PDF, abandon"
    echo "* Exact reason : "
    head -c 6 "${SYNTHESE_TMP_FILE}" 2>&1 | tail -1
    exit 1
fi

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_last_month "${destination_document_prefix}")
docFilteredLocation=$(replace_var_in_var_last_month "${destination_location}")
mkdir -v -p "${docFilteredLocation}"
chmod -v 777 "${SYNTHESE_TMP_FILE}"
cp -v "${SYNTHESE_TMP_FILE}" "${docFilteredLocation}/${docFilteredPrefix}.pdf"
