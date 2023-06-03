/*
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
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <signal.h>
#ifdef __linux__
#include <sys/types.h>
#include <sys/wait.h>
#elif _WIN32
#include <windows.h>
#endif
#include "freej2me_libretro.h"
#include <file/file_path.h>
#include <retro_miscellaneous.h>

#define DefaultFPS 30
#define MaxWidth 800
#define MaxHeight 800

/* Used as a limit to the string of core option updates to be sent to the Java app*/
#define PIPE_MAX_LEN 255

retro_environment_t Environ;
retro_video_refresh_t Video;
retro_audio_sample_t Audio;
retro_audio_sample_batch_t AudioBatch;
retro_input_poll_t InputPoll;
retro_input_state_t InputState;

static struct retro_log_callback logging;
static retro_log_printf_t log_fn;

void retro_set_video_refresh(retro_video_refresh_t fn) { Video = fn; }
void retro_set_audio_sample(retro_audio_sample_t fn) { Audio = fn; }
void retro_set_audio_sample_batch(retro_audio_sample_batch_t fn) { AudioBatch = fn;}

static void fallback_log(enum retro_log_level level, const char *fmt, ...)
{
   (void)level;
   va_list va;
   vfprintf(stderr, fmt, va);
}

void retro_set_environment_core_info(retro_environment_t fn)
{
	int core_opt_version = 0;

	/* Logging support */
	if (fn(RETRO_ENVIRONMENT_GET_LOG_INTERFACE, &logging)) { log_fn = logging.log; }
	else { log_fn = fallback_log; }

	/* Checks if the core options version is v2 or v1*/
	if (!Environ(RETRO_ENVIRONMENT_GET_CORE_OPTIONS_VERSION, &core_opt_version)) { core_opt_version = 0; }

	Environ(RETRO_ENVIRONMENT_SET_CONTROLLER_INFO, (void*)ports);
	Environ(RETRO_ENVIRONMENT_SET_INPUT_DESCRIPTORS, (void*)desc);

	if (core_opt_version >= 2) { Environ(RETRO_ENVIRONMENT_SET_CORE_OPTIONS_V2, &core_exposed_options); }
	else if (core_opt_version >= 1) { Environ(RETRO_ENVIRONMENT_SET_CORE_OPTIONS, (void*)core_options); }
	else { Environ(RETRO_ENVIRONMENT_SET_VARIABLES, (void*)vars); }
}

void retro_set_environment(retro_environment_t fn)
{
	Environ = fn;

	retro_set_environment_core_info(fn);
}

void retro_set_input_poll(retro_input_poll_t fn) { InputPoll = fn; }
void retro_set_input_state(retro_input_state_t fn) { InputState = fn; }

/* Global variables */
struct retro_game_geometry Geometry;

#ifdef __linux__
bool isRunning(pid_t pid);
pid_t javaOpen(char *cmd, char **params);
pid_t javaProcess;
int pRead[2];
int pWrite[2];
#elif _WIN32
BOOL succeeded = FALSE;
bool isRunning();
void javaOpen(char *cmd, char **params);
PROCESS_INFORMATION javaProcess;
STARTUPINFO startInfo;
HANDLE pRead[2];
HANDLE pWrite[2];
#endif

int joypad[14]; /* joypad state */
int joypre[14]; /* joypad previous state */
unsigned char joyevent[5] = { 0,0,0,0,0 };

int joymouseX = 0;
int joymouseY = 0;
long joymouseTime = 0; /* countdown to show/hide mouse cursor */
long joymouseClickedTime = 0; /* Countdown to show/hide the cursor in the clicked state */
bool joymouseAnalog = false; /* flag - using analog stick for mouse movement */
int mouseLpre = 0; /* old mouse button state */
bool uses_mouse = true;
bool uses_pointer = false;
bool booted = false;

unsigned int readSize = 16384;
unsigned char readBuffer[16384];

unsigned int frameWidth = 800;
unsigned int frameHeight = 800;
unsigned int frameSize = 640000;
unsigned int frameBufferSize = 1920000;
unsigned int frame[640000];
unsigned char frameBuffer[1920000];
unsigned char frameHeader[5];
struct retro_game_info gameinfo;

bool frameRequested = false;
int framesDropped = 0;

/* Libretro exposed config variables START */

char *options_update; /* String containing the options updated in check_variables() */
unsigned int optstrlen; /* length of the string above */
unsigned long int screenRes[2]; /* {width, height} */
int rotateScreen; /* acts as a boolean */
int phoneType; /* 0=standard, 1=nokia, 2=siemens, 3=motorola */
int gameFPS; /* Auto(0), 60, 30, 15 */
int soundEnabled; /* also acts as a boolean */
/* Variables used to manage the pointer speed when controlled from an analog stick */
int pointerXSpeed = 8;
int pointerYSpeed = 8;
/* Variables containing the on-screen pointer's colors */
unsigned int pointerInnerColor   = 0x000000;
unsigned int pointerOutlineColor = 0xFFFFFF;
unsigned int pointerClickedColor = 0xFFFF00;

/* Libretro exposed config variables END */

