// detectors.h
// Detectors using opencv


#ifndef DETECTOR_H
#define DETECTOR_H

#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

class PedestriansDetector {
	private:
	    // TODO : Change to "cascade hog" because hog is slow
	    HOGDescriptor hog_detector; // Pedestrians Detector
	    vector<Rect> found;

	public:
	    PedestriansDetector();
	    ~PedestriansDetector();
	    void findPedestrians(Mat& img);
};

#endif
