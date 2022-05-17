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
package org.recompile.mobile;

import java.io.InputStream;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Canvas;
import javax.microedition.m3g.Graphics3D;

/*

	Mobile

	Provides MobilePlatform access to mobile app

*/

public class Mobile
{
	private static MobilePlatform platform;

	private static Display display;

	private static Graphics3D graphics3d;

	public static boolean quiet = false; 

	public static boolean nokia = false;

	public static boolean siemens = false;

	public static boolean motorola = false;

	public static boolean sound = true;

	//Standard keycodes
	public static final int KEY_NUM0  = Canvas.KEY_NUM0;  // 48
	public static final int KEY_NUM1  = Canvas.KEY_NUM1;  // 49
	public static final int KEY_NUM2  = Canvas.KEY_NUM2;  // 50
	public static final int KEY_NUM3  = Canvas.KEY_NUM3;  // 51
	public static final int KEY_NUM4  = Canvas.KEY_NUM4;  // 52
	public static final int KEY_NUM5  = Canvas.KEY_NUM5;  // 53
	public static final int KEY_NUM6  = Canvas.KEY_NUM6;  // 54
	public static final int KEY_NUM7  = Canvas.KEY_NUM7;  // 55
	public static final int KEY_NUM8  = Canvas.KEY_NUM8;  // 56
	public static final int KEY_NUM9  = Canvas.KEY_NUM9;  // 57
	public static final int KEY_STAR  = Canvas.KEY_STAR;  // 42
	public static final int KEY_POUND = Canvas.KEY_POUND; // 35
	public static final int GAME_UP   = Canvas.UP;     // 1
	public static final int GAME_DOWN = Canvas.DOWN;   // 6
	public static final int GAME_LEFT = Canvas.LEFT;   // 2 
	public static final int GAME_RIGHT= Canvas.RIGHT;  // 5 
	public static final int GAME_FIRE = Canvas.FIRE;   // 8
	public static final int GAME_A    = Canvas.GAME_A; // 9
	public static final int GAME_B    = Canvas.GAME_B; // 10
	public static final int GAME_C    = Canvas.GAME_C; // 11
	public static final int GAME_D    = Canvas.GAME_D; // 12

	//Nokia-specific keycodes
	public static final int NOKIA_UP    = -1; // KEY_UP_ARROW = -1;
	public static final int NOKIA_DOWN  = -2; // KEY_DOWN_ARROW = -2;
	public static final int NOKIA_LEFT  = -3; // KEY_LEFT_ARROW = -3;
	public static final int NOKIA_RIGHT = -4; // KEY_RIGHT_ARROW = -4;
	public static final int NOKIA_SOFT1 = -6; // KEY_SOFTKEY1 = -6;
	public static final int NOKIA_SOFT2 = -7; // KEY_SOFTKEY2 = -7;
	public static final int NOKIA_SOFT3 = -5; // KEY_SOFTKEY3 = -5;
	public static final int NOKIA_END   = -11; // KEY_END = -11;
	public static final int NOKIA_SEND  = -10; // KEY_SEND = -10;

	//Siemens-specific keycodes
	public static final int SIEMENS_UP    = -59;
	public static final int SIEMENS_DOWN  = -60;
	public static final int SIEMENS_LEFT  = -61;
	public static final int SIEMENS_RIGHT = -62;
	public static final int SIEMENS_SOFT1 = -1; 
	public static final int SIEMENS_SOFT2 = -4; 
	public static final int SIEMENS_FIRE = -26; 

	//Motorola-specific keycodes
	public static final int MOTOROLA_UP    = -1;
	public static final int MOTOROLA_DOWN  = -6;
	public static final int MOTOROLA_LEFT  = -2;
	public static final int MOTOROLA_RIGHT = -5;
	public static final int MOTOROLA_SOFT1 = -21; 
	public static final int MOTOROLA_SOFT2 = -22; 
	public static final int MOTOROLA_FIRE = -20; 

	public static MobilePlatform getPlatform()
	{
		return platform;
	}

	public static void setPlatform(MobilePlatform p)
	{
		platform = p;
	}

	public static Display getDisplay()
	{
		return display;
	}

	public static void setDisplay(Display d)
	{
		display = d;
	}

	public static Graphics3D getGraphics3D()
	{
		return graphics3d;
	}

	public static void setGraphics3D(Graphics3D g)
	{
		graphics3d = g;
	}

	public static InputStream getResourceAsStream(Class c, String resource)
	{
		return platform.loader.getMIDletResourceAsStream(resource);
	}

	public static InputStream getMIDletResourceAsStream(String resource)
	{
		return platform.loader.getMIDletResourceAsStream(resource);
	}

	public static void log(String text)
	{
		if(!quiet)
		{
			System.out.println(text);
		}
	}
}
