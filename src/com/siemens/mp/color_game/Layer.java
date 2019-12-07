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

import javax.microedition.lcdui.Image;

public abstract class Layer extends javax.microedition.lcdui.game.Layer
{

	public Layer() { x = 0;  y = 0; }

	public Layer(int w, int h) { x = 0;  y = 0; width = w; height = h; }

	public Layer(Image i) { setLayerImage(i); }

	protected void setWidth(int w) { width = w; }

	protected void setHeight(int h) { height = h; }

	protected void setLayerImage(Image i)
	{
		image = i;
		x = 0;
		y = 0;
		width = i.getWidth();
		height = i.getHeight();
	}

	protected void copyAllLayerVariables(Layer target)
	{
		target.setPosition(x, y);
		target.setWidth(width);
		target.setHeight(height);
		target.setLayerImage(image);
		target.setVisible(visible);
    }
}