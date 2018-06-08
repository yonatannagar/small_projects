CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread

all: bin/connectionHandler.o bin/echoClient.o
	g++ -o bin/BBclient bin/connectionHandler.o bin/echoClient.o $(LDFLAGS) 
	
bin/connectionHandler.o: src/connectionHandler.cpp include/connectionHandler.h
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
clean:
	rm -f bin/*
