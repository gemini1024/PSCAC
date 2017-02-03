// ConnectServer.h
// Send data to server

#ifndef CONNECT_SERVER_H
#define CONNECT_SERVER_H

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>

using namespace std;


class ConnectServer {
private :
    enum {
        MESSAGE_SIZE = 100
    };
    int sd;
    struct sockaddr_in sin;

public:
    ConnectServer(const string hostName, const int portNum);
    ~ConnectServer();
    void sendMessage(const string message);
};


#endif
