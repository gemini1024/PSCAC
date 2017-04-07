// Situation.cpp
// Predict harzard situation

#include "Situation.h"


// Get the size of the frame to initialize roadImg
Situation::Situation(int imgRows, int imgCols) {
    roadImg = UMat::zeros(imgRows, imgCols, CV_8UC3);
}

Situation::~Situation() {
}



const UMat& Situation::getRoadImg(void) {
    return roadImg;
}


// Draw a roadImg using objects of found cars
void Situation::updateRoadImg(const std::vector<Rect>& foundVehicles) {
    // Draw Red line under the vehicles
    for ( auto const& r : foundVehicles ) {
        rectangle(roadImg, Point(r.tl().x, r.br().y+5), r.br(), Scalar(0,0,255), -1);
    }

    // TODO : Remove when the situation is judged to some extent.
    if ( !foundVehicles.empty() ) {
       sendSignalToParentProcess( SigDef::SIG_FOUND_CAR );

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
       sendSignalToParentProcess( SigDef::SIG_FOUND_HUMAN );

       Mat roadMat = roadImg.getMat( ACCESS_READ );
        for( auto const& r : foundPedestrians ) {
            std::cout << "Human : tl = (" << r.tl().x << "," << r.tl().y << ") , br = ("
            << r.br().x << "," << r.br().y << "), md = ("
            << ( r.br().x - r.tl().x )/2 + r.tl().x << "," << ( r.br().y - r.tl().y )/2 + r.tl().y << ")" << std::endl;


            // Warns you if one of the top and bottom coordinates in the center of a person object has red coordinates
            for( int i=-2; i <= 2; i++) {
                if( roadMat.at<Vec3b>(Point( (r.br()-r.tl()).x/2 + r.tl().x, (r.br()-r.tl()).y/2 + r.tl().y + i ) )[2] == 255  ) {
                    std::cout << " [[ Warning !! ]] Human in roadImg " << std:: endl;
                    break;
                }
            }
        }
        roadMat.release();
    }
}