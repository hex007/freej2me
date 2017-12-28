#!/bin/sh

ifeq ($(DESTDIR),)
    DESTDIR := /usr/local/bin
endif

# Make file for Anbu and SDL_interface
all: anbu.cpp
	@echo "Building Anbu, sdl_interface for FreeJ2ME"
	@g++ -std=c++11 -lSDL2 -lpthread -lfreeimage -o sdl_interface anbu.cpp

.PHONY: install
install:
	@echo "Installing Anbu, sdl_interface as $(DESTDIR)/sdl_interface"
	@cp ./sdl_interface $(DESTDIR)/sdl_interface

clean:
	rm ./sdl_interface
