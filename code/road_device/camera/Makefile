.cpp.o : 
	g++ -c -std=c++11 -o $@ $<

OBJS = $(patsubst %.cpp, %.o, $(wildcard *.cpp))

all : $(OBJS)
	cp -f $^ ../

clean:
	rm -f *.o
