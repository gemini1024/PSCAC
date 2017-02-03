// Detectors.cpp
// Detectors' functions

#include "Detectors.h"


// Functions of Detector
Detector::Detector(const std::string data_xml) {
    if( !detector.load( data_xml ) ) {
        std::cerr << "ERROR: Could not load classifier " << data_xml << std::endl;
        exit(1);
    }
}

Detector::~Detector() {
}


bool Detector::isFound(void) {
    return !found.empty();
}




// Functions of PedestriansDetector
PedestriansDetector::PedestriansDetector()
: Detector("camera/data/haarcascade_fullbody.xml" ) {
}

PedestriansDetector::~PedestriansDetector() {
}


void PedestriansDetector::detect(UMat& img) {
    // Find Pedestrians
    detector.detectMultiScale(img, found, 1.1, 2, 0, Size(0,0), Size(160,300));

    // Draw Green Rectangle on Pedestrians
    for ( auto const& r : found ) {
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}





// Functions of VehiclesDetector
VehiclesDetector::VehiclesDetector()
: Detector("camera/data/haarcascade_cars.xml") {
}

VehiclesDetector::~VehiclesDetector() {
}


void VehiclesDetector::detect(UMat& img) {
    // Find Vehicles
    // TODO : Reduce false alarm rate
    detector.detectMultiScale(img, found, 1.1, 2, 0, Size(30,30), Size(480,480));

    // Draw Red Rectangle on Vehicles
    for ( auto const& r : found ) {
        rectangle(img, r.tl(), r.br(), Scalar(0,0,255), 3);
    }
}
