#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <iostream>

using namespace cv; // openCV
using namespace std;

int takeRoad(void)
{
    VideoCapture vc(0); // Connect cam
    if (!vc.isOpened()) return false;

    Mat img, dst; //img선언
    cout<<"Press ESC to exit"<<endl;
    while (1) {
        vc >> img; // Put the captured image in img
        if (img.empty()) break;

        flip(img, dst, 1);	// Reverse left / right

        imshow("detect", dst);  // show image

        if (waitKey(10) == 27) break; // ESC(27) -> break
    }
    destroyAllWindows();

    return true;
}
