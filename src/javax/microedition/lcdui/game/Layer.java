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

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;


public abstract class Layer
{
	protected int x;

	protected int y;
	
	protected int height;
	
	protected int width;

	protected Image image;

	protected boolean visible;


	public int getHeight() { return height; }

	public int getWidth() { return width; }

	public int getX() { return x; }

	public int getY() { return y; }

	public boolean isVisible() { return visible; }

	public void move(int dx, int dy) { x+=dx; y+=dy; }

	public abstract void paint(Graphics g);

	public void setPosition(int nx, int ny) { x=nx; y=ny; }

	public void setVisible(boolean state) { visible = state; }

	
	public Image getLayerImage() { return image; }

	public void render() { this.paint(image.platformImage.getGraphics()); }

}