unsigned char javaRequestFrame[5] = { 0xF, 0, 0, 0, 0 };

/* mouse cursor image */
unsigned int joymouseImage[408] =
{
	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
	0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,0,0,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,0,0,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,0,0,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,1,1,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,2,2,1,1,1,0,0,0,0,
	0,0,0,0,1,2,2,1,2,2,1,2,2,1,1,0,0,
	0,0,0,0,1,2,2,1,2,2,1,2,2,1,2,1,0,
	1,1,1,0,1,2,2,1,2,2,1,2,2,1,2,2,1,
	1,2,2,1,1,2,2,2,2,2,2,2,2,1,2,2,1,
	1,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,1,
	0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,
	0,0,0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,
	0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0
};

/* mouse cursor clicked image */
unsigned int joymouseClickedImage[408] =
{
	0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,
	0,1,3,1,0,0,0,0,1,3,1,0,0,0,0,0,0,
	0,0,1,3,1,0,0,1,3,1,0,0,0,0,0,0,0,
	0,0,0,1,3,1,1,3,1,0,0,0,0,0,0,0,0,
	0,0,1,3,1,2,2,1,3,1,0,0,0,0,0,0,0,
	0,1,3,1,1,2,2,1,1,3,1,0,0,0,0,0,0,
	0,0,1,0,1,2,2,1,0,1,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,1,1,0,0,0,0,0,0,0,
	0,0,0,0,1,2,2,1,2,2,1,1,1,0,0,0,0,
	0,0,0,0,1,2,2,1,2,2,1,2,2,1,1,0,0,
	0,0,0,0,1,2,2,1,2,2,1,2,2,1,2,1,0,
	1,1,1,0,1,2,2,1,2,2,1,2,2,1,2,2,1,
	1,2,2,1,1,2,2,2,2,2,2,2,2,1,2,2,1,
	1,2,2,2,1,2,2,2,2,2,2,2,2,2,2,2,1,
	0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,2,1,
	0,0,0,1,2,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,1,2,2,2,2,2,2,2,2,2,2,1,0,
	0,0,0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,
	0,0,0,0,0,1,2,2,2,2,2,2,2,2,1,0,0,
	0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0
};

/*
 * Custom functions to read from, and write to, pipes.
 * Those functions are used to simplify the upcoming port
 * to WIN32, since all ifdefs will only need to be done here
 * instead of all around the core whenever a pipe write/read is
 * requested.
 */
