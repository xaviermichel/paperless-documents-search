#!/bin/bash

# TRACE, DEBUG, INFO, WARN, ERROR, FATAL

#
# configuration
#

log_level='DEBUG'
log_output=


log_date_format='+%Y-%m-%d_%H:%M:%S.%N'
log_date_color='0;36;40'


declare -A log_data_map

log_data_map[TRACE_pound]=5
log_data_map[TRACE_message]='TRACE'
log_data_map[TRACE_color]='0;37;40'

log_data_map[DEBUG_pound]=10
log_data_map[DEBUG_message]='DEBUG'
log_data_map[DEBUG_color]='0;37;40'

log_data_map[INFO_pound]=15
log_data_map[INFO_message]='INFO'
log_data_map[INFO_color]='0;36;40'

log_data_map[WARN_pound]=20
log_data_map[WARN_message]='WARN'
log_data_map[WARN_color]='0;33;40'

log_data_map[ERROR_pound]=30
log_data_map[ERROR_message]='ERROR'
log_data_map[ERROR_color]='0;31;40'

log_data_map[FATAL_pound]=35
log_data_map[FATAL_message]='FATAL'
log_data_map[FATAL_color]='0;37;41'


#
# Initialisation
#

log_level_pound=${log_data_map[${log_level}_pound]}


# $1 Level
# $* Message
function xlog() {
	level=$1
	shift

	if [ -z "${log_output}" ]; then
		xlog_color_line  $(date ${log_date_format}) ${level} $*
	else
		printf "%-30s %-6s $*\n" $(date ${log_date_format}) ${level} >> ${log_output}
	fi
}

function xlog_color_line() {
	line=$@

	level=$(echo $line | awk '{print $2}')
	level_pound=${log_data_map[${level}_pound]}
	if [ ${level_pound} -lt ${log_level_pound} ]; then
		return
	fi

	l_date=$1
	shift
	l_level=$1
	shift
	l_message=$*

	log_color=${log_data_map[${level}_color]}
	printf "\033[${log_date_color}m%-30s\033[0m \033[${log_color}m%-6s\033[0m ${l_message}\n" ${l_date} ${l_level}
}

function xlog_fill_log_file() {
	xlog TRACE this is an trace message
	xlog DEBUG this is an debug message
	xlog INFO  this is an info message
	xlog WARN  this is an warn message
	xlog ERROR this is an error message
	xlog FATAL this is an fatal message
	xlog DEBUG ------------------------
	xlog INFO "The log file (${log_output}) has been filled"
	xlog INFO "Now, I'm gonna read it"
}

function xlog_usage() {
	echo "Usage"
	echo "-----"
	echo ". xlog.sh                     # load xlog lib (use in another script)"
	echo "xlog.sh demo                  # fill a demo log file and print it"
	echo "xlog.sh usage                 # show help"
	echo "cat log.log | xlog.sh read    # process coloration on stdin"
	echo "xlog.sh log.log               # tailf the given file"
}

#
# main
#

if [ "$1" == "demo" ]; then
	xlog_fill_log_file
	cat ${log_output} | while read line ; do
                xlog_color_line $line
        done
	exit
elif [ "$1" == "usage" ]; then
	xlog_usage
	exit
elif [ "$1" == "read" ]; then
    echo -e "   \033[0;30;47m-> Reading STDIN\033[0m"
	while read line ; do
		xlog_color_line $line
	done
elif [ ! -z "$1" ]; then
	echo -e "   \033[0;30;47m-> Reading given file\033[0m"
	tail -f "$1" | while read line ; do
		xlog_color_line $line
	done
else
	echo "xlog lib loaded"
fi

