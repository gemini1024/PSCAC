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
    	CAUTION,
    	DANGER
    };
    UMat roadImg;
    Mat safetyImg;
    Mat cautionImg;
    Mat dangerImg;
    const int delay;
    int safeCnt;

private :
    void setSituation(int situation);

public :
    Situation(int imgRows, int imgCols, int delay);
    ~Situation();
    const UMat& getRoadImg(void);
    void updateRoadImg(const std::vector<Rect>& foundVehicles);
    void loadRoadImg(void);
    void sendPredictedSituation(const std::vector<Rect>& foundPedestrians);
};


#endif