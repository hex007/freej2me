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
import org.recompile.mobile.PlatformGraphics;

public class Graphics
{
	public static final int BASELINE = 64;
	public static final int BOTTOM = 32;
	public static final int DOTTED = 1;
	public static final int HCENTER = 1;
	public static final int LEFT = 4;
	public static final int RIGHT = 8;
	public static final int SOLID = 0;
	public static final int TOP = 16;
	public static final int VCENTER = 2;


	protected int translateX = 0;
	protected int translateY = 0;

	protected int clipX = 0;
	protected int clipY = 0;
	protected int clipWidth = 0;
	protected int clipHeight = 0;

	protected int color = 0xFFFFFF;
	protected Font font = Font.getDefaultFont();
	protected int strokeStyle = SOLID;


	public PlatformImage platformImage;
	public PlatformGraphics platformGraphics;

	public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {  }

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {  }

	public void drawChar(char character, int x, int y, int anchor) {  }

	public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {  }

	public void drawImage(Image img, int x, int y, int anchor) {  }

	public void drawLine(int x1, int y1, int x2, int y2) {  }

	public void drawRect(int x, int y, int width, int height) {  }

	public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor) {  }

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {  }

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {  }

	public void drawString(String str, int x, int y, int anchor) {  }

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {  }

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {  }

	public void fillRect(int x, int y, int width, int height) { }

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {  }

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {  }

	public int getColor() { return color; }

	public int getDisplayColor(int trycolor) { return trycolor; }

	public Font getFont() { return font; }

	public int getGrayScale()
	{
		int r = (color>>16) & 0xFF;
		int g = (color>>8) & 0xFF;
		int b = color & 0xFF;
		return ((r+g+b) / 3) & 0xFF;
	}

	public int getBlueComponent() { return color & 0xFF; }

	public int getGreenComponent() { return (color>>8) & 0xFF; }

	public int getRedComponent() { return (color>>16) & 0xFF; }

	public int getStrokeStyle() { return strokeStyle; }


	public void clipRect(int x, int y, int width, int height) { }

	public void setClip(int x, int y, int width, int height) { }

	public int getClipHeight() { return clipHeight; }

	public int getClipWidth() { return clipWidth; }

	public int getClipX() { return clipX; }

	public int getClipY() { return clipY; }

	public void translate(int x, int y)
	{
		translateX += x;
		translateY += y;
	}

	public int getTranslateX()
	{
		return translateX;
	}

	public int getTranslateY()
	{
		return translateY;
	}

	public void setColor(int RGB) { color = RGB; }

	public void setColor(int red, int green, int blue) { color = (red<<16) + (green<<8) + blue; }

	public void setFont(Font newfont) { font = newfont; }

	public void setGrayScale(int value)
	{
		value = value & 0xFF;
		color = (value<<16) + (value<<8) + value;
	}

	public void setStrokeStyle(int style)
	{
		strokeStyle = style;
	}

}
