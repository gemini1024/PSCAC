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
    		cerr << "fork() failed" << endl;
    		break;
    	case 0 : // Detect pedestrians and vehicle on road
    		takeRoad();
    		break;
    	default : // Alert
    		// TODO : Send Warning Message
    		waitpid(pid, NULL, 0);
    		break;
    }

    return 0;
}
