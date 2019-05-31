#!/bin/bash -x
TARGET_JAR="melo.jar"
TARGET_PATH=$HOME/.rubato/plugins/$TARGET_JAR

echo "Cleaning..."
echo "TARGET:      $TARGET_PATH"
rm -f $TARGET_PATH
