// camera.cpp
// Control various functions using camera.
// Executed by child process

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/bgsegm.hpp>
#include <iostream>

#include "Detectors.h"
#include "SigDef.h"

using namespace cv; // openCV

int takeRoad(void)
{
    // Connect camera
    // VideoCapture vc(0);
    // vc.set(CV_CAP_PROP_FRAME_WIDTH, 640);
    // vc.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

    VideoCapture vc("sample.avi"); // Load test video
    if (!vc.isOpened()) {
        std::cerr << "ERROR : Cannot open the camera" << std::endl;
        return false;
    }


    UMat img, fgimg; // using OpenCL
    UMat fgMask; // Mask excluding moving objects

    // Ptr<BackgroundSubtractor> pMask = createBackgroundSubtractorMOG2();
    // Ptr<BackgroundSubtractor> pMask = createBackgroundSubtractorKNN();
    // Ptr<BackgroundSubtractor> pMask = bgsegm::createBackgroundSubtractorMOG();
    Ptr<bgsegm::BackgroundSubtractorGMG> pMask = bgsegm::createBackgroundSubtractorGMG();

    // TODO : Remove output after adjusting properties
    std::cout << "LearningRate : " << pMask->getDefaultLearningRate()
    << "\nQuantizationLevels : " << pMask->getQuantizationLevels ()
    << "\nSmoothingRadius : " << pMask->getSmoothingRadius()
    << "\nUpdateBackgroundModel : " << pMask->getUpdateBackgroundModel()
    << std::endl;

    PedestriansDetector pe_Detector;
    VehiclesDetector car_Detector;


    int numFrame = 0, numinitializationFrame = pMask->getNumFrames();
    std::cout << "Recognize the background ... " << std::endl;

    while (1) {
        vc >> img; // Put the captured image in img
        if (img.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            break;
        }

        // After numinitializationFrame, the frame is display on the screen
        if( (++numFrame) == numinitializationFrame ) {
            std::cout << "Complete!" << std::endl;
        }
        pMask->apply(img, fgMask);
        fgimg.release();
        img.copyTo(fgimg, fgMask);

        // Detect pedestrians and vehicle
        pe_Detector.detect(fgimg);
        if( pe_Detector.isFound() ) {
            sendSignalToParentProcess(SigDef::SIG_FOUND_HUMAN);
        }

        // TODO : Must be detected quickly
        car_Detector.detect(fgimg);
        if( car_Detector.isFound() ) {
            sendSignalToParentProcess(SigDef::SIG_FOUND_CAR);
        }


        imshow("detect", fgimg);  // show image

        if (waitKey(10) == 27) {  // ESC(27) -> break
            std::cout << "Closing the program ..." << std::endl;
            break;
        }
    }

    vc.release();

    destroyAllWindows();

    return true;
}
