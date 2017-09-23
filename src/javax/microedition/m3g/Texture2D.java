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

public class Texture2D extends Transformable
{

	public static final int FILTER_BASE_LEVEL = 208;
	public static final int FILTER_LINEAR = 209;
	public static final int FILTER_NEAREST = 210;
	public static final int FUNC_ADD = 224;
	public static final int FUNC_BLEND = 225;
	public static final int FUNC_DECAL = 226;
	public static final int FUNC_MODULATE = 227;
	public static final int FUNC_REPLACE = 228;
	public static final int WRAP_CLAMP = 240;
	public static final int WRAP_REPEAT = 241;


	private int blending = FUNC_ADD;
	private int blendcolor = 0;
	private int filter = FILTER_LINEAR;
	private int filterlevel = FILTER_BASE_LEVEL;
	private int wraps = WRAP_CLAMP;
	private int wrapt = WRAP_CLAMP;

	private Image2D texImage;

	public Texture2D(Image2D image) {  }


	public int getBlendColor() { return blendcolor; }

	public int getBlending() { return blending; }

	public Image2D getImage() { return texImage; }

	public int getImageFilter() { return filter; }

	public int getLevelFilter() { return filterlevel; }

	public int getWrappingS() { return wraps; }

	public int getWrappingT() { return wrapt; }

	public void setBlendColor(int RGB) {  }

	public void setBlending(int func) { blending = func; }

	public void setFiltering(int levelFilter, int imageFilter) { filterlevel = levelFilter; filter = imageFilter; }

	public void setImage(Image2D image) { texImage = image; }

	public void setWrapping(int wrapS, int wrapT) { wraps = wrapS; wrapt = wrapT; }

}
