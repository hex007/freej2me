# freej2me

![Java CI](https://github.com/hex007/freej2me/workflows/Java%20CI/badge.svg)

A free J2ME emulator with libretro, awt and sdl2 frontends.

Authors :
- David Richardson [Recompile@retropie]
- Saket Dandawate  [Hex@retropie]

---

## Controls

* `Q` and `W` for left and right softkeys.
* Arrow keys for nav, unless phone is set to "Standard", when arrow keys become 2, 4, 6, and 8.
* Numbers work as expected, the number pad is inverted (123 swap with 789, like a phone)
* `E` and `R` are alternatives to `*` and `#`.
* Enter functions as the Fire key or `5` on "Standard" mode
* ESC brings up the settings menu
* In the AWT frontend (freej2me.jar) `Ctrl+C` takes a screenshot and `+`/`-` can be used to control the window scaling factor

Click [here](KEYMAP.md) for information about more keybindings

## Links
Screenshots:
  https://imgur.com/a/2vAeC

Discussion/Support thread:
  https://retropie.org.uk/forum/topic/13084/freej2me-support-thread

Development thread:
  https://retropie.org.uk/forum/topic/11441/would-you-like-to-play-nokia-j2me-games-on-retropie/

----
**Compilation:**
```
> cd freej2me/
> ant

# SDL2 binary compilation
> cd src/sdl2
> make
> make install
```
Will create jar files for each frontend. SDL2 jar file needs SDL binary to be compiled. SDL2 can be used to play on Raspberry pi.

----
**Usage:**

Launching the AWT frontend (freej2me.jar) will bring up a filepicker to select the MIDlet to run.

Alternatively it can be launched from the command line: `java -jar freej2me.jar 'file:///path/to/midlet.jar' [width] [height] [scale]`
Where _width_, _height_ (dimensions of the simulated screen) and _scale_ (initial scale factor of the window) are optional arguments.

The SDL2 frontend (freej2me-sdl.jar) accepts the same command-line arguments format, aside from the _scale_ option which is unavailable.

When running under Microsoft Windows please do note paths require an additional `/` prefixed. For example, `C:\path\to\midlet.jar` should be passed as `file:///C:\path\to\midlet.jar`

FreeJ2ME keeps savedata and config at the working directory it is run from. Currently any resolution specified at the config file takes precedence over the values passed via command-line.

---
**How to contribute as a developer:**
  1) Open an Issue
  2) Try solving that issue
  3) Post on the Issue if you have a possible solution
  4) Submit a PR implementing the solution

**If you are not a developer:**
  1) Post on discussion thread only

**Roadmap:**
  - Get as many games as possible to work well.
  - Document games that work well in the wiki
  - Reduce as many bugs as possible
