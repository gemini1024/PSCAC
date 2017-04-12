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
    safetyImg = imread( SignsDef::safety_big );
    cautionImg = imread( SignsDef::caution_big );
    dangerImg = imread( SignsDef::danger_big );
    if( safetyImg.empty() || cautionImg.empty() || dangerImg.empty() ) {
        std::cerr << "ERROR : Could not load signpost image" << std::endl;
        exit(1);
    }
    imshow( CamDef::sign, safetyImg );
}

Situation::~Situation() {
    dangerImg.release();
    cautionImg.release();
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
        line(roadImg, Point(r.tl().x, r.br().y), r.br(), Scalar(0,0,255), 1);
    }
}


// Load the road image previously created by the call to updateRoadImg()
void Situation::loadRoadImg(void) {
    Mat learnedRoadImg = imread( CamDef::learnedRoadImg  );
    if ( learnedRoadImg.empty() ) {
        std::cerr << "ERROR : Unable to load learnedRoadImg" << std::endl;
        exit(1);
    }
    learnedRoadImg.copyTo(roadImg);
    learnedRoadImg.release();
}


// Predicts a dangerous situation and generates a corresponding signal
void Situation::sendPredictedSituation(const std::vector<Rect>& foundPedestrians) {

    // TODO : Store the coordinates for a period of time and predict the risk situation.
    if ( !foundPedestrians.empty() ) {
       Mat roadMat = roadImg.getMat( ACCESS_READ );
       for( auto const& r : foundPedestrians ) {

            // The detected pedestrian object and roadImg are used to judge the dangerous situation
            int hitCount = 0;
            for( int i=0; i <= 5; i++) {
                // If the lower 5 coordinates of the center of the object are above roadImg, it is judged to be in state DANGER.
                if( roadMat.at<Vec3b>(Point( (r.br()-r.tl()).x/2 + r.tl().x, (r.br()-r.tl()).y/4*3 + r.tl().y + i ) )[2] == 255  ) {
                    hitCount++;
                }
                if ( hitCount > 3 ) {
                    setSituation( DANGER );
                    break;
                // If it is not the current DANGER state, if the coordinates of both ends of the lower end of the object are roadImg, it is judged as CAUTION state
                } else if( safeCnt < delay/2 && (roadMat.at<Vec3b>( r.br() )[2] == 255
                    || roadMat.at<Vec3b>(Point( r.tl().x, r.br().y ))[2] == 255 ) ) {
                    setSituation( CAUTION );
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
            std::cout << " [[ SAFETY ]] This road is safety" << std:: endl;
            imshow( CamDef::sign, safetyImg );
            safeCnt = 0;
            break;
        case CAUTION :
            sendSignalToParentProcess( SigDef::SIG_WARNING );
            std::cout << " [[ CAUTION ! ]] Human are approaching" << std:: endl;
            imshow( CamDef::sign, cautionImg );
            safeCnt = delay/2;
            break;
        case DANGER :
            sendSignalToParentProcess( SigDef::SIG_STOP );
            std::cout << " [[ DANGER !! ]] Human in roadImg " << std:: endl;
            imshow( CamDef::sign, dangerImg );
            safeCnt = delay;
            break;
        default :
            break;
    }
}