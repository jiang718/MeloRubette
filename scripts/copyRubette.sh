#!/bin/bash -x
REPO_DIR=$(readlink -f $(dirname $0)/..)
SOURCE_JAR="melo-1.0.jar"
TARGET_JAR="melo.jar"
SOURCE_PATH=$REPO_DIR/target/$SOURCE_JAR
TARGET_PATH=$HOME/.rubato/plugins/$TARGET_JAR

echo "Copying..."
echo "Source:      $SOURCE_PATH"
echo "Destination: $TARGET_PATH"
cp $SOURCE_PATH $TARGET_PATH
