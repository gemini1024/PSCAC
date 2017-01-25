// camera.cpp
// Control various functions using camera.
// Executed by child process

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <iostream>

#include "Detectors.h"
#include "SigDef.h"

using namespace cv; // openCV

int takeRoad(void)
{
    // Connect camera
    VideoCapture vc(0);
    vc.set(CV_CAP_PROP_FRAME_WIDTH, 640);
    vc.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
    if (!vc.isOpened()) {
        std::cerr << "ERROR : Cannot open the camera" << std::endl;
        return false;
    }


    UMat img; // using OpenCL
    PedestriansDetector pe_Detector;
    VehiclesDetector car_Detector;


    while (1) {
        vc >> img; // Put the captured image in img
        if (img.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            break;
        }

        // Detect pedestrians and vehicle
        pe_Detector.detect(img);
        if( pe_Detector.isFound() ) {
            sendSignalToParentProcess(SigDef::SIG_FOUND_HUMAN);
        }

        // TODO : Must be detected quickly
        car_Detector.detect(img);
        if( car_Detector.isFound() ) {
            sendSignalToParentProcess(SigDef::SIG_FOUND_CAR);
        }


        imshow("detect", img);  // show image

        if (waitKey(10) == 27) {  // ESC(27) -> break
            std::cout << "Closing the program ..." << std::endl;
            break;
        }
    }

    destroyAllWindows();

    return true;
}
