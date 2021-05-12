# freej2me

![Java CI](https://github.com/hex007/freej2me/workflows/Java%20CI/badge.svg)

A free J2ME emulator with libretro, awt and sdl2 frontends.

Authors :
- David Richardson [Recompile@retropie]
- Saket Dandawate  [Hex@retropie]

---

## Controls

* `Q` and `W` for `soft1` and `soft2`.
* Arrow keys for nav, unless you turn nokia off, when arrow keys become 2, 4, 6, and 8.
* Numbers work as expected, the number pad is inverted (123 swap with 789, like a phone) ESC brings up the menu.
* `E` and `R` are alternatives to `*` and `#`.
* Enter functions as `5`

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