// SigDef.h
// Defines the signals and functions to use.

#ifndef SIGDEF_H
#define SIGDEF_H

#include <signal.h>


void sendSignalToParentProcess(int signo);
void regSignals(void);

namespace SigDef {
    enum {
        SIG_FOUND_HUMAN = SIGUSR1,
        SIG_FOUND_CAR = SIGUSR2
    };
    void signalHandler(int signo);
    void foundPedestrians(void);
    void foundVehicles(void);
}


#endif