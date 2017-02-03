# Makefile
# using opencv

CC = g++
OBJS = main.o camera.o BackgroundMask.o Detectors.o SigDef.o
TARGET = detect

.SUFFIXES : .cpp .o

all : $(TARGET)

$(TARGET): $(OBJS)
	$(CC) -o $@ $(OBJS) `pkg-config opencv --cflags --libs`

clean :
	rm -f $(OBJS) $(TARGET) core
