#!/bin/sh

# Make file for Anbu and SDL_interface
all: anbu.cpp
	@echo "Building Anbu, sdl_interface for FreeJ2ME ..."
	@g++ -std=c++11 -lSDL2 -lpthread -lfreeimage -o sdl_interface anbu.cpp

install:
	@echo "Installing interface to /usr/local/bin/"
	@cp ./sdl_interface /usr/local/bin/sdl_interface

clean:
	rm ./sdl_interface
