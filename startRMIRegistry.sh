#!/bin/bash

WORK_DIR=$(pwd)

cd $WORK_DIR/out/production/FEUP_SDIS
rmiregistry &
cd $WORK_DIR