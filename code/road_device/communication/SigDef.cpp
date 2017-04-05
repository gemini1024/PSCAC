// SigDef.cpp
// Send the situation shot by camera to each device.

#include "SigDef.h"
#include <iostream>
#include <unistd.h>
#include "ConnectServer.h"



// External calling functions
void sendSignalToParentProcess(int signo) {
    kill(getppid(), signo);
}

void regSignals(void) {
    struct sigaction act;

    // Do not process other signals before current processing is finished
    sigemptyset(&act.sa_mask);
    act.sa_handler = SigDef::signalHandler;

    if( sigaction(SigDef::SIG_FOUND_HUMAN, &act, NULL) < 0
        || sigaction(SigDef::SIG_FOUND_CAR, &act, NULL) < 0) {
        perror("sigaction");
        exit(1);
    }
}




// Internal calling functions
void SigDef::signalHandler(int signo) {
    switch(signo) {
        case SIG_FOUND_HUMAN :
            foundPedestrians();
            break;
        case SIG_FOUND_CAR :
            foundVehicles();
            break;
        default :
            break;
    }
}


void SigDef::foundPedestrians(void) {
    // TODO : send data to Server
    std::cout<< "Found  Pedestrians" << std::endl;

    // TODO : Apply our server address and port number and Need to define the communication method.
    // static ConnectServer connServ("127.0.0.1", 5001);
    // static ConnectServer connServ("211.253.29.38", 5001);
    // connServ.sendMessage("Found  Pedestrians");
}

void SigDef::foundVehicles(void) {
    // TODO : send data to Pedestrians
    std::cout<< "Found  Vehicles" << std::endl;
}

