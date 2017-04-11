// Situation.h
// Predict harzard situation

#ifndef SITUATION_H
#define SITUATION_H


#include <opencv2/opencv.hpp>

using namespace cv;


class Situation {
private :
    enum {
    	SAFETY,
    	WARNING,
    	STOP
    };
    UMat roadImg;
    Mat safetyImg;
    Mat warningImg;
    Mat stopImg;
    const int delay;
    int safeCnt;

private :
    void setSituation(int situation);

public :
    Situation(int imgRows, int imgCols, int delay);
    ~Situation();
    const UMat& getRoadImg(void);
    void updateRoadImg(const std::vector<Rect>& foundVehicles);
    void sendPredictedSituation(const std::vector<Rect>& foundPedestrians);
};


#endif