#ifdef __linux__
void write_to_pipe(int pipe, void *data, int datasize)
{
	write(pipe, data, datasize);
#elif _WIN32
void write_to_pipe(void* pipe, void *data, int datasize)
{
	BOOL succeeded = FALSE;
	succeeded = WriteFile(
		pipe,               /* pipe handle */
		data,               /* message */
		datasize,           /* message length */
		NULL,               /* bytes written (not needed) */
		NULL);              /* not overlapped */
	if (!succeeded)
	{
		log_fn(RETRO_LOG_ERROR, "Failed to write to pipe. Error: %d!\n", GetLastError() );
		retro_deinit();
	}
#endif
}

#ifdef __linux__
int read_from_pipe(int pipe, void *data, int datasize)
{
	return read(pipe, data, datasize);
#elif _WIN32
int read_from_pipe(void* pipe, void *data, int datasize)
{
	BOOL succeeded = FALSE;
	/*
	 * BytesRead is basically a long unsigned int which is
	 * casted to int later in order to keep compatibility with
	 * the unix origins of this file. The amount of bytes read
	 * doesn't ever go beyond what is allowed on an int, so this
	 * cast can be considered safe.
	 */
	long unsigned int bytesRead = 0;

	succeeded = ReadFile( /* Same args as WriteFile */
		pipe,
		data,
		datasize,
		&bytesRead,
		NULL);
	if (!succeeded)
	{
		log_fn(RETRO_LOG_ERROR, "Failed to read from pipe. Error: %d!\n", GetLastError() );
		retro_deinit();
	}

	return (int) bytesRead;
#endif
}

/* Function to check the core's config states in the libretro frontend */
static void check_variables(bool first_time_startup)
{
   struct retro_variable var = {0};


   var.key = "freej2me_resolution";
   if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
   {
		char *resChar;
		char str[100];
		snprintf(str, sizeof(str), "%s", var.value);

		resChar = strtok(str, "x");
		if (resChar) { screenRes[0] = strtoul(resChar, NULL, 0); }
		resChar = strtok(NULL, "x");
		if (resChar) { screenRes[1] = strtoul(resChar, NULL, 0); }
   }


   var.key = "freej2me_rotate";
   if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
   {
		if (!strcmp(var.value, "off"))     { rotateScreen = 0; }
		else if (!strcmp(var.value, "on")) { rotateScreen = 1; }
   }


   var.key = "freej2me_phone";
   if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
   {
		if (!strcmp(var.value, "Standard"))      { phoneType = 0; }
		else if (!strcmp(var.value, "Nokia"))    { phoneType = 1; }
		else if (!strcmp(var.value, "Siemens"))  { phoneType = 2; }
		else if (!strcmp(var.value, "Motorola")) { phoneType = 3; }
   }


   var.key = "freej2me_fps";
   if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
   {
		if (!strcmp(var.value, "Auto"))    { gameFPS = 0;  }
		else if (!strcmp(var.value, "60")) { gameFPS = 60; }
		else if (!strcmp(var.value, "30")) { gameFPS = 30; }
		else if (!strcmp(var.value, "15")) { gameFPS = 15; }
   }


   var.key = "freej2me_sound";
   if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
   {
		if (!strcmp(var.value, "off"))     { soundEnabled = 0; }
		else if (!strcmp(var.value, "on")) { soundEnabled = 1; }
   }


   var.key = "freej2me_pointertype";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		if (!strcmp(var.value, "Mouse"))
		{
			uses_mouse = true;
			uses_pointer = false;
		}
		else if (!strcmp(var.value, "Touch"))
		{
			uses_mouse = false;
			uses_pointer = true;
		}
		else if (!strcmp(var.value, "None"))
		{
			uses_mouse = false;
			uses_pointer = false;
		}
	}


	var.key = "freej2me_pointerxspeed";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		pointerXSpeed = atoi(var.value);
	}


	var.key = "freej2me_pointeryspeed";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		pointerYSpeed = atoi(var.value);
	}


	var.key = "freej2me_pointerinnercolor";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		if (!strcmp(var.value, "White"))       { pointerInnerColor = 0xFFFFFF; }
		else if (!strcmp(var.value, "Red"))    { pointerInnerColor = 0xFF0000; }
		else if (!strcmp(var.value, "Green"))  { pointerInnerColor = 0x00FF00; }
		else if (!strcmp(var.value, "Blue"))   { pointerInnerColor = 0x0000FF; }
		else if (!strcmp(var.value, "Yellow")) { pointerInnerColor = 0xFFFF00; }
		else if (!strcmp(var.value, "Pink"))   { pointerInnerColor = 0xFF00FF; }
		else if (!strcmp(var.value, "Cyan"))   { pointerInnerColor = 0x00FFFF; }
		else if (!strcmp(var.value, "Black"))  { pointerInnerColor = 0x000000; }
	}


	var.key = "freej2me_pointeroutercolor";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		if (!strcmp(var.value, "White"))       { pointerOutlineColor = 0xFFFFFF; }
		else if (!strcmp(var.value, "Red"))    { pointerOutlineColor = 0xFF0000; }
		else if (!strcmp(var.value, "Green"))  { pointerOutlineColor = 0x00FF00; }
		else if (!strcmp(var.value, "Blue"))   { pointerOutlineColor = 0x0000FF; }
		else if (!strcmp(var.value, "Yellow")) { pointerOutlineColor = 0xFFFF00; }
		else if (!strcmp(var.value, "Pink"))   { pointerOutlineColor = 0xFF00FF; }
		else if (!strcmp(var.value, "Cyan"))   { pointerOutlineColor = 0x00FFFF; }
		else if (!strcmp(var.value, "Black"))  { pointerOutlineColor = 0x000000; }
	}


	var.key = "freej2me_pointerclickcolor";
	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE, &var) && var.value)
	{
		if (!strcmp(var.value, "White"))       { pointerClickedColor = 0xFFFFFF; }
		else if (!strcmp(var.value, "Red"))    { pointerClickedColor = 0xFF0000; }
		else if (!strcmp(var.value, "Green"))  { pointerClickedColor = 0x00FF00; }
		else if (!strcmp(var.value, "Blue"))   { pointerClickedColor = 0x0000FF; }
		else if (!strcmp(var.value, "Yellow")) { pointerClickedColor = 0xFFFF00; }
		else if (!strcmp(var.value, "Pink"))   { pointerClickedColor = 0xFF00FF; }
		else if (!strcmp(var.value, "Cyan"))   { pointerClickedColor = 0x00FFFF; }
		else if (!strcmp(var.value, "Black"))  { pointerClickedColor = 0x000000; }
	}


	/* Prepare a string to pass those core options to the Java app */
	snprintf(options_update, PIPE_MAX_LEN, "FJ2ME_LR_OPTS:|%lux%lu|%d|%d|%d|%d", screenRes[0], screenRes[1], rotateScreen, phoneType, gameFPS, soundEnabled);
	optstrlen = strlen(options_update);

	/* 0xD = 13, which is the special case where the java app will receive the updated configs */
	unsigned char optupdateevent[5] = { 0xD, (optstrlen>>24)&0xFF, (optstrlen>>16)&0xFF, (optstrlen>>8)&0xFF, optstrlen&0xFF };

	/* Sends the event to set Java in core options read mode, then send the string containing those options */
	if(booted) /* Checks if the java app booted first, or else it'll fail to write to pipes as they don't exist yet. */
	{
		write_to_pipe(pWrite[1], optupdateevent, 5);
		write_to_pipe(pWrite[1], options_update, optstrlen);
		log_fn(RETRO_LOG_INFO, "Sent updated options to the Java app.\n");
	}
}

/* Core exit function */
#ifdef __linux__
void quit(int state)
{
	if(isRunning(javaProcess))
	{
		kill(javaProcess, SIGKILL);
	}
	/* exit(state); */
}

