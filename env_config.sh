#!/bin/bash
LIB_DIR=$(readlink -f $(dirname $0)/lib)
echo "Installing Rubato..."
mvn install:install-file -Dfile=$LIB_DIR/rubato.jar -DgroupId=org.rubato -DartifactId=rubato -Dversion=1 -Dpackaging=jar
