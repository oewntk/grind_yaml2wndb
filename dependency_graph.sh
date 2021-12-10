#!/bin/bash

graph=grind-yaml2wndb

mvn dependency:tree \
-DoutputType=dot \
-DoutputFile=${graph}.dot

dot -Tpng -o "${graph}.png" "${graph}.dot"
