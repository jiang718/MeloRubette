#!/bin/bash -x
RUBATO_FILE=$(readlink -f lib/rubato.jar)
LIB_DIR=$(readlink -f lib)
java -Djava.library.path=$LIB_DIR -jar $RUBATO_FILE
