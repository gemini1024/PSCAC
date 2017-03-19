// CamDef.h
// Defines what the camera module needs

#ifndef CAMDEF_H
#define CAMDEF_H

#include <string>

namespace CamDef {
    enum {
        DELAY = 10,
        ESC = 27
    };
    const std::string sampleVideo("camera/sample.mp4"); // from https://www.youtube.com/watch?v=V3oZI1G3H5M
    const std::string originalVideo("origin");
    const std::string mask("mask");
    const std::string resultVideo("detect");
}


#endif

