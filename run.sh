#!/usr/bin/env bash
shopt -s globstar
set -xe

javac -Xlint:unchecked ex/**/*.java
gcc -shared -fpic -o libAffinity.so -I/usr/lib/jvm/default/include -I/usr/lib/jvm/default/include/linux ex/affinity/Affinity.c
#sudo chrt -r 99 java -ea ex.Main
java -Djava.library.path=. -ea ex.Main
