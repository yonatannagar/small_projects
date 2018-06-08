#include <stdlib.h>
#include <iostream>
#include "../include/connectionHandler.h"
#include <boost/thread.hpp>

/**
 * recieves input from handler, prints to console
 * @param handler
 * @param loggedIn
 */
    void task1(ConnectionHandler* handler, boost::atomic<bool>* loggedIn){
        while (!std::cin.eof()) {
            std::string response;
            if (!(*handler).getLine(response)) {
				(*handler).close();
				break;
            }

            int len = response.length();

            response.resize(len - 1);
            std::cout << response << std::endl;
			if(response == "ACK login succeeded")
				*loggedIn=true;
            if (response == "ACK signout succeeded") {
                (*handler).close();

                break;
            }
            boost::this_thread::yield();

        }
    }
/**
 * recieves input from keyboard, sends through handler
 * @param handler
 * @param loggedIn
 */
void task2(ConnectionHandler* handler, boost::atomic<bool>* loggedIn) {
    while (!std::cin.eof()) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!(*handler).sendLine(line)) {
            break;
        }
        if (line.compare("SIGNOUT")==0&&(*loggedIn)) {
            break;
        }

    }
}

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    //std::string host = "127.0.0.1";
    //short port = 7777;
    ConnectionHandler handler(host, port);
    if (!handler.connect()) {
        return -1;
    }
	boost::atomic<bool> loggedIn(false);



    boost::thread t1(task1, &handler, &loggedIn);
    boost::thread t2(task2, &handler, &loggedIn);
    t1.join();
    t2.join();
    return 0;
}
