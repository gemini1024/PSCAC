// CamDef.h
// Defines what the camera module needs

#ifndef CAMDEF_H
#define CAMDEF_H

#include <string>

namespace CamDef {

    enum {
        DELAY = 10, // 10 ms
        CLOSE = 27, // ESC
        PAUSE = 32 // SPACE BAR
    };

    // Video's source
    const std::string sampleVideo("camera/scenario2.mp4"); // from https://www.youtube.com/watch?v=V3oZI1G3H5M

    // The title of screens printed on screens
    const std::string learnedMask("camera/scenario_mask.png");
    const std::string learnedRoadImg("camera/scenario_road.png");
    const std::string originalVideo("origin");
    const std::string mask("mask");
    const std::string roadImg("roadImg");
    const std::string sign("SIGN");
    const std::string resultVideo("detect");
}


#endif

