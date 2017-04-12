# Makefile
# using opencv

CC = g++
DIRS = camera communication
OBJS = main.o camera.o BackgroundMask.o Detectors.o Situation.o SigDef.o ConnectServer.o
TARGET = detect

.SUFFIXES : .cpp .o

all : subobj $(TARGET)

subobj :
	@for dir in $(DIRS); do \
	make -C $$dir || exit $?; \
	done

$(TARGET): $(OBJS)
	$(CC) -o $@ $(OBJS) `pkg-config opencv --cflags --libs` -pthread -O2

clean :
	@for dir in $(DIRS); do \
	make -C $$dir clean; \
	done
	rm -f $(OBJS) $(TARGET) core
