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
package javax.microedition.m3g;

public class Image2D extends Object3D
{

	public static final int ALPHA = 96;
	public static final int LUMINANCE = 97;
	public static final int LUMINANCE_ALPHA = 98;
	public static final int RGB = 99;
	public static final int RGBA = 100;


	private byte[] image;
	private int width = 0;
	private int height = 0;
	private int format = RGB;

	public Image2D(int fmt, int w, int h) { width=w; height=h; format=fmt; }

	public Image2D(int fmt, int w, int h, byte[] img) { width=w; height=h; format=fmt; image=img; }

	public Image2D(int fmt, int w, int h, byte[] img, byte[] Palette) { width=w; height=h; format=fmt; image=img; }

	public Image2D(int fmt, Object img) { format=fmt; /*image=(byte[])img;*/ }


	public int getFormat() { return format; }

	public int getHeight() { return height; }

	public int getWidth() { return width; }

	public boolean isMutable() { return true; }

	public void set(int x, int y, int w, int h, byte[] img) { width=w; height=h; image=img; }
}
