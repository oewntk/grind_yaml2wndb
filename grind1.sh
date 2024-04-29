#!/bin/bash

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

jar=target/yaml2wndb-1.0.5-uber.jar
java -ea -cp "${jar}" org.oewntk.grind.yaml2wndb.Grind1 "${IN}" "${IN2}" $3 $4 $5
