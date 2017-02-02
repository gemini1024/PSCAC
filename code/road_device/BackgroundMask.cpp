// BackgroundMask.cpp
// recognize the background and create a frame that excludes this part
// For better detecting performance

#include "BackgroundMask.h"
#include "CamDef.h"


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

    return accumulatedMask;
}

// Recognize a moving object and wait until a mask using GMG method is created
void BackgroundMask::recognizeBackgournd(VideoCapture& vc) {
    UMat img;

    for(int n = 0; n <= pMask->getNumFrames()+1; n++) {
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

    for(int n = 0; n <= accumulateNumFrames; n++) {
        vc >> img;
        if (img.empty())  {
            std::cerr << "ERROR : Unable to load frame" << std::endl;
            exit(0);
        }

        pMask->apply(img, bgMask);
        bitwise_or(bgMask, accumulatedMask, accumulatedMask);

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
    << "LearningRate : " << pMask->getDefaultLearningRate()
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


// Only copy the foreground using the completed mask
void BackgroundMask::locateForeground(UMat& src, UMat& dst) {
    assert(!accumulatedMask.empty());
    dst.release();
    src.copyTo(dst, accumulatedMask);
}