// main.cpp
// road device's main function.
// Take the road and Transmit the dangerous situation.

#include <opencv2/opencv.hpp>
#include <iostream>
#include <cstdlib>
#include <unistd.h>
#include <sys/wait.h>


// Camera functions.
extern int takeRoad(void); // ( In camera/camera.cpp )
// Communication functions.
extern void regSignals(int deviceId); // ( In commuication/Sigdef.cpp )


using namespace std;

int main(int argc, char** argv)
{
    if ( argc < 2 ) {
        cout << "ERROR : Invalid Argument ! -> detect <device id> <video source>" << endl;
        exit(1);
    }

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
        default : // Send Warning Message until the camera is shut down
            regSignals( atoi(argv[1]) );
            while(waitpid(pid, NULL, 0) != pid);
            cout<<"Closing the communication process ..."<<endl;
            break;
    }

    return 0;
}
