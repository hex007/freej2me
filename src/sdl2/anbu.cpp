/*
Anbu, an interface between FreeJ2ME emulator and SDL2
Authors:
	Anbu        Saket Dandawate (hex @ retropie)
	FreeJ2ME    D. Richardson (recompile @ retropie)
	
To compile : g++ -std=c++11 -lSDL2 -lpthread -lfreeimage -o anbu anbu.cpp

This file is part of FreeJ2ME.

FreeJ2ME is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FreeJ2ME is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/

*/

#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <assert.h>
#include <map>

#ifdef _WIN32
#include <windows.h>
#include <string>
#else
#include <pthread.h>
#endif

#include <SDL2/SDL.h>
#include <FreeImage.h>

#define DEADZONE 23000
#define BYTES 3

using namespace std;

#ifdef _WIN32
DWORD t_capturing;
#else
pthread_t t_capturing;
#endif

string bg_image = "";

int angle = 0;
int source_width = 0, source_height = 0;
int display_width = 0, display_height = 0;
double additional_scale = 1;
double overlay_scale = 1;
int last_time = 0;

bool capturing = true;
string interpol = "nearest";

int image_index = 0;

SDL_Renderer *mRenderer;
SDL_Texture *mBackground;
SDL_Texture *mOverlay;
SDL_Texture *mTexture;
SDL_Window *mWindow;

std::map<SDL_JoystickID, SDL_Joystick*> mJoysticks;
std::map<SDL_JoystickID, int*> mPrevAxisValues;

string getHelp()
{
	string help = "Anbu 0.8.5, an interface between FreeJ2ME emulator and SDL2.\n\
Usage: anbu width height [OPTION]...\n\
\n\
Options:\n\
  -i type               Interpolation to use {'nearest', 'linear', 'best'}\n\
  -r angle              Rotate incomming frame by angle in degrees [0-360]\n\
  -s scale              Additional scaling [0-1.0] of incomming frame\n\
  -c R G B              Background color in RGB format [0-255] for each channel\n\
  -b bg.png | bg.jpg    Background image in PNG or JPG format. Should have same\n\
                        aspect ratio as screen to avoid unwanted streching\n\
\n\
Authors:\n\
  Anbu:                 Saket Dandawate / hex\n\
  FreeJ2ME:             D. Richardson / recompile\n\
\n\
Licensed under GPL3. Free for Non-Commercial usage.";
	return help;
}

/**************************************************** Input / Output Handlers */
void addJoystick(int id)
{
	assert(id >= 0 && id < SDL_NumJoysticks());

	// open joystick & add to our list
	SDL_Joystick* joy = SDL_JoystickOpen(id);
	assert(joy);

	// add it to our list so we can close it again later
	SDL_JoystickID joyId = SDL_JoystickInstanceID(joy);
	mJoysticks[joyId] = joy;

	// set up the prevAxisValues
	int numAxes = SDL_JoystickNumAxes(joy);
	mPrevAxisValues[joyId] = new int[numAxes];
	std::fill(mPrevAxisValues[joyId], mPrevAxisValues[joyId] + numAxes, 0);
}

void removeJoystick(SDL_JoystickID joyId)
{
	assert(joyId != -1);
	// delete old prevAxisValues
	auto axisIt = mPrevAxisValues.find(joyId);
	delete[] axisIt->second;
	mPrevAxisValues.erase(axisIt);

	// close the joystick
	auto joyIt = mJoysticks.find(joyId);
	if(joyIt != mJoysticks.end())
	{
		SDL_JoystickClose(joyIt->second);
		mJoysticks.erase(joyIt);
	}
}

void sendKey(int key, bool pressed, bool joystick)
{
	unsigned char bytes [5];
	bytes[0] = (char) (joystick << 4) | pressed;
	bytes[1] = (char) (key >> 24);
	bytes[2] = (char) (key >> 16);
	bytes[3] = (char) (key >> 8);
	bytes[4] = (char) (key);
	fwrite(&bytes, sizeof(char), 5, stdout);
}

bool sendQuitEvent()
{
	SDL_Event* quit = new SDL_Event();
	quit->type = SDL_QUIT;
	SDL_PushEvent(quit);
	return true;
}

/********************************************************** Utility Functions */
void loadDisplayDimentions()
{
	SDL_DisplayMode dispMode;
	SDL_GetDesktopDisplayMode(0, &dispMode);
	display_width = dispMode.w;
	display_height = dispMode.h;
}

SDL_Rect getDestinationRect()
{
	double scale;
	switch (angle)
	{
	case 0:
	case 180:
		scale = min( (double) display_width/source_width, (double) display_height/source_height );
		break;
	case 90:
	case 270:
		scale = min( (double) display_width/source_height, (double) display_height/source_width );
		break;
	default:
		double angle_r = std::acos(-1) * angle / 180;
		double bound_W = fabs(cos(angle_r) * source_width) + fabs(sin(angle_r) * source_height);
		double bound_H = fabs(sin(angle_r) * source_width) + fabs(cos(angle_r) * source_height);
		scale = min(display_width / bound_W, display_height / bound_H);
		break;
	}

	int w = source_width * scale * additional_scale, h = source_height * scale * additional_scale;
	return { (display_width - w )/2, (display_height - h)/2, w, h };
}

