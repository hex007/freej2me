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


package com.siemens.mp.color_game;

import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformGraphics;

public class LayerManager extends javax.microedition.lcdui.game.LayerManager
{
	private Vector<Layer> layers;

	public LayerManager() 
	{
		layers = new Vector<Layer>();

		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;

		canvas = Image.createImage(width, height);
		gc = canvas.platformImage.getGraphics();
	}

	public void paint(Graphics g, int xdest, int ydest)
	{
		for(int i=0; i<layers.size(); i++)
		{
			drawLayer(g, xdest, ydest, layers.get(i));
		}
	}

	private void drawLayer(Graphics g, int dx, int dy, Layer l)
	{
		if(l.isVisible())
		{
			g.drawRegion(l.getLayerImage(), 0, 0, l.getLayerImage().getWidth(), l.getLayerImage().getHeight(), 0, dx+x+l.getX(), dy+y+l.getY(), Graphics.TOP|Graphics.LEFT);
		}
	}

	public void append(Layer l)
	{
		try
		{
			layers.add(l);
		}
		catch(Exception e)
		{
			System.out.println("Can't Append Layer " + e.getMessage());
		}
	}

	public void remove(Layer l)
	{
		layers.remove(l);
	}
}
