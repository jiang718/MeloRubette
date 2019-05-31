#!/bin/bash -x
LIB_DIR=$(readlink -f $(dirname $0)/lib)
java -Djava.library.path=$LIB_DIR -jar $LIB_DIR/rubato.jar
