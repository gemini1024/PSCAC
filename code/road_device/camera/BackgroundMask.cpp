// BackgroundMask.cpp
// recognize the background and create a frame that excludes this part
// For better detecting performance

#include "BackgroundMask.h"
#include "CamDef.h"

#include <opencv2/imgproc.hpp>
#include <iostream>


BackgroundMask::BackgroundMask() : accumulateNumFrames(200) {
}

BackgroundMask::~BackgroundMask() {
}


UMat BackgroundMask::createBackgroundMask(VideoCapture& vc) {
    std::cout << "Recognizing the background ... " << std::endl;
    recognizeBackgournd(vc);
    std::cout << "Recognition Complete! " << std::endl;

    std::cout << "Creating Mask ... " << std::endl;
    accumulateMasks(vc);
    std::cout << "Mask creation Complete!" << std::endl;

    std::cout << "Applying opening operation to Mask ... " << std::endl;
    dilate(accumulatedMask, accumulatedMask, UMat());
    erode(accumulatedMask, accumulatedMask, UMat());
    std::cout << "Opening Complete!" << std::endl;

    imshow( CamDef::mask, accumulatedMask );  // show background mask

    return accumulatedMask;
}

// Recognize a moving object and wait until a mask using GMG method is created
void BackgroundMask::recognizeBackgournd(VideoCapture& vc) {
    UMat img, timg;

    for(int n = 0; n <= pMask->getNumFrames()+1; n++) {
        vc >> timg;
        if (timg.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            exit(0);
        }
        resize(timg, img, Size(), 0.5, 0.5);

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

    UMat img, timg;
    bgMask.copyTo(accumulatedMask);
    int andFrames = 5;

    for(int n = 0; n <= accumulateNumFrames; n += andFrames) {

        UMat tmpMask(bgMask);

        for(int m=0; m<andFrames; m++) {
            vc >> timg;
            if (timg.empty())  {
                std::cerr << "ERROR : Unable to load frame" << std::endl;
                exit(0);
            }
            resize(timg, img, Size(), 0.5, 0.5);

            pMask->apply(img, bgMask);
            bitwise_and(bgMask, tmpMask, tmpMask);
        }
        // Reduce noise
        bitwise_or(tmpMask, accumulatedMask, accumulatedMask);

        imshow( CamDef::originalVideo, img );  //  show original image
        imshow( CamDef::mask, accumulatedMask );  // show background mask

        if( waitKey( CamDef::DELAY ) == CamDef::ESC ) {
            std::cout << "Closing the program ..." << std::endl;
            exit(0);
        }
    }

    img.release();
}


void BackgroundMask::printProperties() {
    std::cout << "InitializationFrames : " << pMask->getNumFrames()
    << "\nLearningRate : " << pMask->getDefaultLearningRate()
    << "\nQuantizationLevels : " << pMask->getQuantizationLevels ()
    << "\nSmoothingRadius : " << pMask->getSmoothingRadius()
    << "\nUpdateBackgroundModel : " << pMask->getUpdateBackgroundModel()
    << std::endl;
}

void BackgroundMask::setRecognizeNumFrames(int num) {
    pMask->setNumFrames(num);
}


void BackgroundMask::setAccumulateNumFrames(int num) {
    accumulateNumFrames = num;
}

void BackgroundMask::setLearningRate(double rate) {
    pMask->setDefaultLearningRate(rate);
}


// Only copy the foreground using the completed mask
void BackgroundMask::locateForeground(UMat& src, UMat& dst) {
    assert(!accumulatedMask.empty());
    dst.release();
    src.copyTo(dst, accumulatedMask);
}