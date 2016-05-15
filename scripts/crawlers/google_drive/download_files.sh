#!/bin/bash

DRIVE_DIR=/home/xavier/paperless-documents-search/google_drive/work
DRIVE_EDM_DIRECTORY=scan_to_paperless-documents-search
# the desination have to exists ! This script won't create it for you
EDM_DESTINATION="/media/documents/00-a_trier"

function box(){
        t="$1xxxx";c=${2:-=}; echo ${t//?/$c}; echo "$c $1 $c"; echo ${t//?/$c};
}

export GOPATH=$HOME/gopath
export PATH=$GOPATH:$GOPATH/bin:$PATH

mkdir -p "${DRIVE_DIR}"
cd "${DRIVE_DIR}"

if [ ! -d ".gd" ] ; then
        echo "You have to init drive repo, to do that :"
        echo "$ cd ${DRIVE_DIR}"
        echo '$ export GOPATH=$HOME/gopath && export PATH=$GOPATH:$GOPATH/bin:$PATH && drive init'
        echo "To do that, you have to install drive (https://github.com/odeke-em/drive)"
        exit 1
fi

# pull changes
drive pull -verbose=true --no-prompt --matches "${DRIVE_EDM_DIRECTORY}"
rm -fv "${DRIVE_EDM_DIRECTORY}/*.desktop"

# trash downloaded files on google docs
for fich in "${DRIVE_EDM_DIRECTORY}"/*
do
        if [ "$fich" == "${DRIVE_EDM_DIRECTORY}/*" ]; then
                continue
        fi

        cp -v "$fich" "${EDM_DESTINATION}"

        drive trash "${fich}"
        rm -fv "${fich}"
done


box "Liste des fichiers a ranger"
ls -R "${EDM_DESTINATION}"