#elif _WIN32
void quit(int state)
{
	if(isRunning())
	{
		TerminateProcess(javaProcess.hProcess, state);
	}
}
#endif

static void Keyboard(bool down, unsigned keycode, uint32_t character, uint16_t key_modifiers)
{
	unsigned char event[5] = {down, (keycode>>24)&0xFF, (keycode>>16)&0xFF, (keycode>>8)&0xFF, keycode&0xFF };

	write_to_pipe(pWrite[1], event, 5);
}

void retro_init(void)
{
	/* init buffers, structs */
	memset(frame, 0, frameSize);
	memset(frameBuffer, 0, frameBufferSize);
	options_update = malloc(sizeof(char) * PIPE_MAX_LEN);

	/*
	 * Those below are arguments sent to Java during init. Otherwise, games
	 * wouldn't get a res setting, rotation, fps, etc. that matched the
	 * frontend's core setting in cases where a game without a matching .conf
	 * file was loaded, prompting FreeJ2ME to create a new config, but still
	 * defaulting to the built-in config values, which are 240x320, rotation
	 * off, etc.
	 */
	check_variables(true);

	char resArg[2][4], rotateArg[2], phoneArg[2], fpsArg[3], soundArg[2];
	sprintf(resArg[0], "%lu", screenRes[0]); /* Libretro config Width  */
	sprintf(resArg[1], "%lu", screenRes[1]); /* Libretro config Height */
	sprintf(rotateArg, "%d",  rotateScreen);
	sprintf(phoneArg,  "%d",  phoneType);
	sprintf(fpsArg,    "%d",  gameFPS);
	sprintf(soundArg,  "%d",  soundEnabled);

	/* start java process */
	char *javapath;
	Environ(RETRO_ENVIRONMENT_GET_SYSTEM_DIRECTORY, &javapath);
	char *outPath = malloc(sizeof(char) * PATH_MAX_LENGTH);
	fill_pathname_join(outPath, javapath, "freej2me-lr.jar", PATH_MAX_LENGTH);
	char *params[] = { "java", "-jar", outPath, resArg[0], resArg[1], rotateArg, phoneArg, fpsArg, soundArg, NULL };

	log_fn(RETRO_LOG_INFO, "Passing params: %s | %s | %s | %s | %s | %s \n", *(params+3),
		*(params+4), *(params+5), *(params+6), *(params+7), *(params+8));
	log_fn(RETRO_LOG_INFO, "Preparing to open FreeJ2ME's Java app (make sure freej2me-lr.jar is inside system/).\n");

#ifdef __linux__
	javaProcess = javaOpen(params[0], params);
#elif _WIN32
	javaOpen(params[2], params);
#endif

	/* wait for java process */
	int t = 0;
	int status = 0;
#ifdef __linux__
	while(status<1 && isRunning(javaProcess))
	{
		status = read_from_pipe(pRead[0], &t, 1);
		if(status<0 && errno != EAGAIN) { quit(EXIT_FAILURE); }
	}
	if(!isRunning(javaProcess)) { quit(EXIT_FAILURE); }
#elif _WIN32
	while(status<1 && isRunning())
	{
		log_fn(RETRO_LOG_INFO, "Reading status...\n");
		status = read_from_pipe(pRead[0], &t, 1);
		log_fn(RETRO_LOG_INFO, "Status read! It's %d\n", status);
		if(status<0) { quit(EXIT_FAILURE); }
	}
	if(!isRunning()) { quit(EXIT_FAILURE); }
#endif

	/* Setup keyboard input */
	struct retro_keyboard_callback kb = { Keyboard };
	Environ(RETRO_ENVIRONMENT_SET_KEYBOARD_CALLBACK, &kb);
	log_fn(RETRO_LOG_INFO, "All preparations done and java app is ready. Keyboard callback set.\n");
}

bool retro_load_game(const struct retro_game_info *info)
{
	int len = 0;

	/* Game info is passed to a global variable to enable restarts */
	gameinfo = *info;
	/* Send savepath to java */
	char *savepath;
	Environ(RETRO_ENVIRONMENT_GET_SAVE_DIRECTORY, &savepath);
	len = strlen(savepath);

	if(len==0)
	{
		Environ(RETRO_ENVIRONMENT_GET_SYSTEM_DIRECTORY, &savepath);
		len = strlen(savepath);
	}

	len += 10;

	unsigned char saveevent[5] = { 0xB, (len>>24)&0xFF, (len>>16)&0xFF, (len>>8)&0xFF, len&0xFF };
	write_to_pipe(pWrite[1], saveevent, 5);
	write_to_pipe(pWrite[1], savepath, len-10);
	write_to_pipe(pWrite[1], "/freej2me/", 10);

	/* Tell java app to load and run game */
	len = strlen(info->path);

	unsigned char loadevent[5] = { 0xA, (len>>24)&0xFF, (len>>16)&0xFF, (len>>8)&0xFF, len&0xFF };
	write_to_pipe(pWrite[1], loadevent, 5);
	write_to_pipe(pWrite[1], (unsigned char*) info->path, len);

	log_fn(RETRO_LOG_INFO, "Sent game file and save paths to Java app.\n");

	return true;
}