bool updateFrame(size_t num_chars, unsigned char* buffer, FILE* input = stdin)
{
	int read_count = fread(buffer, sizeof(char), num_chars, input);
	return read_count == num_chars;
}

void saveFrame(unsigned char* frame)
{
	int width = source_width;
	int height = source_height;
	int scan_width = source_width * sizeof(unsigned char) * 3;
	FIBITMAP *dst = FreeImage_ConvertFromRawBits(frame, width, height, scan_width, 32, FI_RGBA_RED_MASK, FI_RGBA_GREEN_MASK, FI_RGBA_BLUE_MASK, FALSE);
	string name = to_string(image_index) + ".jpg";
	image_index++;
	FreeImage_Save(FIF_JPEG, dst, name.c_str(), 0);
}

void drawFrame(unsigned char *frame, size_t pitch, SDL_Rect *dest, int angle, int interFrame = 16)
{
	// Cutoff rendering at 60fps
	if (SDL_GetTicks() - last_time < interFrame) {
		return;
	}

	last_time = SDL_GetTicks();

	SDL_RenderClear(mRenderer);
	SDL_UpdateTexture(mTexture, NULL, frame, pitch);
	SDL_RenderCopy(mRenderer, mBackground, NULL, NULL);
	SDL_RenderCopyEx(mRenderer, mTexture, NULL, dest, angle, NULL, SDL_FLIP_NONE);
	SDL_RenderCopyEx(mRenderer, mOverlay, NULL, dest, angle, NULL, SDL_FLIP_NONE);
	SDL_RenderPresent(mRenderer);
}

void loadBackground(string image)
{
	if (image.empty())
		return;

	FREE_IMAGE_FORMAT format = FreeImage_GetFileType(image.c_str(), 0);
	FIBITMAP* imagen = FreeImage_Load(format, image.c_str());

	int w = FreeImage_GetWidth(imagen);
	int h = FreeImage_GetHeight(imagen);
	int scan_width = FreeImage_GetPitch(imagen);

	unsigned char* buffer = new unsigned char[w * h * BYTES];
	FreeImage_ConvertToRawBits(buffer, imagen, scan_width, 24, 0, 0, 0, TRUE);
	FreeImage_Unload(imagen);

	mBackground = SDL_CreateTexture(mRenderer, SDL_PIXELFORMAT_BGR24, SDL_TEXTUREACCESS_STATIC, w, h);
	SDL_UpdateTexture(mBackground, NULL, buffer, w * sizeof(char) * BYTES);
	delete[] buffer;
}

void loadOverlay(SDL_Rect &rect)
{
	int psize =  overlay_scale * rect.w / source_width;
	int size = rect.w * rect.h * 4;
	unsigned char *bytes = new unsigned char[size];

	for (int h = 0; h < rect.h; h++)
		for (int w = 0; w < rect.w; w++)
		{
			int c = (h * rect.w + w) * 4;
			bytes[c] = 0;
			bytes[c+1] = 0;
			bytes[c+2] = 0;
			bytes[c+3] = w % psize == 0 || h % psize == 0 ? 64 : 0;
		}

	mOverlay = SDL_CreateTexture(mRenderer, SDL_PIXELFORMAT_ARGB8888, SDL_TEXTUREACCESS_STATIC, rect.w, rect.h);
	SDL_SetTextureBlendMode(mOverlay, SDL_BLENDMODE_BLEND);
	SDL_UpdateTexture(mOverlay, NULL, bytes, rect.w * sizeof(unsigned char) * 4);
	delete[] bytes;
}

/******************************************************** Processing Function */
void init(Uint8 r = 0, Uint8 g = 0, Uint8 b = 0)
{
	if (source_width == 0 || source_height == 0)
	{
		cerr << "anbu: Neither width nor height parameters can be 0." << endl;
		exit(1);
	}

	if (SDL_Init(SDL_INIT_VIDEO | SDL_INIT_JOYSTICK) < 0 )
	{
		cerr << "Unable to initialize SDL" << endl;
		exit(1);
	}

	loadDisplayDimentions();

	// Clear screen and draw coloured Background
	SDL_CreateWindowAndRenderer(0, 0, SDL_WINDOW_FULLSCREEN_DESKTOP, &mWindow, &mRenderer);
	SDL_SetRenderDrawColor(mRenderer, r, g, b, 255);
	SDL_RenderClear(mRenderer);
	SDL_RenderPresent(mRenderer);

	// Set scaling properties
	SDL_SetHint(SDL_HINT_RENDER_SCALE_QUALITY, interpol.c_str());
	SDL_RenderSetLogicalSize(mRenderer, display_width, display_height);
}

