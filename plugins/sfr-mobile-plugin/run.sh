#!/bin/bash

. ../commons.sh

SYNTHESE_TMP_FILE=synthese.pdf
DETAILS_TMP_FILE=details.pdf

# read configuration
declare -A config
read_properties configuration.properties

# remove potential old files
rm -fv ${SYNTHESE_TMP_FILE}
rm -fv ${DETAILS_TMP_FILE}

# download new files
vagrant up
vagrant ssh --command "cd /vagrant ; casperjs dl_invoice.js \"${config[sfr.user]}\" \"${config[sfr.pass]}\" \"${SYNTHESE_TMP_FILE}\" \"${DETAILS_TMP_FILE}\""
vagrant halt

# copy then in target directory
docFilteredPrefix=$(replace_var_in_var_last_month "${config[destination.document.prefix]}")
docFilteredLocation=$(replace_var_in_var_last_month "${config[destination.location]}")
mkdir -p "${docFilteredLocation}"
chmod 777 "${SYNTHESE_TMP_FILE}" "${DETAILS_TMP_FILE}"
cp "${SYNTHESE_TMP_FILE}" "${docFilteredLocation}/${docFilteredPrefix}-${SYNTHESE_TMP_FILE}"
cp "${DETAILS_TMP_FILE}"  "${docFilteredLocation}/${docFilteredPrefix}-${DETAILS_TMP_FILE}"
