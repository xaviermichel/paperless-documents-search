#!/bin/bash

# replace var in var for last month
#   $1 the var
#   output : std : var
function replace_var_in_var_last_month() {
    varValue=$1
    varValue="$(echo "${varValue}" | sed "s/{str{month}}/$(date --date='last month' +'%B')/g")"
    varValue="$(echo "${varValue}" | sed "s/{year}/$(date --date='last month' +'%Y')/g")"
    varValue="$(echo "${varValue}" | sed "s/{month}/$(date --date='last month' +'%m')/g")"
    echo ${varValue}
}

# replace var in var for current month
#   $1 the var
#   output : std : var
function replace_var_in_var_current_month() {
    varValue=$1
    varValue="$(echo "${varValue}" | sed "s/{str{month}}/$(date +'%B')/g")"
    varValue="$(echo "${varValue}" | sed "s/{year}/$(date +'%Y')/g")"
    varValue="$(echo "${varValue}" | sed "s/{month}/$(date +'%m')/g")"
    echo ${varValue}
}
