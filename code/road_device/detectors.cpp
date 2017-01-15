// detectors.cpp
// Detectors' functions

#include "detectors.h"

PedestriansDetector::PedestriansDetector() {
    hog_detector.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());
}

PedestriansDetector::~PedestriansDetector() {
}

void PedestriansDetector::findPedestrians(Mat& img) {
    // Find Pedestrians
    hog_detector.detectMultiScale(img, found, 0, Size(8,8), Size(32,32), 1.05, 2);

    // Draw Rectangle on Pedestrians
    for ( size_t i = 0 ; i < found.size() ; i++ ) {
        Rect r = found[i];
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}
