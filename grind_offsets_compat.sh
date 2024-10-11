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

OUTDIR="$2"
if [ -z "$2" ]; then
	OUTDIR=wndb_offsets/wndb_compat
fi
mkdir -p "${OUTDIR}"
echo "DIR:   "${OUTDIR}"" 1>&2;

jar=target/yaml2wndb-2.1.3-uber.jar
java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.GrindOffsets ${COMPAT_POINTER} ${COMPAT_LEXID} ${COMPAT_VERBFRAME} "${IN}" "${OUTDIR}"
