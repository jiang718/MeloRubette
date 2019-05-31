#!/bin/bash -x
RUBATO_DIR=$(readlink -f $(dirname $0)/../..)
java -Djava.library.path=$RUBATO_DIR -jar $RUBATO_DIR/rubato.jar
