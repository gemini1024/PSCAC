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
    sigfillset(&act.sa_mask);
    act.sa_handler = SigDef::signalHandler;
    act.sa_flags = 0;

    if( sigaction(SigDef::SIG_WARNING, &act, NULL) < 0
        || sigaction(SigDef::SIG_STOP, &act, NULL) < 0) {
        perror("sigaction");
        exit(1);
    }
}




// Internal calling functions
void SigDef::signalHandler(int signo) {
    switch(signo) {
        case SIG_WARNING :
            sendWarning();
            break;
        case SIG_STOP :
            sendStop();
            break;
        default :
            break;
    }
}


// Send the current situation to the server
void SigDef::sendWarning(void) {
    static ConnectServer connServ("211.253.29.38", 5001);
    connServ.sendMessage("2,caution");
}

void SigDef::sendStop(void) {
    static ConnectServer connServ("211.253.29.38", 5001);
    connServ.sendMessage("2,dangerous");
}

