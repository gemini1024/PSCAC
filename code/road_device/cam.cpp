#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>

using namespace cv; // openCV
using namespace std;

int takeRoad(void)
{
    // TODO : Connect camera when executing on odroid
    VideoCapture vc("sample.avi"); // Load test video
    if (!vc.isOpened()) return false;

    Mat img; // frame

    while (1) {
        vc >> img; // Put the captured image in img
        if (img.empty()) break;

        imshow("detect", img);  // show image

        if (waitKey(10) == 27) break; // ESC(27) -> break
    }
    destroyAllWindows();

    return true;
}
