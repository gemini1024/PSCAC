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


// Determine if the objects found were found
bool Detector::isFound(void) {
    return !found.empty();
}

// Returns the coordinates of the objects found in frame.
// the returned coordinate values are intended for output and can not be changed.
const std::vector<Rect>& Detector::getFoundObjects(void) {
    return found;
}



// Functions of PedestriansDetector
PedestriansDetector::PedestriansDetector()
: Detector("camera/data/funvision_cascade_pedestrians.xml" ) {
}

PedestriansDetector::~PedestriansDetector() {
}


void PedestriansDetector::detect(UMat& img) {
    // Find Pedestrians
    // detector.detectMultiScale(img, found, 1.1, 2, 0, Size(0,0), Size(160,300));
    detector.detectMultiScale(img, found, 1.04, 4, 1, Size(30,80), Size(80,200));

    // Draw Green Rectangle on Pedestrians
    for ( auto const& r : found ) {
        rectangle(img, r.tl(), r.br(), Scalar(0,255,0), 3);
    }
}





// Functions of VehiclesDetector
VehiclesDetector::VehiclesDetector()
: Detector("camera/data/funvision_cascade_vehicles.xml") {
}

VehiclesDetector::~VehiclesDetector() {
}


void VehiclesDetector::detect(UMat& img) {
    // Find Vehicles
    // detector.detectMultiScale(img, found, 1.1, 2, 0, Size(30,30), Size(480,480));
    detector.detectMultiScale(img, found, 1.1, 5, 1, Size(20,20), Size(600,600));

    // Draw Red Rectangle on Vehicles
    for ( auto const& r : found ) {
        rectangle(img, r.tl(), r.br(), Scalar(0,0,255), 3);
    }
}