void retro_unload_game(void)
{
	/* Quit */
	quit(0);
}

void retro_run(void)
{
	int i = 0;
	int j = 0;
	int t = 0; /* temp */
	int w = 0; /* sent frame width */
	int h = 0; /* sent frame height */
	int r = 0; /* rotation flag */
	int stat = 0;
	int status = 0;
	bool mouseChange = false;
	bool updated_vars = false; /* Used to check if the core's variables were updated */

	if (Environ(RETRO_ENVIRONMENT_GET_VARIABLE_UPDATE, &updated_vars) && updated_vars)
		check_variables(false);

	if(isRunning(javaProcess))
	{
		/* request frame */
		if(!frameRequested)
		{
			write_to_pipe(pWrite[1], javaRequestFrame, 5);
			frameRequested = true;
		}

		/* handle joypad */
		for(i=0; i<14; i++)
		{
			joypre[i] = joypad[i];
		}

		InputPoll();

		joypad[0] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_UP);
		joypad[1] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_DOWN);
		joypad[2] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_LEFT);
		joypad[3] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_RIGHT);

		joypad[4] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_A);
		joypad[5] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_B);
		joypad[6] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_X);
		joypad[7] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_Y);

		joypad[8] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_START);
		joypad[9] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_SELECT);

		joypad[10] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_L);
		joypad[11] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_R);
		joypad[12] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_L2);
		joypad[13] = InputState(0, RETRO_DEVICE_JOYPAD, 0, RETRO_DEVICE_ID_JOYPAD_R2);

		/* Right analog will control the pointer, freeing the left analog to mirror the D-Pad if needed. */
		int joyRx = InputState(0, RETRO_DEVICE_ANALOG, RETRO_DEVICE_INDEX_ANALOG_RIGHT, RETRO_DEVICE_ID_ANALOG_X);
		int joyRy = InputState(0, RETRO_DEVICE_ANALOG, RETRO_DEVICE_INDEX_ANALOG_RIGHT, RETRO_DEVICE_ID_ANALOG_Y);

		int mouseX = InputState(0, RETRO_DEVICE_MOUSE, 0, RETRO_DEVICE_ID_MOUSE_X);
		int mouseY = InputState(0, RETRO_DEVICE_MOUSE, 0, RETRO_DEVICE_ID_MOUSE_Y);
		int mouseL = InputState(0, RETRO_DEVICE_MOUSE, 0, RETRO_DEVICE_ID_MOUSE_LEFT);

		int touchX = InputState(0, RETRO_DEVICE_POINTER, 0, RETRO_DEVICE_ID_POINTER_X);
		int touchY = InputState(0, RETRO_DEVICE_POINTER, 0, RETRO_DEVICE_ID_POINTER_Y);
		int touchP = InputState(0, RETRO_DEVICE_POINTER, 0, RETRO_DEVICE_ID_POINTER_PRESSED);


		/* analog right - move joymouse. XSpeed and YSpeed are multipliers set through the frontend */
		joyRx /= 32768 / pointerXSpeed;
		joyRy /= 32768 / pointerYSpeed;
		if(joyRx != 0 || joyRy !=0)
		{
			joymouseAnalog = true;
			joymouseTime = DefaultFPS;
			joymouseX += joyRx<<1;
			joymouseY += joyRy<<1;

			mouseChange = true;
		}

		if(joymouseX<0) { joymouseX=0; }
		if(joymouseY<0) { joymouseY=0; }
		if(joymouseX>frameWidth-17) { joymouseX=frameWidth-17; }
		if(joymouseY>frameHeight)   { joymouseY=frameHeight; }

		if(uses_mouse)
		{
			/* mouse - move joymouse */
			if(mouseX != 0 || mouseY !=0)
			{
				joymouseAnalog = false;
				joymouseTime = DefaultFPS;
				joymouseX += mouseX;
				joymouseY += mouseY;

				mouseChange = true;
			}
			/* mouse - drag */
			if(mouseL>0 && mouseChange)
			{
				joyevent[0] = 6;
				joyevent[1] = (joymouseX >> 8) & 0xFF;
				joyevent[2] = (joymouseX) & 0xFF;
				joyevent[3] = (joymouseY >> 8) & 0xFF;
				joyevent[4] = (joymouseY) & 0xFF;
				write_to_pipe(pWrite[1], joyevent, 5);
			}

			/* mouse - down/up */
			if(mouseLpre != mouseL)
			{
				joymouseClickedTime = DefaultFPS *0.1;
				joyevent[0] = 4 + mouseL;
				joyevent[1] = (joymouseX >> 8) & 0xFF;
				joyevent[2] = (joymouseX) & 0xFF;
				joyevent[3] = (joymouseY >> 8) & 0xFF;
				joyevent[4] = (joymouseY) & 0xFF;
				write_to_pipe(pWrite[1], joyevent, 5);
			}
			mouseLpre = mouseL;
		}


		/* touch event */
		else if(uses_pointer)
		{
			if(touchP!=0)
			{
				touchX = (int)(((float)(touchX + 0x7FFF)) * ((float)frameWidth / (float)0xFFFE));
				touchY = (int)(((float)(touchY + 0x7FFF)) * ((float)frameHeight / (float)0xFFFE));
				joymouseAnalog = false;
				joyevent[0] = 5; /* touch down */
				joyevent[1] = (touchX >> 8) & 0xFF;
				joyevent[2] = (touchX) & 0xFF;
				joyevent[3] = (touchY >> 8) & 0xFF;
				joyevent[4] = (touchY) & 0xFF;
				write_to_pipe(pWrite[1], joyevent, 5);
				joyevent[0] = 4; /* touch up */
				write_to_pipe(pWrite[1], joyevent, 5);
			}
		}


		for(i=0; i<14; i++)
		{
			/* joypad - spot the difference, send corresponding keyup/keydown events */
			if(joypad[i]!=joypre[i])
			{
				if(i==7 && joymouseTime>0 && joymouseAnalog)
				{
					/* when mouse is visible, and using analog stick for mouse, Y / [5] clicks */
					joymouseClickedTime = DefaultFPS * 0.1;
					joymouseTime = DefaultFPS;
					joyevent[0] = 4+joypad[7];
					joyevent[1] = (joymouseX >> 8) & 0xFF;
					joyevent[2] = (joymouseX) & 0xFF;
					joyevent[3] = (joymouseY >> 8) & 0xFF;
					joyevent[4] = (joymouseY) & 0xFF;
					write_to_pipe(pWrite[1], joyevent, 5);
				}
				else
				{
					joyevent[0] = 2+joypad[i];
					joyevent[1] = 0;
					joyevent[2] = 0;
					joyevent[3] = 0;
					joyevent[4] = i;
					write_to_pipe(pWrite[1], joyevent, 5);
				}
			}

			/*
			 * With the libretro core's menu settings now working, this isn't as useful,
			 * and also froze the frontend on a restart. Also just isn't as intuitive to use, at least to me...
			if(joypad[8]+joypad[10]+joypad[11]==3)
			{
				// start+L+R = ESC
				unsigned char event[5] = { 1, 0,0,0,27 };
				write_to_pipe(pWrite[1], event, 5);
			}
			 */
		}

		/*
		 * grab frame
		 * some jars are noisy
		 * wait for start of frame marker 0xFE
		 */
		i=0;
		while(t!=0xFE && isRunning(javaProcess))
		{
			i++;
			status = read_from_pipe(pRead[0], &t, 1);

			if(i>255 && t!=0xFE)
			{
				/* drop frame */
				framesDropped++;
				if(framesDropped>250)
				{
					log_fn(RETRO_LOG_ERROR, "More than 250 frames dropped. Exiting!\n");
					quit(EXIT_FAILURE);
				}
				Video(frame, frameWidth, frameHeight, sizeof(unsigned int) * frameWidth);
				return;
			}
			if(status<0 && errno != EAGAIN)
			{
				log_fn(RETRO_LOG_ERROR, "Serious Error!\n");
				fflush(stdout);
				quit(EXIT_FAILURE);
			}
			/*
			if(t!=0xFE)
			{
				if((t<128 && t>31)||t==10) { printf("%c", t); }
				else { printf("%u", t); }
			}
			 */
		}

		/* read frame header */
		frameRequested = false;
		framesDropped = 0;

		status = read_from_pipe(pRead[0], frameHeader, 5);

		if(status>0)
		{
			w = (frameHeader[0]<<8) | (frameHeader[1]);
			h = (frameHeader[2]<<8) | (frameHeader[3]);
			r = (frameHeader[4]);
			if(r!=0)
			{
				t = w;
				w = h;
				h = t;
			}
			if(frameWidth!=w || frameHeight!=h)
			{
				frameWidth = w;
				frameHeight = h;
				frameSize = w * h;
				frameBufferSize = frameSize * 3;

				/* update geometry */
				Geometry.base_width = w;
				Geometry.base_height = h;
				Geometry.max_width = MaxWidth;
				Geometry.max_height = MaxHeight;
				Geometry.aspect_ratio = ((float)w / (float)h);
				Environ(RETRO_ENVIRONMENT_SET_GEOMETRY, &Geometry);
			}
		}

		/* read frame */
		status = 0;
		do
		{
			stat = read_from_pipe(pRead[0], readBuffer, readSize);
			if (stat<=0) break;
			for(i=0; i<stat; i++)
			{
				frameBuffer[status + i] = readBuffer[i];
			}
			status += stat;
		} while(status > 0 && status < frameBufferSize);

		if(status>0)
		{
			if(r==0)
			{
				/* copy frameBuffer to frame */
				t = 0;
				for(i=0; i<frameSize; i++)
				{
					frame[i] = (frameBuffer[t]<<16) | (frameBuffer[t+1]<<8) | (frameBuffer[t+2]);
					t+=3;
				}
			}
			else
			{
				/* copy frameBuffer to frame rotated 90 degrees anticlockwise */
				t = 0;
				for(j=0; j<frameWidth; j++)
				{
					for(i=frameHeight-1; i>=0; i--)
					{
						frame[(i*frameWidth)+j] = (frameBuffer[t]<<16) | (frameBuffer[t+1]<<8) | (frameBuffer[t+2]);
						t+=3;
					}
				}
			}
		}

		/* draw pointer */
		/* check if the clicked pointer needs to be shown */
		if(joymouseClickedTime>0)
		{
			joymouseClickedTime--;

			for(i=0; i<24; i++)
			{
				for (j=0; j<17; j++)
				{
					t = ((joymouseY + i)*frameWidth)+(joymouseX + j);
					if(t>=0 && t<sizeof(frame))
					{
						switch (joymouseClickedImage[(i*17)+j])
						{
							case 1: frame[t] = pointerOutlineColor; break;
							case 2: frame[t] = pointerInnerColor; break;
							case 3: frame[t] = pointerClickedColor; break;
						}
					}
				}
			}
		}

		/* Otherwise, draw the standard pointer */
		else if(joymouseTime>0)
		{
			joymouseTime--;

			for(i=0; i<24; i++)
			{
				for (j=0; j<17; j++)
				{
					t = ((joymouseY + i)*frameWidth)+(joymouseX + j);
					if(t>=0 && t<sizeof(frame))
					{
						switch (joymouseImage[(i*17)+j])
						{
							case 1: frame[t] = pointerOutlineColor; break;
							case 2: frame[t] = pointerInnerColor; break;
						}
					}
				}
			}
		}

		/* send frame to libretro */
		Video(frame, frameWidth, frameHeight, sizeof(unsigned int) * frameWidth);
	}
	else
	{
		retro_deinit();
	}
}

