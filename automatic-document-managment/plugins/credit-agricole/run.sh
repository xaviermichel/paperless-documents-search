#!/bin/bash

. ../commons.sh
. ./configuration.cfg

# remove potential old files
clean_data_directory

# download new files
node dl_invoice.js "${ca_account_number}" "${ca_account_pass}"
sleep 5

accountPdf=$(find ./data -name '*.pdf')

assert_is_valid_pdf ${accountPdf}

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_last_month "${destination_document_prefix}")
docFilteredLocation=$(replace_var_in_var_last_month "${destination_location}")
mkdir -v -p "${docFilteredLocation}"
chmod -v 777 "${accountPdf}"
cp -v "${accountPdf}" "${docFilteredLocation}/${docFilteredPrefix}.pdf"

