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
package javax.microedition.lcdui;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;

public abstract class Canvas extends Displayable
{
	public static final int UP = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 5;
	public static final int DOWN = 6;
	public static final int FIRE = 8;

	public static final int GAME_A = 9;
	public static final int GAME_B = 10;
	public static final int GAME_C = 11;
	public static final int GAME_D = 12;

	public static final int KEY_NUM0 = 48;
	public static final int KEY_NUM1 = 49;
	public static final int KEY_NUM2 = 50;
	public static final int KEY_NUM3 = 51;
	public static final int KEY_NUM4 = 52;
	public static final int KEY_NUM5 = 53;
	public static final int KEY_NUM6 = 54;
	public static final int KEY_NUM7 = 55;
	public static final int KEY_NUM8 = 56;
	public static final int KEY_NUM9 = 57;
	public static final int KEY_STAR = 42;
	public static final int KEY_POUND = 35;


	protected Canvas()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;

		System.out.println("Create Canvas:"+width+", "+height);

		platformImage = new PlatformImage(width, height);
	}

	public int getGameAction(int keyCode)
	{
		switch(keyCode)
		{
			case Mobile.KEY_NUM2: return UP;
			case Mobile.KEY_NUM8: return DOWN;
			case Mobile.KEY_NUM4: return LEFT;
			case Mobile.KEY_NUM6: return RIGHT;
			case Mobile.KEY_NUM5: return FIRE;
			case Mobile.KEY_NUM1: return GAME_A;
			case Mobile.KEY_NUM3: return GAME_B;
			case Mobile.KEY_NUM7: return GAME_C;
			case Mobile.KEY_NUM9: return GAME_D;
			case Mobile.NOKIA_UP: return UP;
			case Mobile.NOKIA_DOWN: return DOWN;
			case Mobile.NOKIA_LEFT: return LEFT;
			case Mobile.NOKIA_RIGHT: return RIGHT;
		}
		return 0;
	}

	public int getKeyCode(int gameAction)
	{
		switch(gameAction)
		{
			//case Mobile.GAME_UP: return Mobile.KEY_NUM2;
			//case Mobile.GAME_DOWN: return Mobile.KEY_NUM8;
			//case Mobile.GAME_LEFT: return Mobile.KEY_NUM4;
			//case Mobile.GAME_RIGHT: return Mobile.KEY_NUM6;
			case Mobile.GAME_UP: return Mobile.NOKIA_UP;
			case Mobile.GAME_DOWN: return Mobile.NOKIA_DOWN;
			case Mobile.GAME_LEFT: return Mobile.NOKIA_LEFT;
			case Mobile.GAME_RIGHT: return Mobile.NOKIA_RIGHT;
			case Mobile.GAME_FIRE: return Mobile.KEY_NUM5;
			case Mobile.GAME_A: return Mobile.KEY_NUM1;
			case Mobile.GAME_B: return Mobile.KEY_NUM3;
			case Mobile.GAME_C: return Mobile.KEY_NUM7;
			case Mobile.GAME_D: return Mobile.KEY_NUM9;
		}
		return Mobile.KEY_NUM5;
	}

	public String getKeyName(int keyCode)
	{
		if(keyCode<0) { keyCode=0-keyCode; }
		switch(keyCode)
		{
			case 1: return "UP";
			case 2: return "DOWN";
			case 5: return "LEFT";
			case 6: return "RIGHT";
			case 8: return "FIRE";
			case 9: return "A";
			case 10: return "B";
			case 11: return "C";
			case 12: return "D";
			case 48: return "0";
			case 49: return "1";
			case 50: return "2";
			case 51: return "3";
			case 52: return "4";
			case 53: return "5";
			case 54: return "6";
			case 55: return "7";
			case 56: return "8";
			case 57: return "9";
			case 42: return "*";
			case 35: return "#";
		}
		return "-";
	}

	public boolean hasPointerEvents() { return true; }

	public boolean hasPointerMotionEvents() { return false; }

	public boolean hasRepeatEvents() { return false; }

	public void hideNotify() { }

	public boolean isDoubleBuffered() { return true; }

	public void keyPressed(int keyCode) { }

	public void keyReleased(int keyCode) { }

	public void keyRepeated(int keyCode) { }

	protected abstract void paint(Graphics g);

	public void pointerDragged(int x, int y) { }

	public void pointerPressed(int x, int y) { }

	public void pointerReleased(int x, int y) { }

	public void repaint()
	{
		try
		{
			paint(platformImage.getGraphics());
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
		catch (Exception e)
		{
			System.out.print("Canvas repaint(): "+e.getMessage());
			e.printStackTrace();
		}
	}

	public void repaint(int x, int y, int width, int height)
	{
		paint(platformImage.getGraphics());
		Mobile.getPlatform().repaint(platformImage, x, y, width, height);
	}

	public void serviceRepaints()
	{
		Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
	}

	public void setFullScreenMode(boolean mode)
	{
		//System.out.print("Set Canvas Full Screen Mode ");
		fullScreen = mode;
		if(fullScreen)
		{
			width = Mobile.getPlatform().lcdWidth;
			height = Mobile.getPlatform().lcdHeight;
		}
	}

	public void showNotify() { }

	protected void sizeChanged(int w, int h)
	{
		width = w;
		height = h;
	}

	public void notifySetCurrent() { repaint(); }

}
