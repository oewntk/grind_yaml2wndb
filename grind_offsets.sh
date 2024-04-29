#!/bin/bash

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

jar=target/yaml2wndb-1.0.5-uber.jar
java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.GrindOffsets "${IN}" "${OUTDIR}"
