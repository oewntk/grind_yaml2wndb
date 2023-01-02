#/bin/bash

# P A R A M S

dbtag=$1
shift
if [ -z "${dbtag}" ]; then
  dbtag=2022
fi

# C O L O R S

R='\u001b[31m'
G='\u001b[32m'
B='\u001b[34m'
Y='\u001b[33m'
M='\u001b[35m'
C='\u001b[36m'
Z='\u001b[0m'

# D I R S

THISDIR=$(dirname $(readlink -m "$0"))

DISTDIR=${THISDIR}/dist
mkdir -p ${DISTDIR}
DATADIR=${THISDIR}
echo "pack to ${DISTDIR} from ${DATADIR}"

# A R C H I V E S

TAR_ARCHIVE=${DISTDIR}/oewn_${dbtag}.dict.tar.gz
ZIP_ARCHIVE=${DISTDIR}/oewn_${dbtag}.zip
TAR_ARCHIVE_MIN=${DISTDIR}/oewn_${dbtag}_bare.dict.tar.gz
ZIP_ARCHIVE_MIN=${DISTDIR}/oewn_${dbtag}_bare.zip
TAR_ARCHIVE_COMPAT=${DISTDIR}/oewn_${dbtag}_compat.dict.tar.gz
ZIP_ARCHIVE_COMPAT=${DISTDIR}/oewn_${dbtag}_compat.zip

# M A I N

# prepare dict container

if [ ! -e "${DATADIR}/wndb" -a ! -d "${DATADIR}/wndb" ]; then
  echo -e "${R}Non existent wndb dir${Z}"
  exit 1
fi
pushd ${DATADIR} >/dev/null
ln -sfT wndb/ dict
popd >/dev/null

# full

echo -e "${M}pack to $(basename ${TAR_ARCHIVE})${Z}"
rm -f ${TAR_ARCHIVE}
tar czfh ${TAR_ARCHIVE} -C ${DATADIR} dict -C ${DATADIR} OEWN_LICENSE.md
echo -e "${C}"
tar tvf ${TAR_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${TAR_ARCHIVE}${Z}"
echo

echo -e "${M}pack to $(basename ${ZIP_ARCHIVE})${Z}"
rm -f ${ZIP_ARCHIVE}
zip -j ${ZIP_ARCHIVE} ${DATADIR}/dict/*
zip ${ZIP_ARCHIVE} OEWN_LICENSE.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE}${Z}"
echo

# bare

echo -e "${M}pack to $(basename ${TAR_ARCHIVE_MIN})${Z}"
rm -f ${TAR_ARCHIVE_MIN}
tar czhf ${TAR_ARCHIVE_MIN} -C ${DATADIR} --exclude --exclude dict/lexnames --exclude dict/sensemap.txt --exclude dict/cntlist --exclude dict/cntlist.rev dict -C ${DATADIR} OEWN_LICENSE_short.md
echo -e "${C}"
tar tvf ${TAR_ARCHIVE_MIN}
echo -en "${Z}"
echo -e "${G}${TAR_ARCHIVE_MIN}${Z}"
echo

echo -e "${M}pack to $(basename ${ZIP_ARCHIVE_MIN})${Z}"
rm -f ${ZIP_ARCHIVE_MIN}
zip -j ${ZIP_ARCHIVE_MIN} ${DATADIR}/dict/* -x "*lexnames" -x "*sensemap.txt" -x "*cntlist" -x "*cntlist.rev"
zip ${ZIP_ARCHIVE_MIN} OEWN_LICENSE_short.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE_MIN}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE_MIN}${Z}"
echo

# compat

if [ ! -e "${DATADIR}/wndb_compat" -a ! -d "${DATADIR}/wndb_compat" ]; then
  echo -e "${R}Non existent wndb_compat dir${Z}"
  exit 1
fi
pushd ${DATADIR} >/dev/null
ln -sfT wndb_compat/ dict
popd >/dev/null

echo -e "${M}pack to $(basename ${TAR_ARCHIVE_COMPAT})${Z}"
rm -f ${TAR_ARCHIVE_COMPAT}
tar czfh ${TAR_ARCHIVE_COMPAT} -C ${DATADIR} dict -C ${DATADIR} OEWN_LICENSE.md
echo -e "${C}"
tar tvf ${TAR_ARCHIVE_COMPAT}
echo -en "${Z}"
echo -e "${G}${TAR_ARCHIVE_COMPAT}${Z}"
echo

echo -e "${M}pack to $(basename ${ZIP_ARCHIVE_COMPAT})${Z}"
rm -f ${ZIP_ARCHIVE_COMPAT}
zip -j ${ZIP_ARCHIVE_COMPAT} ${DATADIR}/dict/*
zip ${ZIP_ARCHIVE_COMPAT} OEWN_LICENSE.md
echo -e "${C}"
unzip -l ${ZIP_ARCHIVE_COMPAT}
echo -en "${Z}"
echo -e "${G}${ZIP_ARCHIVE_COMPAT}${Z}"
echo

rm ${DATADIR}/dict
