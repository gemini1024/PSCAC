# Makefile

CC = g++
CFLAGS = -c -std=c++11

.cpp.o :
	$(CC) $(CFLAGS) -o $@ $<

OBJS = $(patsubst %.cpp, %.o, $(wildcard *.cpp))

all : $(OBJS)
	cp -f $^ ../

clean:
	rm -f *.o
