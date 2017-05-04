// Situation.h
// Predict harzard situation

#ifndef SITUATION_H
#define SITUATION_H


#include <opencv2/opencv.hpp>
#include "BackgroundMask.h"
#include "Detectors.h"

using namespace cv;


class Situation {
private :
    enum {
    	SAFETY,
    	CAUTION,
    	DANGER
    };
    // Used for calculation
    UMat roadImg;
    // For output only
    Mat safetyImg;
    Mat cautionImg;
    Mat dangerImg;
    // delays
    const int delay;
    int sendDelayCnt;
    int safeCnt;

private :
    void setSituation(int situation, bool isCarOnRoad);
    void trimeRoadImg(void);

public :
    Situation(int imgRows, int imgCols, int delay);
    ~Situation();
    const UMat& getRoadImg(void);
    void createRoadImg(VideoCapture& vc, BackgroundMask& bgMask, VehiclesDetector& car_Detector, unsigned int accumulateNumFrames);
    void loadRoadImg(void);
    void setSignToFullScreen(void);
    void sendPredictedSituation(const std::vector<Rect>& foundPedestrians, bool isCarOnRoad);
};


#endif