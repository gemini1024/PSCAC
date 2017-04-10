// SigDef.h
// Defines the signals and functions to use.

#ifndef SIGDEF_H
#define SIGDEF_H

#include <signal.h>


void sendSignalToParentProcess(int signo);
void regSignals(void);

namespace SigDef {
    enum {
        SIG_WARNING = SIGUSR1,
        SIG_STOP = SIGUSR2
    };
    void signalHandler(int signo);
    void sendWarning(void);
    void sendStop(void);
}


#endif