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

import java.net.URL;
import java.io.InputStream;

import java.awt.event.KeyEvent;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Graphics3D;

import java.awt.image.BufferedImage;


/*

	Mobile Platform

*/

public class MobilePlatform
{

	private PlatformImage lcd;
	private PlatformGraphics gc;
	public int lcdWidth;
	public int lcdHeight;

	public MIDletLoader loader;

	public Runnable painter;

	public String dataPath = "";

	public boolean sound = true;

	public int keyState = 0;

	public MobilePlatform(int width, int height)
	{
		lcdWidth = width;
		lcdHeight = height;

		lcd = new PlatformImage(width, height);
		gc = lcd.getGraphics();

		Mobile.setGraphics3D(new Graphics3D());

		painter = new Runnable()
		{
			public void run()
			{
				// Placeholder //
			}
		};
	}

	public void resizeLCD(int width, int height)
	{
		lcdWidth = width;
		lcdHeight = height;

		lcd = new PlatformImage(width, height);
		gc = lcd.getGraphics();
	}

	public BufferedImage getLCD()
	{
		return lcd.getCanvas();
	}

	public void setPainter(Runnable r)
	{
		painter = r;
	}

	public void keyPressed(int keycode)
	{
		updateKeyState(keycode, 1);
		Mobile.getDisplay().getCurrent().keyPressed(keycode);
	}

	public void keyReleased(int keycode)
	{
		updateKeyState(keycode, 0);
		Mobile.getDisplay().getCurrent().keyReleased(keycode);
	}

	public void pointerDragged(int x, int y)
	{
		Mobile.getDisplay().getCurrent().pointerDragged(x, y);
	}

	public void pointerPressed(int x, int y)
	{
		Mobile.getDisplay().getCurrent().pointerPressed(x, y);
	}

	public void pointerReleased(int x, int y)
	{
		Mobile.getDisplay().getCurrent().pointerReleased(x, y);
	}

	private void updateKeyState(int key, int val)
	{
		int mask=0;
		switch (key)
		{
			case Mobile.KEY_NUM2: mask = GameCanvas.UP_PRESSED; break;
			case Mobile.KEY_NUM4: mask = GameCanvas.LEFT_PRESSED; break;
			case Mobile.KEY_NUM6: mask = GameCanvas.RIGHT_PRESSED; break;
			case Mobile.KEY_NUM8: mask = GameCanvas.DOWN_PRESSED; break;
			case Mobile.KEY_NUM5: mask = GameCanvas.FIRE_PRESSED; break;
			case Mobile.KEY_NUM1: mask = GameCanvas.GAME_A_PRESSED; break;
			case Mobile.KEY_NUM3: mask = GameCanvas.GAME_B_PRESSED; break;
			case Mobile.KEY_NUM7: mask = GameCanvas.GAME_C_PRESSED; break;
			case Mobile.KEY_NUM9: mask = GameCanvas.GAME_D_PRESSED; break;
			case Mobile.NOKIA_UP: mask = GameCanvas.UP_PRESSED; break;
			case Mobile.NOKIA_LEFT: mask = GameCanvas.LEFT_PRESSED; break;
			case Mobile.NOKIA_RIGHT: mask = GameCanvas.RIGHT_PRESSED; break;
			case Mobile.NOKIA_DOWN: mask = GameCanvas.DOWN_PRESSED; break;
		}
		keyState |= mask;
		keyState ^= mask;
		if(val==1) { keyState |= mask; }
	}

/*
	******** Jar Loading ********
*/

	public boolean loadJar(String jarurl)
	{
		try
		{
			URL jar = new URL(jarurl);
			loader = new MIDletLoader(new URL[]{jar});
			return true;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public void runJar()
	{
		try
		{
			loader.start();
		}
		catch (Exception e)
		{
			System.out.println("Error Running Jar");
			e.printStackTrace();
		}
	}

/*
	********* Graphics ********
*/

	public void flushGraphics(Image img, int x, int y, int width, int height)
	{
		gc.flushGraphics(img, x, y, width, height);

		painter.run();

		//System.gc();
	}

	public void repaint(Image img, int x, int y, int width, int height)
	{
		gc.flushGraphics(img, x, y, width, height);

		painter.run();

		//System.gc();
	}

}
