// CamDef.cpp
// Defines what the camera module needs


#include "CamDef.h"
#include <opencv2/highgui.hpp>


// The keyboard controls the start and stop of camera motion
// Returns true if the user presses ESC
bool CamDef::shouldStop(void) {
    bool pauseCamera = false;
    char pressedKey;
    do {
        // press SPACE BAR -> pause video
        // press ESC -> close video
        if ( ( pressedKey = cv::waitKey( CamDef::DELAY ) ) == CamDef::PAUSE ) // SPACE BAR
            pauseCamera = !pauseCamera;
        else if(  pressedKey == CamDef::CLOSE ) { // ESC
            return true;
        }
    } while( pauseCamera );
    return false;
}
