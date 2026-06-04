#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

# 1
IN="$1"
shift
echo "YAML:  ${IN}" 1>&2;

# -sense		SENSEID
# -synset		SYNSETID
# -offset		(n|v|a|r|s)		OFFSET

#./grind1.sh yaml --offset v00001740
#./grind1.sh yaml --sense "breathe%2:29:00::"
#./grind1.sh yaml --sense 00001740-v

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

java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.Grind1 "${IN}" "$*"
