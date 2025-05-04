#!/usr/bin/env bash
shopt -s globstar
set -xe

JVM_PATH=/usr/lib/jvm/default

$JVM_PATH/bin/javac -Xlint:unchecked ex/**/*.java
gcc -shared -fpic -o libAffinity.so -I$JVM_PATH/include -I$JVM_PATH/include/linux ex/affinity/Affinity.c
#sudo chrt -r 99 java -Djava.library.path=. -ea ex.Main
$JVM_PATH/bin/java -Djava.library.path=. -ea ex.Main
