// detectors.h
// Detectors using opencv


#ifndef DETECTOR_H
#define DETECTOR_H

#include <opencv2/opencv.hpp>
#include <string>

using namespace cv;
using namespace std;




class Detector {
protected:
    CascadeClassifier detector;
    vector<Rect> found;

public:
    Detector(const string data_xml);
    ~Detector();
    virtual void detect(UMat& img) = 0;
};




class PedestriansDetector : public Detector {
private:

public:
    PedestriansDetector();
    ~PedestriansDetector();
    virtual void detect(UMat& img);
    // TODO : Process when pedestrians exist

};





class VehiclesDetector : public Detector {
private:

public:
    VehiclesDetector();
    ~VehiclesDetector();
    virtual void detect(UMat& img);
    // TODO : Process when vehicles exist

};

#endif
