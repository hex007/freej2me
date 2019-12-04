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
package javax.microedition.lcdui.game;

import java.util.ArrayList;

import java.awt.Shape;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformGraphics;


public class LayerManager
{

	protected ArrayList<Layer> layers;

	protected Image canvas;
	protected PlatformGraphics gc;
	protected Shape clip;

	protected int x;
	protected int y;
	protected int width;
	protected int height;


	public LayerManager()
	{
		layers = new ArrayList<Layer>();

		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;

		canvas = Image.createImage(width, height);
		gc = canvas.platformImage.getGraphics();
	}

	public void append(Layer l) { layers.add(l); }

	public Layer getLayerAt(int index) { return layers.get(index); }

	public int getSize() { return layers.size(); }

	public void insert(Layer l, int index) { layers.add(index, l); }

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
			l.render();
			g.drawRegion(l.getLayerImage(), 0, 0, l.getLayerImage().getWidth(), l.getLayerImage().getHeight(), 0, dx+x+l.getX(), dy+y+l.getY(), Graphics.TOP|Graphics.LEFT);
		}
	}

	public void remove(Layer l) { layers.remove(l); }

	public void setViewWindow(int wx, int wy, int wwidth, int wheight)
	{
		x = wx;
		y = wy;
		width = wwidth;
		height = wheight;
	}

}
