// camera.cpp
// Control various functions using camera.
// Executed by child process

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/bgsegm.hpp>
#include <iostream>
#include <thread>

#include "BackgroundMask.h"
#include "Detectors.h"
#include "CamDef.h"

#include "../communication/SigDef.h"

using namespace cv; // openCV


void detectObjects(UMat& fgimg, Detector& detector, int signo) {
    detector.detect(fgimg);
    if( detector.isFound() ) {
        sendSignalToParentProcess(signo);

        std::string objtype;
        if ( signo == SigDef::SIG_FOUND_HUMAN ) {
            objtype = "Human";
        } else {
            objtype = "Car";
        }

        const std::vector<Rect>& foundObj = detector.getFoundObjects();
        for( auto const& r : foundObj ) {
            std::cout << objtype << " : tl = (" << r.tl().x << "," << r.tl().y << ") , br = ("
            << r.br().x << "," << r.br().y << "), md = ("
            << ( r.br().x - r.tl().x )/2 + r.tl().x << "," << ( r.br().y - r.tl().y )/2 + r.tl().y << ")" << std::endl;
        }
    }
}


int takeRoad(void)
{
    // Connect camera
    // VideoCapture vc(0);
    // vc.set(CV_CAP_PROP_FRAME_WIDTH, 640);
    // vc.set(CV_CAP_PROP_FRAME_HEIGHT, 480);
    // vc.set(CV_PROP_FPS, 20);

    VideoCapture vc( CamDef::sampleVideo ); // Load test video
    if (!vc.isOpened()) {
        std::cerr << "ERROR : Cannot open the camera" << std::endl;
        return false;
    }


    // Background recognition and removal
    BackgroundMask bgMask;
    bgMask.setRecognizeNumFrames(24);  // Default : 120 ( BackgroundSubtractorGMG's default value )
    bgMask.setAccumulateNumFrames(800); // Default : 200
    bgMask.setLearningRate(0.025); // Default : 0.025
    bgMask.printProperties();
    UMat mask = bgMask.createBackgroundMask(vc);


    UMat img, fgimg; // using OpenCL ( UMat )
    PedestriansDetector pe_Detector;
    VehiclesDetector car_Detector;


    std::cout << "Start Detection ..." << std::endl;
    while (1) {
        vc >> img; // Put the captured image in img
        if (img.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            break;
        }

        bgMask.locateForeground(img, fgimg);

        // Detect pedestrians and vehicle
        std::thread t1(detectObjects, std::ref(fgimg), std::ref(pe_Detector), SigDef::SIG_FOUND_HUMAN);
        std::thread t2(detectObjects, std::ref(fgimg), std::ref(car_Detector), SigDef::SIG_FOUND_CAR);
        t1.join();
        t2.join();


        imshow( CamDef::originalVideo, img );  // show original image
        imshow( CamDef::mask, mask );  // show background mask
        imshow( CamDef::resultVideo, fgimg );  // show image

        if( waitKey( CamDef::DELAY ) == CamDef::ESC ) {  // ESC(27) -> break
            std::cout << "Closing the program ..." << std::endl;
            break;
        }
    }

    vc.release();

    destroyAllWindows();

    return true;
}
