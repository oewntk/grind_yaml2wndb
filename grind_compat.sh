#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

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

jar=yaml2wndb-3.0.1-uber.jar
if [ ! -e "${jar}" ]; then
  if [ ! -e "target/${jar}" ]; then
    echo "Non existing uber jar" >&2
    exit 1
    fi
  ln -s "target/${jar}"
  fi
if [ ! -e "${jar}" ]; then
  echo "Non existing uber jar" >&2
  exit 2
  fi
java -ea -jar "${jar}" ${COMPAT_POINTER} ${COMPAT_LEXID} ${COMPAT_VERBFRAME} "${IN}" "${IN2}" "${OUTDIR}"
