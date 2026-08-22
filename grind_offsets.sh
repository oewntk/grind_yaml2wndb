#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

IN="$1"
if [ -z "$1" ]; then
	IN=yaml
fi
echo "YAML:  ${IN}" 1>&2;

OUTDIR="$2"
if [ -z "$2" ]; then
	OUTDIR=wndb_offsets/wndb
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
java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.GrindOffsets "${IN}" "${OUTDIR}"
