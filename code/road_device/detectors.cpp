// detectors.cpp
// Detectors' functions

#include "detectors.h"

PedestriansDetector::PedestriansDetector() {
    if( !detector.load( "data/haarcascade_fullbody.xml" ) ) {
        cerr << "ERROR: Could not load classifier human detect cascade" << endl;
        exit(1);
    }
}

PedestriansDetector::~PedestriansDetector() {
}

void PedestriansDetector::findPedestrians(Mat& img) {
    // Find Pedestrians
    detector.detectMultiScale(img, found, 1.1, 2, 0|1, Size(40,70), Size(80,300));

    // Draw Rectangle on Pedestrians
    for ( size_t i = 0 ; i < found.size() ; i++ ) {
        Rect r = found[i];
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}
