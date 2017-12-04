#!/bin/bash

. ../commons.sh
. ./configuration.cfg

ACCOUNT_TMP_FILE=account.pdf
ACCOUNT_TMP_FILE_2=account_2.pdf

# remove potential old files
rm -fv ${ACCOUNT_TMP_FILE} ${ACCOUNT_TMP_FILE_2}

# download new files
casperjs --ssl-protocol=any dl_invoice.js "${ca_account_number}" "${ca_account_pass}" "${ACCOUNT_TMP_FILE}" "${ACCOUNT_TMP_FILE_2}"
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


# deal with ACCOUNT_TMP_FILE_2 if neccesay
if [ -z "${destination_location2}" ]; then
	echo "destination_location2 is empty, won't deal with document 2"
else
	if grep -qv "%PDF" <<< $(head -c 4 "${ACCOUNT_TMP_FILE_2}" 2>&1); then
		echo "${ACCOUNT_TMP_FILE_2} ne semble pas etre un PDF, abandon"
		echo "* Exact reason : "
		head -c 4 "${ACCOUNT_TMP_FILE_2}" 2>&1
		exit 1
	fi

	# copy then in target directory
	docFilteredPrefix2=$(replace_var_in_var_last_month "${destination_document_prefix2}")
	docFilteredLocation2=$(replace_var_in_var_last_month "${destination_location2}")
	mkdir -v -p "${docFilteredLocation2}"
	chmod -v 777 "${ACCOUNT_TMP_FILE_2}"
	cp -v "${ACCOUNT_TMP_FILE_2}" "${docFilteredLocation2}/${docFilteredPrefix2}.pdf"
fi

