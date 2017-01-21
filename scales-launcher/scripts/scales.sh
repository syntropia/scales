#!/usr/bin/env bash

################################################
# Verticle name-class map
################################################

verticleClassFromName() {
    case $1 in
        eventstore) return "studio.lysid.scales.eventStore.EventStoreVerticle";;
        query) return "studio.lysid.scales.query.QueryVerticle";;
        command) return "studio.lysid.scales.command.CommandVerticle";;
        facade) return "studio.lysid.scales.facade.FacadeVerticle";;
        *) return "";
    esac
}

eventstore="studio.lysid.scales.eventStore.EventStoreVerticle"
query="studio.lysid.scales.query.QueryVerticle"
command="studio.lysid.scales.command.CommandVerticle"
facade="studio.lysid.scales.facade.FacadeVerticle"


# TODO Pass remaining command-line variables to the java -jar calls

#############################################################################
# Start / Stop a given verticle (all if none specified)
#############################################################################
# $1: start or stop
# $2: name of the verticle ( eventstore | query | command | facade )
#############################################################################

startOrStopVerticle() {
    if [ -z $1 ] || [ -z ${!2+x} ]; then
        echo "The module $2 does not exist. Can be one of: eventstore, query, command or facade."
        exit 1
    else
        java -jar ${SCRIPT_DIR}/scales-launcher.jar ${1} ${!2} -Dvertx-id=scales
    fi
}

startStopVerticleByName() {
    if [ -z $2 ]; then
        if [ "$1" = start ]; then
            echo "Starting all modules..."
        else
            echo "Stopping all modules..."
        fi
        startOrStopVerticle $1 eventstore
        startOrStopVerticle $1 query
        startOrStopVerticle $1 command
        startOrStopVerticle $1 facade
    else
        startOrStopVerticle $1 $2
    fi
}



################################################
# Main
################################################

SCRIPT_DIR=`dirname "${BASH_SOURCE[0]}"`

case $1 in
    start)
        startStopVerticleByName start $2
        exit 0
        ;;
    stop)
        startStopVerticleByName stop $2
        exit 0
        ;;
    *)
        echo "First param must be either start or stop";
        exit 1
esac