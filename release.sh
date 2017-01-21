#!/bin/sh

REPO_DIR="$( cd "$( dirname "$0" )" && pwd )"

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

if [[ $(git status 2> /dev/null | tail -n1) != "nothing to commit, working tree clean" ]]; then
  echo "Working directory dirty. Please revert or commit."
  exit 1
fi

set -ex

$REPO_DIR/gradlew -p "$REPO_DIR" clean assemble -Dpre-dex=false

open "$REPO_DIR/duitsland-nieuws/build/outputs/apk"
