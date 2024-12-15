#!/bin/bash

set -e

if [ -z "$1" ]; then
  >&2 echo "No target dir"
  exit 1
fi

dir="$1"
if [ ! -e "${dir}" ]; then
  >&2 echo "Target dir does not exist"
  exit 2
fi
if [ ! -d "${dir}" ]; then
  >&2 echo "Target dir does not exist"
  exit 3
fi

target=offsets

pushd "${dir}" > /dev/null
if [ ! -e "${target}.map" ]; then
  >&2 echo "No target ${target}.map in ${dir}"
  exit 4
else
  sort -k 2n "${target}.map" > "${target}_sorted_by_offset.map"
  sort -k 1.10,1.10 -k 2n "${target}.map" > "${target}_sorted_by_pos_offset.map"
fi
popd > /dev/null
