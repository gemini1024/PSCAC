// Detectors.h
// Detectors using opencv


#ifndef DETECTOR_H
#define DETECTOR_H

#include <opencv2/opencv.hpp>
#include <string>

using namespace cv;



class Detector {
protected:
    CascadeClassifier detector;
    std::vector<Rect> found;

public:
    Detector(const std::string data_xml);
    virtual ~Detector();
    virtual void detect(UMat& img) = 0;
    bool isFound(void);
    const std::vector<Rect>& getFoundObjects(void);
};




class PedestriansDetector : public Detector {
private:

public:
    PedestriansDetector();
    virtual ~PedestriansDetector();
    virtual void detect(UMat& img);
};





class VehiclesDetector : public Detector {
private:
    UMat roadImg;

public:
    VehiclesDetector();
    virtual ~VehiclesDetector();
    virtual void detect(UMat& img);
    void initRoadImg(UMat& mask);
    const UMat& getRoadImg(void);
};

#endif
