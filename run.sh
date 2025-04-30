#!/usr/bin/env bash
shopt -s globstar
set -xe

javac -Xlint:unchecked ex/**/*.java
#sudo chrt -r 99 java -ea ex.Main
java -ea ex.Main
