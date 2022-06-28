#!/bin/bash

# C O L O R S

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

# G I T

gitsource="https://github.com/globalwordnet/english-wordnet.git"
branch=master

# D I R S

THISDIR=`dirname $(readlink -m "$0")`

# in
GITDIR=yaml

# out
TARGETDIRS="wndb wndb_compat"
TAG=build

# F U N C T I O N S

function tag()
{
	echo "UPSTREAM TAG: "
	#git rev-parse HEAD
	git log -n 1 --invert-grep --committer='1313ou'
}

# M A I N

if false; then 																	# S T A R T _ C O N D I T I O N A L
echo "not executed"
fi																				# E N D _ C O N D I T I O N A L

pushd "${GITDIR}" > /dev/null

# update
git pull
git checkout "${branch}"
git status

# tag
tag=`tag`
echo -e "${Y}'''"
echo -e "${tag}"
echo -e "'''${Z}"

popd > /dev/null

echo "${tag}" >> ${TAG}

# copy to source
read -p "Are you sure you want to copy build tag to src? " -n 1 -r
echo    # (optional) move to a new line
echo -e "${Z}"
if [[ $REPLY =~ ^[Yy]$ ]]; then
	for d in ${TARGETDIRS}; do
		cp -p ${TAG} "${d}"
	done
fi

echo "done"

