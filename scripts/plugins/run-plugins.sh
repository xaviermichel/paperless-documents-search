#!/bin/sh

for dir in *
do
	if [ ! -d $dir ]; then
		continue
	fi

	#
	# Below launchers, order by priority
	#
	
	# launch sh if exists, and continue
	if [ -e "$dir/run.sh" ]; then
		cd $dir
		./run.sh
		cd -
		continue
	fi

done
