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

    // The title of screens printed on screens
    // const std::string learnedMask("camera/data/scenario_mask.png");
    // const std::string learnedRoadImg("camera/data/scenario_road.png");
    const std::string learnedMask("camera/data/learnedMask_opt.png");
    const std::string learnedRoadImg("camera/data/learnedRoadImg_opt.png");

    // Windows name
    const std::string originalVideo("origin");
    const std::string mask("mask");
    const std::string roadImg("roadImg");
    const std::string sign("SIGN");
    const std::string resultVideo("detect");

    // Functions for controlling the camera with the keyboard
    bool shouldStop(void);
}


#endif

