// detectors.cpp
// Detectors' functions

#include "detectors.h"


// Functions of Detector
Detector::Detector(const string data_xml) {
    if( !detector.load( data_xml ) ) {
        cerr << "ERROR: Could not load classifier " << data_xml << endl;
        exit(1);
    }
}

Detector::~Detector() {
}




// Functions of PedestriansDetector
PedestriansDetector::PedestriansDetector()
: Detector("data/haarcascade_fullbody.xml" ) {
}

PedestriansDetector::~PedestriansDetector() {
}

void PedestriansDetector::detect(UMat& img) {
    // Find Pedestrians
    detector.detectMultiScale(img, found, 1.1, 2, 0, Size(40,70), Size(80,300));

    // Draw Green Rectangle on Pedestrians
    for ( size_t i = 0 ; i < found.size() ; i++ ) {
        Rect r = found[i];
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}





// Functions of VehiclesDetector
VehiclesDetector::VehiclesDetector()
: Detector("data/haarcascade_cars.xml") {
}

VehiclesDetector::~VehiclesDetector() {
}

void VehiclesDetector::detect(UMat& img) {
    // Find Vehicles
    // TODO : Reduce false alarm rate
    detector.detectMultiScale(img, found, 1.1, 2, 0, Size(30,30), Size(480,480));

    // Draw Red Rectangle on Vehicles
    for ( size_t i = 0 ; i < found.size() ; i++ ) {
        Rect r = found[i];
        rectangle(img, r.tl(), r.br(), Scalar(0,0,255), 3);
    }
}
