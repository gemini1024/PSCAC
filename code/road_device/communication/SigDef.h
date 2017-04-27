// SigDef.h
// Defines the signals and functions to use.

#ifndef SIGDEF_H
#define SIGDEF_H

#include <signal.h>


void sendSignalToParentProcess(int signo);
void regSignals(int deviceId);

namespace SigDef {
    enum {
        SIG_CAUTION = SIGUSR1,
        SIG_DANGER = SIGUSR2
    };
    static unsigned int deviceId;
    void signalHandler(int signo);
    void sendCaution(void);
    void sendDanger(void);
}


#endif