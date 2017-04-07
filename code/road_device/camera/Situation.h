// Situation.h
// Predict harzard situation

#ifndef SITUATION_H
#define SITUATION_H


#include <opencv2/opencv.hpp>
#include "../communication/SigDef.h"

using namespace cv;


class Situation {
private :
    UMat roadImg;

public :
    Situation(int imgRows, int imgCols);
    ~Situation();
    const UMat& getRoadImg(void);
    void updateRoadImg(const std::vector<Rect>& foundVehicles);
    void sendPredictedSituation(const std::vector<Rect>& foundPedestrians);
};


#endif