#!/bin/bash

. ../commons.sh
. ./configuration.cfg

# remove potential old files
clean_data_directory

fromDate=$1
toDate=$2

if [ -z "${fromDate}" ]; then
    fromDate=$(date --date="20 days ago" +"%d/%m/%Y")
fi
if [ -z "${toDate}" ]; then
    toDate=$(date --date="5 days ago" +"%d/%m/%Y")
fi

# download new files
node dl_invoice.js "${ca_account_number}" "${ca_account_pass}" "${fromDate}" "${toDate}"
sleep 5

accountCsv=$(find ./data -name '*.csv')
if [ -z "${accountCsv}" ]; then
    echo "Failed to download CSV"
    exit 1
fi

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_last_month "${destination_document_prefix}")
docFilteredLocation=$(replace_var_in_var_last_month "${destination_location}")
mkdir -v -p "${docFilteredLocation}"
chmod -v 777 "${accountCsv}"
cp -v "${accountCsv}" "${docFilteredLocation}/${docFilteredPrefix}.csv"

