#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
CLASSPATH="/out/production/FEUP_SDIS"
PEERCLASS="proj.peer.Peer"

NPEERS=${1:-3}
PEERPREFIX="PEER"

if ! [[ "$NPEERS" =~ ^[0-9]+$ ]]
    then
        echo "Number of peers must be an integer"
        exit -1
fi

for (( c=0; c<$NPEERS; c++ ))
do  
    x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$c &
    echo "Launched peer with id $PERR$c"
done
