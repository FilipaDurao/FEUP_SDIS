#!/bin/bash


SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
CLASSPATH="/out"
PEERCLASS="proj.peer.Peer"

COMPILERPATH="/src"
CCLASSPATH="/src/proj/peer/Peer.java"

PEERPREFIX="peer-"

MCNAME="228.25.25.25"
MCPORT="8823"

MDBNAME="228.25.25.25"
MDBPORT="8824"

MDRNAME="228.25.25.25"
MDRPORT="8825"

INITIAL_INDEX=1

INPLACE=false
TILE=false
KILL_PEERS=false
RESET=false

VERSION="1.5"


function startConsole() {
    let STARTEDP=$index-$INITIAL_INDEX+1
    if [[ ${STARTEDP} -gt $NPEERS ]]; then
        echo "Ending launch..."
        exit 0
    fi

    x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $VERSION $index $PEERPREFIX$index $MCNAME $MCPORT $MDBNAME $MDBPORT $MDRNAME $MDRPORT &
    echo "Launched peer with id $PEERPREFIX$index"
    let index++
    sleep .2
}

function startInPlace() {
    let STARTEDP=$index-$INITIAL_INDEX+1
    if [[ ${STARTEDP} -gt $NPEERS ]]; then
        echo "Ending launch..."
        exit 0
    fi

    java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $VERSION $index $PEERPREFIX$index $MCNAME $MCPORT $MDBNAME $MDBPORT $MDRNAME $MDRPORT &
    echo "Launched peer with id $PEERPREFIX$index"
    let index++
    sleep .2
}

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -i|--in-place)
    INPLACE=true
    shift # past argument
    ;;
    -k|--kill)
    KILL_PEERS=true
    shift # past argument
    ;;
    -r|--reset)
    RESET=true
    shift # past argument
    ;;
    -c|--columns)
    TILE=true
    COLUMNS="$2"
    shift # past argument
    shift # past value
    ;;
    -s|--start-index)
    INITIAL_INDEX=$2
    shift # past argument
    shift # past value
    ;;
    *)    # unknown option
    POSITIONAL+=("$1")
    shift # past argument
    ;;
esac
done
set -- "${POSITIONAL[@]}" # restore positional parameters

NPEERS=${1:-3}
index=$INITIAL_INDEX



if [[ "$KILL_PEERS" == true ]]; then
    ./killAllPeers.sh
    sleep 0.5
fi

if [[ "$RESET" == true ]]; then
    rm -rf $SCRIPTPATH"/peer_"*
fi

if ! [[ "$NPEERS" =~ ^[0-9]+$ ]]
    then
        echo "Number of peers must be an integer"
        exit -1
fi

javac -cp $SCRIPTPATH$COMPILERPATH -d $SCRIPTPATH$CLASSPATH $SCRIPTPATH$CCLASSPATH 
if [[ $? -ne 0 ]]; then
    exit -1
fi

if [[ "$TILE" = true && "$INPLACE" == false ]] ; then

    let "NROW=($NPEERS+1)/($COLUMNS)-1"
    let "NLAST=($NPEERS+1)%($COLUMNS)"
    
    echo NPEERS = "$NPEERS"
    echo NROW = "$NROW"
    echo NLAST = "$NLAST"

    i3-msg split h
    for (( c=0; c<$COLUMNS-1; c++ ))
    do
        startConsole
    done
    
    for (( i=0; i<$COLUMNS; i++ ))
    do
        i3-msg split v
        for (( c=0; c<$NROW; c++ ))
        do
            startConsole
        done

        if [[ $i < $NLAST ]]; then
            startConsole
        fi

        if [[ $i -lt $COLUMNS-1 ]]; then
            i3-msg focus left
        fi
    done


else
    for (( c=0; c<$NPEERS; c++ ))
    do  
        if [[ "$INPLACE" = true ]] ; then
            startInPlace
        else
            startConsole
        fi

    done
fi

