#!/bin/bash

. ../commons.sh
. ./configuration.cfg

SYNTHESE_TMP_FILE=synthese.pdf

# remove potential old files
rm -fv ${SYNTHESE_TMP_FILE}

# download new files
casperjs --ignore-ssl-errors=true --ssl-protocol=tlsv1 dl_invoice.js "${orange_user}" "${orange_pass}" "${SYNTHESE_TMP_FILE}"
sleep 60

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
