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

public class Background extends Object3D
{

	public static final int BORDER = 32;
	public static final int REPEAT = 33;

	private int color;
	private int modex;
	private int modey;
	private int cropw;
	private int croph;
	private int cropx;
	private int cropy;

	private Image2D image;
	private boolean depthclear;
	private boolean colorclear;


	public Background() {  }


	public int getColor() { return color; }

	public int getCropHeight() { return croph; }

	public int getCropWidth() { return cropw; }

	public int getCropX() { return cropx; }

	public int getCropY() { return cropy; }

	public Image2D getImage() { return image; }

	public int getImageModeX() { return modex; }

	public int getImageModeY() { return modey; }

	public boolean isColorClearEnabled() { return colorclear; }

	public boolean isDepthClearEnabled() { return depthclear; }

	public void setColor(int ARGB) { color = ARGB; }

	public void setColorClearEnable(boolean enable) {  colorclear = enable; }

	public void setCrop(int cropX, int cropY, int width, int height)
	{
		cropx=cropX;
		cropy=cropY;
		cropw=width;
		croph=height;
	}

	public void setDepthClearEnable(boolean enable) { depthclear = enable; }

	public void setImage(Image2D img) { image = img; }

	public void setImageMode(int modeX, int modeY) { modex=modeX; modey=modeY; }

}
