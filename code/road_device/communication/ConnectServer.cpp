// ConnectServer.cpp
// Send data to server

#include "ConnectServer.h"
#include <unistd.h>
#include <string.h>
#include <iostream>

// Initialize socket descriptor and set server location.
// param - hostName : server ip
// param - portNum : server port
ConnectServer::ConnectServer(const string hostName, const int portNum) {
    // Open the socket. Currently UDP. Changeable.
    if( (sd = socket(AF_INET, SOCK_DGRAM, 0)) == -1 ) {
        perror("socket");
        exit(1);
    }

    memset(&sin, 0, sizeof(sin));
    sin.sin_family = AF_INET;
    sin.sin_port = htons(portNum);
    sin.sin_addr.s_addr = inet_addr(hostName.c_str());
}

// Terminates the connection with the server
ConnectServer::~ConnectServer() {
    close(sd);
}


// Send the message to the server.
// param - message : Message to send to server
void ConnectServer::sendMessage(const string message) {
    char buf[MESSAGE_SIZE];

    bcopy(message.c_str(), buf, MESSAGE_SIZE);
    if( sendto(sd, buf, strlen(buf)+1, 0, (struct sockaddr *)&sin, sizeof(sin)) == -1 ) {
        perror("sendto");
        exit(1);
    }
    // std::cout<< "Send Server" << std::endl;
}