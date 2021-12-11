#!/bin/bash

graph=grind-yaml2wndb
todir=images/

mvn dependency:tree \
-DoutputType=dot \
-DoutputFile=${todir}${graph}.dot

dot -Tpng -o "${todir}${graph}.png" "${todir}${graph}.dot"
