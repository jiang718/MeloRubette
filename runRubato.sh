#!/bin/bash -x
RUBATO_FILE=$(readlink -f lib/rubato.jar)
# using rubato.jar at the upper level in case third party developers put MeloRubette repo under the installation location of latest rubato 
if [[ -f ../rubato.jar ]]; then
    RUBATO_FILE=$(readlink -f ../rubato.jar)
fi
echo "Using Rubato Jar at: $RUBATO_FILE"
LIB_DIR=$(readlink -f lib)
java -Djava.library.path=$LIB_DIR -jar $RUBATO_FILE
