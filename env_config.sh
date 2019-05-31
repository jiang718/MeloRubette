#!/bin/bash
RUBATO_FILE=$(readlink -f lib/rubato.jar)
echo $RUBATO_FILE
echo "Installing Rubato..."
mvn install:install-file -Dfile=$RUBATO_FILE -DgroupId=org.rubato -DartifactId=rubato -Dversion=1 -Dpackaging=jar
