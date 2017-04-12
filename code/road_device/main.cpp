// main.cpp
// road device's main function.
// Take the road and Transmit the dangerous situation.

#include <opencv2/opencv.hpp>
#include <iostream>
#include <unistd.h>
#include <sys/wait.h>
#include "functions.h"

using namespace std;

int main(int argc, char** argv)
{
    cout<<"Press ESC to exit"<<endl;
    cout << "( Using OpenCV " << CV_MAJOR_VERSION << "." << CV_MINOR_VERSION << "." << CV_SUBMINOR_VERSION << " )" << endl;

    pid_t pid;
    switch( pid = fork() ) {
        case -1 :
            cerr << "ERROR : fork() failed" << endl;
            break;
        case 0 : // Detect pedestrians and vehicle on road
            takeRoad();
            cout << "Closing the camera process ..." << endl;
            break;
        default : // Alert
            // Send Warning Message until the camera is shut down
            regSignals();
            while(waitpid(pid, NULL, 0) != pid);
            cout<<"Closing the communication process ..."<<endl;
            break;
    }

    return 0;
}
