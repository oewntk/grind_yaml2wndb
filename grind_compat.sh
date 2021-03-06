#!/bin/bash

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

java -ea -jar oewn-grind-yaml2wndb.jar ${COMPAT_POINTER} ${COMPAT_LEXID} ${COMPAT_VERBFRAME} "${IN}" "${IN2}" "${OUTDIR}"
