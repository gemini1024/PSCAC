// Situation.cpp
// Predict harzard situation

#include "Situation.h"
#include "CamDef.h"
#include "../communication/SigDef.h"
#include "../signs/SignsDef.h"



// param - imgRows, imgCols : Get the size of the frame to initialize roadImg
// param - delay : Delay time to switch from a caution situation to a safety situation
Situation::Situation(int imgRows, int imgCols, int delay) : delay(delay), safeCnt(0) {
    roadImg = UMat::zeros(imgRows, imgCols, CV_8UC3);
    safetyImg = imread( SignsDef::safety );
    warningImg = imread( SignsDef::warning );
    stopImg = imread( SignsDef::stop );
    if( safetyImg.empty() || warningImg.empty() || stopImg.empty() ) {
        std::cerr << "ERROR : Could not load signpost image" << std::endl;
        exit(1);
    }
    imshow( CamDef::sign, safetyImg );
}

Situation::~Situation() {
    stopImg.release();
    warningImg.release();
    safetyImg.release();
    roadImg.release();
}



const UMat& Situation::getRoadImg(void) {
    return roadImg;
}


// Draw a roadImg using objects of found cars
void Situation::updateRoadImg(const std::vector<Rect>& foundVehicles) {
    // Draw Red line under the vehicles
    for ( auto const& r : foundVehicles ) {
        rectangle(roadImg, Point(r.tl().x, r.br().y-3), r.br(), Scalar(0,0,255), -1);
    }

    // TODO : Remove when the situation is judged to some extent.
    if ( !foundVehicles.empty() ) {
        for( auto const& r : foundVehicles ) {
            std::cout << "Car : tl = (" << r.tl().x << "," << r.tl().y << ") , br = ("
            << r.br().x << "," << r.br().y << "), md = ("
            << ( r.br().x - r.tl().x )/2 + r.tl().x << "," << ( r.br().y - r.tl().y )/2 + r.tl().y << ")" << std::endl;
        }
    }
}


// Predicts a dangerous situation and generates a corresponding signal
void Situation::sendPredictedSituation(const std::vector<Rect>& foundPedestrians) {

    // TODO : Store the coordinates for a period of time and predict the risk situation.
    if ( !foundPedestrians.empty() ) {
       Mat roadMat = roadImg.getMat( ACCESS_READ );
       for( auto const& r : foundPedestrians ) {
            std::cout << "Human : tl = (" << r.tl().x << "," << r.tl().y << ") , br = ("
            << r.br().x << "," << r.br().y << "), md = ("
            << ( r.br().x - r.tl().x )/2 + r.tl().x << "," << ( r.br().y - r.tl().y )/2 + r.tl().y << ")" << std::endl;


            // Warns you if one of the top and bottom coordinates in the center of a person object has red coordinates
            int hitCount = 0;
            for( int i=0; i <= 5; i++) {
                if( roadMat.at<Vec3b>(Point( (r.br()-r.tl()).x/2 + r.tl().x, (r.br()-r.tl()).y/4*3 + r.tl().y + i ) )[2] == 255  ) {
                    hitCount++;
                }
                if ( hitCount > 3 ) {
                    setSituation(STOP);
                    break;
                }
            }
        }
        roadMat.release();
    }
    // After a certain period of time, switch to safety
    if( --safeCnt < 0 )
        setSituation(SAFETY);
}


// Set the current status and send it to LCD
void Situation::setSituation(int situation) {
    switch(situation) {
        case SAFETY :
            std::cout << " [[ Safety ]]" << std:: endl;
            imshow( CamDef::sign, safetyImg );
            safeCnt = 0;
            break;
        case WARNING :
            sendSignalToParentProcess( SigDef::SIG_WARNING );
            std::cout << " [[ Warning !! ]] Human are approaching" << std:: endl;
            imshow( CamDef::sign, warningImg );
            safeCnt = delay;
            break;
        case STOP :
            sendSignalToParentProcess( SigDef::SIG_STOP );
            std::cout << " [[ STOP !! ]] Human in roadImg " << std:: endl;
            imshow( CamDef::sign, stopImg );
            safeCnt = delay;
            break;
        default :
            break;
    }
}