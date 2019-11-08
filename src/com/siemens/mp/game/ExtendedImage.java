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

package com.siemens.mp.game;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import com.siemens.mp.misc.NativeMem;

public class ExtendedImage extends com.siemens.mp.misc.NativeMem
{
	private int[] palette = { 0xFF000000, 0xFFFFFFFF };

	private PlatformImage image;

	private PlatformGraphics gc;

	private int width;

	private int height; 
	
	public ExtendedImage(Image img)
	{
		image = new PlatformImage(img);
		width = image.getWidth();
		height = image.getHeight();
		gc = image.getGraphics();
	}

	public Image getImage() { return image; }

	public int getPixel(int x, int y)
	{
		return image.getPixel(x, y);
	}

	public void setPixel(int x, int y, byte color)
	{
		image.setPixel(x, y, palette[color & 0x1]);
		System.out.println("setPixels");
	}

	public void getPixelBytes(byte[] pixels, int x, int y, int width, int height) { }

	public void setPixels(byte[] pixels, int x, int y, int width, int height) { System.out.println("setPixels"); }

	public void clear(byte color)
	{
		gc.setColor(palette[color & 1]);
		gc.fillRect(0, 0, width, height);
		gc.setColor(palette[0]);
	}

	public void blitToScreen(int x, int y) // from Micro Java Game Development By David Fox, Roman Verhovsek
	{
		Mobile.getPlatform().flushGraphics(image, x, y, width, height);
	} 
}