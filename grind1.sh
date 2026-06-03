#!/bin/bash

#
# Copyright (c) 2024. Bernard Bou.
#

set -e

# 1
IN="$1"
echo "YAML:  ${IN}" 1>&2;

IN2="$2"
echo "YAML2: ${IN2}" 1>&2;

# 2				3				4
# SYNSETID
# -sense		SENSEID
# -offset		(n|v|a|r|s)		OFFSET

#./grind1.sh yaml yaml2 -offset v 1740
#./grind1.sh yaml yaml2 -sense "breathe%2:29:00::"
#./grind1.sh yaml yaml2 1740-v

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
java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.Grind1 "${IN}" "${IN2}" $3 $4 $5
