all: build-trec build-new

build-trec: main.cpp
	g++ -std=c++0x main.cpp -o build-trec -I ./flann/headers ./flann/libs/windows/flann.dll -static-libgcc -lstdc++

build-new: build-new.cpp
	g++ -std=c++0x build-new.cpp -o build-new -I ./flann/headers ./flann/libs/windows/flann.dll -static-libgcc -lstdc++