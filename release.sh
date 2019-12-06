#!/bin/sh

set -o pipefail

REPO_DIR="$( cd "$( dirname "$0" )/../" && pwd )"

if [[ $(git status 2> /dev/null | tail -n1) != "nothing to commit, working tree clean" ]]; then
  echo "Working directory dirty. Please revert or commit."
  exit 1
fi

echo "Keystore password: "
read -s KEYSTORE_PASS
if [ -z "${KEYSTORE_PASS}" ]; then
	echo "No password given. Abort"
	exit 1
fi
export KSTOREPWD=$KEYSTORE_PASS

echo "Key (alias) password [return when same]: "
read -s KEYPASS
if [ -z "${KEYPASS}" ]; then
	KEYPASS=$KEYSTORE_PASS
fi
export KEYPWD=$KEYPASS

echo "Building bundle 'b' or apk 'a' [return when b]:"
read -s INPUT
if [[ -z "${INPUT}" ]]  ||
   [[ "${INPUT}" == "b" ]] ||
   [[ "${INPUT}" == "B" ]]; then
  APP_TYPE="Bundle"
  GRADLE_COMMAND=bundle
  ARTIFACT="bundle/${FLAVOR}${BUILD_TYPE}"
elif [[ "${INPUT}" == "a" ]] ||
     [[ "${INPUT}" == "A" ]]; then
  APP_TYPE="APK"
  GRADLE_COMMAND=assemble
  ARTIFACT="apk/${FLAVOR}/${BUILD_TYPE}/"
else
  echo "Invalid input. Abort"
  exit 0
fi

echo "Creating ${APP_TYPE} for ${FLAVOR}${BUILD_TYPE}"

${REPO_DIR}/gradlew -p "$REPO_DIR" clean ${GRADLE_COMMAND} -Dpre-dex=false

open "$REPO_DIR/duitsland-nieuws/build/outputs/${ARTIFACT}/"
