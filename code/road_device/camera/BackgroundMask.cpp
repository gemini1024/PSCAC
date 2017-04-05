// BackgroundMask.cpp
// recognize the background and create a frame that excludes this part
// For better detecting performance

#include "BackgroundMask.h"
#include "CamDef.h"

#include <opencv2/imgproc.hpp>
#include <iostream>


BackgroundMask::BackgroundMask()
: accumulateNumFrames(600), noiseRemovalNumFrames(12) {
}

BackgroundMask::~BackgroundMask() {
}



// Identify moving areas for a period of time.
// The area without motion is erased.
UMat BackgroundMask::createBackgroundMask(VideoCapture& vc) {
    std::cout << "Recognizing the background ... " << std::endl;
    recognizeBackgournd(vc);
    std::cout << "Recognition Complete! " << std::endl;

    std::cout << "Creating Mask ... " << std::endl;
    accumulateMasks(vc);
    // Closing operation ( to reduce noise )
    dilate(accumulatedMask, accumulatedMask, UMat());
    erode(accumulatedMask, accumulatedMask, UMat());
    std::cout << "Mask creation Complete!" << std::endl;

    return accumulatedMask;
}



// Recognize a moving object and wait until a mask using GMG method is created
void BackgroundMask::recognizeBackgournd(VideoCapture& vc) {
    UMat img;

    for(int n = 0; n < pMask->getNumFrames(); n++) {
        vc >> img;
        if (img.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            exit(0);
        }

        pMask->apply(img, bgMask);
        imshow( CamDef::originalVideo, img );  // show original image

        if( waitKey( CamDef::DELAY ) == CamDef::ESC ) {
            std::cout << "Closing the program ..." << std::endl;
            exit(0);
        }
    }

    img.release();
}



// Determine and accumulate areas with moving objects
void BackgroundMask::accumulateMasks(VideoCapture& vc) {
    assert(!bgMask.empty());

    UMat img;
    bgMask.copyTo(accumulatedMask);

    for(int n = 0; n <= accumulateNumFrames; n += noiseRemovalNumFrames) {

        UMat tmpMask(bgMask);

        for(int m=0; m<noiseRemovalNumFrames; m++) {
            vc >> img;
            if (img.empty())  {
                std::cerr << "ERROR : Unable to load frame" << std::endl;
                exit(0);
            }

            pMask->apply(img, bgMask);
            bitwise_and(bgMask, tmpMask, tmpMask);

            imshow( CamDef::originalVideo, img );  //  show original image
            imshow( CamDef::mask, accumulatedMask );  // show background mask

            if( waitKey( CamDef::DELAY ) == CamDef::ESC ) {
                std::cout << "Closing the program ..." << std::endl;
                exit(0);
            }
        }

        // Reduce noise
        erode(tmpMask, tmpMask, UMat());
        bitwise_or(tmpMask, accumulatedMask, accumulatedMask);
    }

    img.release();
}



// Check the current setting of BackgroundMask
void BackgroundMask::printProperties() {
    std::cout << "InitializationFrames : " << pMask->getNumFrames()
    << "\nLearningRate : " << pMask->getDefaultLearningRate()
    << "\nQuantizationLevels : " << pMask->getQuantizationLevels ()
    << "\nSmoothingRadius : " << pMask->getSmoothingRadius()
    << "\nUpdateBackgroundModel : " << pMask->getUpdateBackgroundModel()
    << std::endl;
}


// Set RecognizeNumFrames of BackgroundSubtractorGMG.
void BackgroundMask::setRecognizeNumFrames(int num) {
    pMask->setNumFrames(num);
}

// Specifies the number of frames to be used for noise removal
void BackgroundMask::setNoiseRemovalNumFrames(int num) {
    noiseRemovalNumFrames = num;
}

// Set the number of frames needed to create the mask.
void BackgroundMask::setAccumulateNumFrames(int num) {
    accumulateNumFrames = num;
}

// Set LearningRate of BackgroundSubtractorGMG.
void BackgroundMask::setLearningRate(double rate) {
    pMask->setDefaultLearningRate(rate);
}


// Only copy the foreground using the completed mask
void BackgroundMask::locateForeground(UMat& src, UMat& dst) {
    assert(!accumulatedMask.empty());
    dst.release();
    src.copyTo(dst, accumulatedMask);
}