unsigned retro_get_region(void)
{
	return RETRO_REGION_NTSC;
}

void retro_get_system_info(struct retro_system_info *info)
{
	memset(info, 0, sizeof(*info));
	info->library_name = "FreeJ2ME";
	info->library_version = "1.2";
	info->valid_extensions = "jar";
	info->need_fullpath = true;
}

void retro_get_system_av_info(struct retro_system_av_info *info)
{
	memset(info, 0, sizeof(*info));
	info->geometry.base_width   = MaxWidth;
	info->geometry.base_height  = MaxHeight;
	info->geometry.max_width    = MaxWidth;
	info->geometry.max_height   = MaxHeight;
	info->geometry.aspect_ratio = ((float)MaxWidth) / ((float)MaxHeight);

	info->timing.fps = DefaultFPS;
	int pixelformat = RETRO_PIXEL_FORMAT_XRGB8888;
	Environ(RETRO_ENVIRONMENT_SET_PIXEL_FORMAT, &pixelformat);
}


void retro_deinit(void)
{
	quit(0);
}

void retro_reset(void)
{
	retro_deinit();
	retro_init();
	retro_load_game(&gameinfo);
}

/* Stubs */
unsigned int retro_api_version(void) { return RETRO_API_VERSION; }
void *retro_get_memory_data(unsigned id) { return NULL; }
size_t retro_get_memory_size(unsigned id){ return 0; }
size_t retro_serialize_size(void) { return 0; }
bool retro_serialize(void *data, size_t size) { return false; }
bool retro_unserialize(const void *data, size_t size) { return false; }
void retro_cheat_reset(void) {  }
void retro_cheat_set(unsigned index, bool enabled, const char *code) {  }
bool retro_load_game_special(unsigned game_type, const struct retro_game_info *info, size_t num_info) { return false; }
void retro_set_controller_port_device(unsigned port, unsigned device) {  }


