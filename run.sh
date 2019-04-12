#!/usr/bin/env bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
CLASSPATH="/out"
MAINCLASS="proj.client.ClientMain"

COMPILERPATH="/src"
CCLASSPATH="/src/proj/client/ClientMain.java"


javac -cp $SCRIPTPATH$COMPILERPATH -d $SCRIPTPATH$CLASSPATH $SCRIPTPATH$CCLASSPATH
java -cp $SCRIPTPATH$CLASSPATH $MAINCLASS $@
