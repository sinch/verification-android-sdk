#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$( cd "${SCRIPT_DIR}/../" && pwd )"

cd $ROOT_DIR
TEMP_DIR=$(mktemp -d)
trap 'rm -rf "$TEMP_DIR"' EXIT

# Since Gitlab does not allow multiline values for masked properties, we store the key as base64 encoded string in
# the CI environment variable
cat "$MAVEN_SIGNING_KEY_BASE64" | base64 -d > $TEMP_DIR/key_decoded.asc
./gradlew publishAggregationToCentralPortal -PsigningVerificationFile="$TEMP_DIR/key_decoded.asc"
