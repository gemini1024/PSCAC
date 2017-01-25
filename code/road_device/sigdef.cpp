// sigdef.cpp
// Send the situation shot by camera to each device.

#include "sigdef.h"
#include <iostream>
#include <unistd.h>



// External calling functions
void sendSignalToParentProcess(int signo) {
    kill(getppid(), signo);
}

void regSignals(void) {
    struct sigaction act;

    // Do not process other signals before current processing is finished
    sigfillset(&act.sa_mask);
    act.sa_handler = sigdef::signalHandler;

    if( sigaction(sigdef::SIG_FOUND_HUMAN, &act, NULL) < 0
        || sigaction(sigdef::SIG_FOUND_CAR, &act, NULL) < 0) {
        perror("sigaction");
        exit(1);
    }
}




// Internal calling functions
void sigdef::signalHandler(int signo) {
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


void sigdef::foundPedestrians(void) {
    // TODO : send data to Server
    std::cout<< "Found  Pedestrians" << std::endl;
}

void sigdef::foundVehicles(void) {
    // TODO : send data to Pedestrians
    std::cout<< "Found  Vehicles" << std::endl;
}

