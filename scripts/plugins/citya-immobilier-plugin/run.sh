#!/bin/bash

. ../commons.sh

DOWNLOAD_INSTRUCTION_FILE=instructions.tmp
SYNTHESE_TMP_FILE=synthese.pdf

# read configuration
declare -A config
read_properties configuration.properties

# remove potential old files
rm -fv ${DOWNLOAD_INSTRUCTION_FILE}
rm -fv ${SYNTHESE_TMP_FILE}

# get download instruction
vagrant up
vagrant ssh --command "cd /vagrant ; casperjs dl_invoice.js \"${config[citya.user]}\" \"${config[citya.pass]}\" \"${DOWNLOAD_INSTRUCTION_FILE}\" \"${SYNTHESE_TMP_FILE}\""
vagrant halt

# download the file
dl_command=$(cat ${DOWNLOAD_INSTRUCTION_FILE})
eval ${dl_command}

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_current_month "${config[destination.document.prefix]}")
docFilteredLocation=$(replace_var_in_var_current_month "${config[destination.location]}")
mkdir -p "${docFilteredLocation}"
chmod 777 "${SYNTHESE_TMP_FILE}"
cp "${SYNTHESE_TMP_FILE}" "${docFilteredLocation}/${docFilteredPrefix}.pdf"