/* Java Process */
#ifdef __linux__
pid_t javaOpen(char *cmd, char **params)
{
    pid_t pid;

	log_fn(RETRO_LOG_INFO, "Setting up java app's process and pipes...\n");

	log_fn(RETRO_LOG_INFO, "Opening: %s %s %s ...\n", *(params+0), *(params+1), *(params+2));
	log_fn(RETRO_LOG_INFO, "Params: %s | %s | %s | %s | %s | %s \n", *(params+3),
		*(params+4), *(params+5), *(params+6), *(params+7), *(params+8));

	int fd_stdin  = 0;
	int fd_stdout = 1;

	/*
	 * parent <-- 0 --  pRead  <-- 1 --  child
	 * parent  -- 1 --> pWrite  -- 0 --> child
	 */

 	pipe(pRead); /* 0: pRead, 1: pWrite */
	pipe(pWrite);

	pid = fork();

	if(pid==0) /* child */
	{

		dup2(pWrite[0], fd_stdin);  /* read from parent pWrite */
		dup2(pRead[1], fd_stdout);  /* write to parent pRead */

		close(pWrite[1]);
		close(pRead[0]);

		execvp(cmd, params);

		/* execvp failure! */
		retro_deinit();
	}

	if(pid>0) /* parent */
	{
		close(pRead[1]);
		close(pWrite[0]);
	}

	if(pid<0) /* error */
	{
		log_fn(RETRO_LOG_ERROR, "Couldn't create child process!\n");
		quit(EXIT_FAILURE);
	}

	booted = true;
	log_fn(RETRO_LOG_INFO, "Core and Java app started! Initializing game data... \n");
	return pid;
}

