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

import com.siemens.mp.misc.NativeMem;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import java.util.ArrayList;


public class GraphicObjectManager extends com.siemens.mp.misc.NativeMem
{
	public GraphicObjectManager() { }
	
	public static byte[] createTextureBits(int width, int height, byte[] texture) { return texture; }
	

	public void addObject(GraphicObject g) { }

	public void insertObject(GraphicObject g, int pos) { }

	public void deleteObject(GraphicObject g) { }


	public GraphicObject getObjectAt(int index) { return null; }
	
	public int getObjectPosition(GraphicObject gobject) { return 0; }

	
	public void paint(ExtendedImage img, int x, int y) { }

	public void paint(Image image, int x, int y) { }

}