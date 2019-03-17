#!/bin/bash

INPLACE=false
TILE=false

POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -i|--in-place)
    INPLACE=true
    shift # past argument
    ;;
    -c|--columns)
    TILE=true
    COLUMNS="$2"
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

if [[ "$TILE" = true && "$INPLACE" == false ]] ; then

    index=0
    let "NROW=($NPEERS+1)/($COLUMNS)-1"
    let "NLAST=($NPEERS+1)%($COLUMNS)"
    
    echo NPEERS = "$NPEERS"
    echo NROW = "$NROW"
    echo NLAST = "$NLAST"

    i3-msg split h
    for (( c=0; c<$COLUMNS-1; c++ ))
    do
        if [[ $index -gt $NPEERS ]]; then 
            echo "Ending launch..."
            exit 0
        fi

        x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$index &
        echo "Launched peer with id $PEER$index"
        let index++
        sleep .2
    done
    
    for (( i=0; i<$COLUMNS; i++ ))
    do
        i3-msg split v
        for (( c=0; c<$NROW; c++ ))
        do
            if [[ $index -gt $NPEERS ]]; then 
                echo "Ending launch..."
                exit 0
            fi

            x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$index &
            echo "Launched peer with id $PEER$index"
            let index++
            sleep .2
        done

        if [[ $i < $NLAST ]]; then
            if [[ $index -gt $NPEERS ]]; then 
                echo "Ending launch..."
                exit 0
            fi

            x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$index &
            echo "Launched peer with id $PEER$index"
            let index++
            sleep .2
        fi

        if [[ $i -lt $COLUMNS-1 ]]; then
            i3-msg focus left
        fi
    done


else
    for (( c=0; c<$NPEERS; c++ ))
    do  
        if [ "$INPLACE" = true ] ; then
            java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$c &
        else
            x-terminal-emulator -e java -cp $SCRIPTPATH$CLASSPATH $PEERCLASS $PEER$c &
        fi

        echo "Launched peer with id $PEER$c"
    done
fi

