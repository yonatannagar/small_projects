# define some Makefile variables for the compiler and compiler flags
# to use Makefile variables later in the Makefile: $()
CC = g++
CFLAGS  = -g -Wall -Weffc++ -std=c++11
LFLAGS  = -L/usr/lib

# All Targets
all: fs

# Tool invocations
# Executable "fs" depends on the files: 
fs: bin/Files.o bin/FileSystem.o bin/Commands.o bin/Environment.o bin/Main.o
	@echo 'Building target: fs'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/fs bin/Files.o bin/FileSystem.o bin/Commands.o bin/Environment.o bin/Main.o $(LFLAGS)
	@echo 'Finished building target: fs'
	@echo ' '

# Depends on the source and header files
bin/Main.o: src/Main.cpp
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Main.o src/Main.cpp

# Depends on the source and header files 
bin/Files.o: src/Files.cpp include/Files.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Files.o src/Files.cpp

# Depends on the source and header files
bin/FileSystem.o: src/FileSystem.cpp include/FileSystem.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/FileSystem.o src/FileSystem.cpp

# Depends on the source and header files
bin/Commands.o: src/Commands.cpp include/Commands.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Commands.o src/Commands.cpp

# Depends on the source and header files
bin/Environment.o: src/Environment.cpp include/Environment.h
	$(CC) $(CFLAGS) -c -Iinclude -o bin/Environment.o src/Environment.cpp


#Clean the build directory and executable
clean: 
	rm -f bin/*
