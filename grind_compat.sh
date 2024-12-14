#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

COMPAT_POINTER="-compat:pointer"
COMPAT_LEXID="-compat:lexid"
COMPAT_LEXID=
COMPAT_VERBFRAME="-compat:verbframe"

IN="$1"
if [ -z "$1" ]; then
	IN=yaml
fi
echo "YAML:  ${IN}" 1>&2;

IN2="$2"
if [ -z "$2" ]; then
	IN2=yaml2
fi
echo "YAML2: ${IN2}" 1>&2;

OUTDIR="$3"
if [ -z "$3" ]; then
	OUTDIR=wndb_compat
fi
mkdir -p "${OUTDIR}"
echo "DIR:   "${OUTDIR}"" 1>&2;

jar=target/yaml2wndb-2.3.1-uber.jar
java -ea -jar "${jar}" ${COMPAT_POINTER} ${COMPAT_LEXID} ${COMPAT_VERBFRAME} "${IN}" "${IN2}" "${OUTDIR}"
