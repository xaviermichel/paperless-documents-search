#!/bin/bash

LANG="fr_FR.utf-8"
LC_COLLATE="fr_FR.utf-8"
LC_TIME="fr_FR.utf-8"

#   $* the var
#   output : std : var
function customReplacement() {
    varValue=$*
    # some sh has not french lang
    varValue=$(echo "${varValue}" | sed "s/January/janvier/g")
    varValue=$(echo "${varValue}" | sed "s/February/février/g")
    varValue=$(echo "${varValue}" | sed "s/March/mars/g")
    varValue=$(echo "${varValue}" | sed "s/April/avril/g")
    varValue=$(echo "${varValue}" | sed "s/May/mai/g")
    varValue=$(echo "${varValue}" | sed "s/June/juin/g")
    varValue=$(echo "${varValue}" | sed "s/July/juillet/g")
    varValue=$(echo "${varValue}" | sed "s/August/août/g")
    varValue=$(echo "${varValue}" | sed "s/September/septembre/g")
    varValue=$(echo "${varValue}" | sed "s/October/octobre/g")
    varValue=$(echo "${varValue}" | sed "s/November/novembre/g")
    varValue=$(echo "${varValue}" | sed "s/December/décembre/g")
    echo ${varValue}
}

# replace var in var for last month
#   $1 the var
#   output : std : var
function replace_var_in_var_last_month() {
    varValue=$1
    varValue="$(echo "${varValue}" | sed "s/{str{month}}/$(LANG=fr_FR date --date='last month' +'%B')/g")"
    varValue="$(echo "${varValue}" | sed "s/{year}/$(LANG=fr_FR date --date='last month' +'%Y')/g")"
    varValue="$(echo "${varValue}" | sed "s/{month}/$(LANG=fr_FR date --date='last month' +'%m')/g")"
    varValue=$(customReplacement ${varValue})
    echo ${varValue}
}

# replace var in var for current month
#   $1 the var
#   output : std : var
function replace_var_in_var_current_month() {
    varValue=$1
    varValue="$(echo "${varValue}" | sed "s/{str{month}}/$(LANG=fr_FR date +'%B')/g")"
    varValue="$(echo "${varValue}" | sed "s/{year}/$(LANG=fr_FR date +'%Y')/g")"
    varValue="$(echo "${varValue}" | sed "s/{month}/$(LANG=fr_FR date +'%m')/g")"
    varValue=$(customReplacement ${varValue})
    echo ${varValue}
}
