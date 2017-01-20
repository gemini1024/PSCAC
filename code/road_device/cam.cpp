// cam.cpp
// Control various functions using camera.

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <iostream>

#include "detectors.h"

using namespace cv; // openCV
using namespace std;

int takeRoad(void)
{
    // TODO : Connect camera when executing on odroid
    VideoCapture vc("sample.avi"); // Load test video
    if (!vc.isOpened()) {
        cerr << "ERROR : Cannot open the camera" << endl;
        return false;
    }



    Mat img; // frame
    PedestriansDetector pe_Detector;
    VehiclesDetector car_Detector;


    while (1) {

        vc >> img; // Put the captured image in img
        if (img.empty())  {
            cerr << "ERROR : load frame" << endl;
            break;
        }



        // Detect pedestrians and vehicle
        // TODO : Must be detected quickly
        pe_Detector.detect(img);
        car_Detector.detect(img);

        // TODO : Send signals corresponding to each situation



        imshow("detect", img);  // show image

        if (waitKey(10) == 27) break; // ESC(27) -> break

    }

    destroyAllWindows();

    return true;
}