bool isRunning(pid_t pid)
{
	int status;

	if(waitpid(pid, &status, WNOHANG) == 0) { return true; }

	log_fn(RETRO_LOG_INFO, "Java app is not running anymore! Last known PID=%d \n", pid);
	return false;
}
#elif _WIN32
void javaOpen(char *cmd, char **params)
{
	SECURITY_ATTRIBUTES pipeSec;

	log_fn(RETRO_LOG_INFO, "Setting up java app's process and pipes...\n");

	ZeroMemory( &pipeSec, sizeof(pipeSec) );
	ZeroMemory( &startInfo, sizeof(startInfo) );
	ZeroMemory( &javaProcess, sizeof(javaProcess) );

	startInfo.cb = sizeof(SECURITY_ATTRIBUTES);
	pipeSec.nLength = sizeof(LPSECURITY_ATTRIBUTES);
	pipeSec.lpSecurityDescriptor = NULL;
	pipeSec.bInheritHandle = TRUE; /* Needed for IPC between java app and this core */

	log_fn(RETRO_LOG_INFO, "Creating pipes...\n");

	/* read from parent pWrite */
	if(CreatePipe(&pWrite[0], &pWrite[1], &pipeSec, 0))
	{
		if(!DuplicateHandle(
			GetCurrentProcess(),
			pWrite[0],
			GetCurrentProcess(),
			NULL,
			0,
			FALSE,
			DUPLICATE_SAME_ACCESS))
		{
			log_fn(RETRO_LOG_INFO, "Failed to create pWrite pipe... \n");
			CloseHandle(pWrite[0]);
			CloseHandle(pWrite[1]);
			retro_deinit();
		}
	}

	/* write to parent pRead */
	if(CreatePipe(&pRead[0], &pRead[1], &pipeSec, 0))
	{
		if(!DuplicateHandle(
			GetCurrentProcess(),
			pRead[1],
			GetCurrentProcess(),
			NULL,
			0,
			FALSE,
			DUPLICATE_SAME_ACCESS))
		{
			log_fn(RETRO_LOG_INFO, "Failed to create pRead pipe... \n");
			CloseHandle(pRead[0]);
			CloseHandle(pRead[1]);
			retro_deinit();
		}
	}

	log_fn(RETRO_LOG_INFO, "Created pipes! \n");

	log_fn(RETRO_LOG_INFO, "Trying to create process... \n");
	log_fn(RETRO_LOG_INFO, "Process name: %s \n", cmd);

	/* Try starting the child process. */
	char cmdWin[PATH_MAX_LENGTH];
	/* resArg[0], resArg[1], rotateArg, phoneArg, fpsArg, soundArg */
	sprintf(cmdWin, "javaw -jar %s", cmd);

	log_fn(RETRO_LOG_INFO, "Opening: %s \n", cmd);
	for (int i = 3; i <= 8; i++) /* There are 8 cmd arguments for now */
	{
		log_fn(RETRO_LOG_INFO, "Processing arg %d: %s \n", i, *(params+i));
		sprintf(cmdWin, "%s %s", cmdWin, *(params+i));
	}

	log_fn(RETRO_LOG_INFO, "Creating proc: %s \n", cmdWin);
	log_fn(RETRO_LOG_INFO, "Params: %s | %s | %s | %s | %s | %s \n", *(params+3),
		*(params+4), *(params+5), *(params+6), *(params+7), *(params+8));

	GetStartupInfo(&startInfo);
	startInfo.dwFlags = STARTF_USESTDHANDLES;
	startInfo.hStdInput = pWrite[0];
	startInfo.hStdOutput = pRead[1];
	startInfo.hStdError = GetStdHandle(STD_ERROR_HANDLE);

	if(!CreateProcess( NULL, /* Module name */
		cmdWin,              /*Command line args */
		NULL,                /* Process handle not inheritable */
		NULL,                /* Thread handle not inheritable */
		TRUE,                /* Set handle inheritance to TRUE */
		0,                   /* No creation flags */
		NULL,                /* Use parent's environment block */
		NULL,                /* Use parent's starting directory */
		&startInfo,          /* Pointer to STARTUPINFO structure */
		&javaProcess ))       /*Pointer to PROCESS_INFORMATION structure */
	{ /* If it fails, this block is executed */
		log_fn(RETRO_LOG_ERROR, "Couldn't create process, error: %lu\n", GetLastError() );
		retro_deinit();
	}

	booted = true;
	log_fn(RETRO_LOG_INFO, "Created process! PID=%d \n", javaProcess.dwProcessId);
	log_fn(RETRO_LOG_INFO, "Core and Java app started! Initializing game data... \n");
}

bool isRunning()
{
	/*
		* On win32, receiving a WAIT_TIMEOUT signal before timing out means the process is
		* still running and didn't face any issues that caused it to lock up or crash.
		*/
	if(WaitForSingleObject(javaProcess.hProcess, 0) == WAIT_TIMEOUT) { return true; }

	log_fn(RETRO_LOG_INFO, "Java app is not running anymore! Last known PID=%d \n", javaProcess.dwProcessId);
	return false;
}
#endif
