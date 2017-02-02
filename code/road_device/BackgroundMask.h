// BackgroundMask.h
// recognize the background and create a frame that excludes this part
// For better detecting performance

#ifndef BACKGROUNDMASK_H
#define BACKGROUNDMASK_H

#include <opencv2/highgui.hpp>
#include <opencv2/bgsegm.hpp>
#include <iostream>

using namespace cv;



class BackgroundMask {
private :
    // TODO : Select background recognition method
    // Ptr<BackgroundSubtractor> pMask = createBackgroundSubtractorMOG2();
    // Ptr<BackgroundSubtractor> pMask = createBackgroundSubtractorKNN();
    // Ptr<BackgroundSubtractor> pMask = bgsegm::createBackgroundSubtractorMOG();
    Ptr<bgsegm::BackgroundSubtractorGMG> pMask = bgsegm::createBackgroundSubtractorGMG();
    UMat bgMask; // Mask excluding moving objects
    UMat accumulatedMask;
    int accumulateNumFrames;

private :
    void recognizeBackgournd(VideoCapture& vc);
    void accumulateMasks(VideoCapture& vc);

public :
    BackgroundMask();
    ~BackgroundMask();
    UMat createBackgroundMask(VideoCapture& vc);
    void printProperties();
    void setRecognizeNumFrames(int num);
    void setAccumulateNumFrames(int num);
    void locateForeground(UMat& src, UMat& dst);
};


#endif