#ifdef _WIN32
DWORD WINAPI startStreaming(LPVOID lpParam)
{
#else
void *startStreaming(void *args)
{
#endif
	SDL_Rect dest = getDestinationRect();

	loadBackground(bg_image);
	loadOverlay(dest);

	size_t pitch = source_width * sizeof(char) * BYTES;
	size_t num_chars = source_width * source_height * BYTES;
	unsigned char* frame = new unsigned char[num_chars];

	// Create a mTexture where drawing can take place. Streaming for constant updates.
	mTexture = SDL_CreateTexture(mRenderer, SDL_PIXELFORMAT_RGB24, SDL_TEXTUREACCESS_STREAMING, source_width, source_height);

	while (capturing && updateFrame(num_chars, frame) || !sendQuitEvent())
		drawFrame(frame, pitch, &dest, angle);

	SDL_DestroyTexture(mTexture);
	delete[] frame;

#ifdef _WIN32
	return 0;
#else
	pthread_exit(NULL);
#endif
}

void startCapturing()
{
	int key;
	SDL_JoystickEventState(SDL_ENABLE);

	while (capturing)
	{
		SDL_Event event;
		if (SDL_WaitEvent(&event))
		{
			switch (event.type)
			{
			case SDL_QUIT:
				capturing = false;
				continue;

			case SDL_KEYDOWN:
			case SDL_KEYUP:
				key = event.key.keysym.sym;
				if (key == SDLK_F4) {
					key = -1;
					capturing = false;
				}
				sendKey(key, event.key.state == SDL_PRESSED, false);
				break;

			case SDL_JOYBUTTONDOWN:
			case SDL_JOYBUTTONUP:
				key = event.jbutton.button;
				sendKey(key, event.jbutton.state == SDL_PRESSED, true);
				break;

			case SDL_JOYHATMOTION:
				key = event.jhat.value;
				sendKey(key << 16, event.jhat.value != SDL_HAT_CENTERED, true);
				break;

			case SDL_JOYAXISMOTION:
				// jaxis.value => -32768 to 32767
				int normValue;
				if(abs(event.jaxis.value) <= DEADZONE)
					normValue = 0;
				else
					if(event.jaxis.value > 0)
						normValue = 1;
					else
						normValue = -1;

				if(abs(normValue) != abs(mPrevAxisValues[event.jaxis.which][event.jaxis.axis]))
				{
					key = 3 * event.jaxis.axis + normValue + 1;
					sendKey(key << 8, normValue != 0, true);
				}
				mPrevAxisValues[event.jaxis.which][event.jaxis.axis] = normValue;
				break;

			case SDL_JOYDEVICEADDED:
				addJoystick(event.jdevice.which);
				break;

			case SDL_JOYDEVICEREMOVED:
				removeJoystick(event.jdevice.which);
				break;
			}
			fflush(stdout);
		}
	}
}

/*********************************************************************** Main */
int main(int argc, char* argv[])
{
	int c = 0;
	Uint8 r = 44, g = 62, b = 80; // Midnight Blue

	while (++c < argc)
	{
		if ( argc < 3 || string("--help") == argv[c] || string("-h") == argv[c]) {
			cout << getHelp() << endl;
			return 0;
		} else if (c == 1) {
			source_width = atoi(argv[c]);
			source_height = atoi(argv[++c]);
		} else if (c > 2 && string("-r") == argv[c] && argc > c + 1) {
			angle = atoi(argv[++c]) % 360;
		} else if (c > 2 && string("-i") == argv[c] && argc > c + 1) {
			interpol = argv[++c];
		} else if (c > 2 && string("-b") == argv[c] && argc > c + 1) {
			bg_image = argv[++c];
		} else if (c > 2 && string("-s") == argv[c] && argc > c + 1) {
			additional_scale = atof(argv[++c]);
		} else if (c > 2 && string("-c") == argv[c] && argc > c + 3) {
			r = atoi(argv[++c]);
			g = atoi(argv[++c]);
			b = atoi(argv[++c]);
		}
	}

	init(r, g, b);
	bool initialCursorState = SDL_ShowCursor(0) == 1;

#ifdef _WIN32
	HANDLE hThreadCapturing;
	if ((hThreadCapturing = CreateThread(NULL, 0, &startStreaming, NULL, 0, &t_capturing)) == NULL) {
		std::cerr << "Unable to start thread, exiting ..." << endl;
		SDL_Quit();
		return 1;
	}
#else
	if (pthread_create(&t_capturing, 0, &startStreaming, NULL))
	{
		std::cerr << "Unable to start thread, exiting ..." << endl;
		SDL_Quit();
		return 1;
	}
#endif

	startCapturing();
#ifdef _WIN32
    WaitForSingleObject(hThreadCapturing, INFINITE);
	CloseHandle(hThreadCapturing);
#else
	pthread_join(t_capturing, NULL);
#endif
	SDL_ShowCursor(initialCursorState);
	SDL_Quit();
	return 0;
}
