// detectors.cpp
// Detectors' functions

#include "detectors.h"

PedestriansDetector::PedestriansDetector() {
    if( !detector.load( "data/hogcascade_pedestrians.xml" ) ) {
        cerr << "ERROR: Could not load classifier human detect cascade" << endl;
        exit(1);
    }
}

PedestriansDetector::~PedestriansDetector() {
}

void PedestriansDetector::findPedestrians(Mat& img) {
    // Find Pedestrians
    detector.detectMultiScale(img, found, 1.1, 6, 0, cvSize(48,96), cvSize(100,200));

    // Draw Rectangle on Pedestrians
    for ( size_t i = 0 ; i < found.size() ; i++ ) {
        Rect r = found[i];
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}
