// main.cpp
// road device's main function.
// Take the road and Transmit the dangerous situation.

#include <opencv2/opencv.hpp>
#include <iostream>
#include <cstdlib>
#include <unistd.h>
#include <sys/wait.h>


// Camera functions.
extern int takeRoad(std::string videoSource); // ( In camera/camera.cpp )
// Communication functions.
extern void regSignals(int deviceId); // ( In commuication/Sigdef.cpp )


using namespace std;



// After confirming the argument,
// the parent process allocates work to communicate and the child process to perform image processing.
// A good command : detect <device id> <video source>
// argv[1] - device id : The ID of the device sending to the server
// argv[2] - video source : The location of the video that will serve as a basis for judging the situation.
// If you do not input or enter "CAMERA", the image is taken from the connected camera.
int main(int argc, char** argv)
{
    // Check for valid command
    if ( argc < 2 || argc > 3 ) {
        cout << "ERROR : Invalid Argument ! -> detect <device id> <video source>" << endl;
        exit(1);
    }

    // Start operation of the system
    cout<<"Press ESC to exit"<<endl;
    cout << "( Using OpenCV " << CV_MAJOR_VERSION << "." << CV_MINOR_VERSION << "." << CV_SUBMINOR_VERSION << " )" << endl;

    // Work distribution
    pid_t pid;
    switch( pid = fork() ) {
        case -1 :
            cerr << "ERROR : fork() failed" << endl;
            break;
        case 0 : // Detect pedestrians and vehicle on road
            if ( argc == 2 ) takeRoad( "CAMERA" );
            else takeRoad( argv[2] );
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
