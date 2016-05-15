#!/bin/bash

. ../commons.sh
. ./configuration.cfg

ACCOUNT_TMP_FILE=account.pdf

# remove potential old files
rm -fv ${ACCOUNT_TMP_FILE}

# download new files
casperjs --ssl-protocol=any dl_invoice.js "${ca_account_number}" "${ca_account_pass}" "${ACCOUNT_TMP_FILE}"
sleep 60

if grep -qv "%PDF" <<< $(head -c 4 "${ACCOUNT_TMP_FILE}" 2>&1); then
    echo "${ACCOUNT_TMP_FILE} ne semble pas etre un PDF, abandon"
    echo "* Exact reason : "
    head -c 4 "${ACCOUNT_TMP_FILE}" 2>&1
    exit 1
fi

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_last_month "${destination_document_prefix}")
docFilteredLocation=$(replace_var_in_var_last_month "${destination_location}")
mkdir -v -p "${docFilteredLocation}"
chmod -v 777 "${ACCOUNT_TMP_FILE}"
cp -v "${ACCOUNT_TMP_FILE}" "${docFilteredLocation}/${docFilteredPrefix}.pdf"
