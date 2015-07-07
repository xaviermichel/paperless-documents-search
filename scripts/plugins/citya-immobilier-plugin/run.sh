#!/bin/bash

. ../commons.sh
. ./configuration.cfg

DOWNLOAD_INSTRUCTION_FILE=instructions.tmp
SYNTHESE_TMP_FILE=synthese.pdf

# remove potential old files
rm -fv ${DOWNLOAD_INSTRUCTION_FILE}
rm -fv ${SYNTHESE_TMP_FILE}

# get download instruction
casperjs dl_invoice.js "${citya_user}" "${citya_pass}" "${DOWNLOAD_INSTRUCTION_FILE}" "${SYNTHESE_TMP_FILE}"
sleep 60

# download the file
dl_command=$(cat ${DOWNLOAD_INSTRUCTION_FILE})

#eval ${dl_command}
# Because of curl in sh problem, I call another shell
/c/cygwin64/bin/bash -c "${dl_command}"

if grep -qv "%PDF" <<< $(head -c 4 "${SYNTHESE_TMP_FILE}" 2>&1); then
    echo "${SYNTHESE_TMP_FILE} ne semble pas etre un PDF, abandon"
    echo "* Exact reason : "
    head -c 4 "${SYNTHESE_TMP_FILE}" 2>&1
    exit 1
fi

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_current_month "${destination_document_prefix}")
docFilteredLocation=$(replace_var_in_var_current_month "${destination_location}")
mkdir -v -p "${docFilteredLocation}"
chmod -v 777 "${SYNTHESE_TMP_FILE}"
cp -v "${SYNTHESE_TMP_FILE}" "${docFilteredLocation}/${docFilteredPrefix}.pdf"
