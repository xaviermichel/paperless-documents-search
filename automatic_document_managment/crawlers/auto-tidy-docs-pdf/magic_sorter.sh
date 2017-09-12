#!/bin/bash

. ../commons.sh

CONF_FILE=./sorting_rules.cfg

file_to_scan=$1
echo "Looking for patterns in $file_to_scan"

if [ -z "${file_to_scan}" ]; then
	echo "No input file, arboring"
	exit 1
fi

cat $CONF_FILE | grep -Ev '^#' | grep -v '^$' | while read line
do
	pattern=$(echo "$line" | awk -F'=' '{print $1}')
	dest_path=$(echo "$line" | awk -F'=' '{print $2}')
	echo -n "searching for '$pattern'... "
	if grep -E -i -q "${pattern}" "${file_to_scan}"
	then
		echo "FOUND !"

		expended_dest_path=$dest_path
		expended_dest_path=$(replace_var_in_var_current_month "${expended_dest_path}")
		expended_dest_path=$(replace_var_in_var_last_month "${expended_dest_path}")

		echo "Destination path : ${expended_dest_path}"
		echo ${expended_dest_path} > "${file_to_scan}.dest"

		# finish search
		break
	else
		echo "not found"
	fi
done

