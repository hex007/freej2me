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

import java.io.InputStream;
import java.util.logging.Logger;

import org.recompile.mobile.PlatformImage;


public class Image
{
	private static final Logger LOG = Logger.getLogger(Image.class.getName());
	public PlatformImage platformImage;

	public int width;
	public int height;

	public static Image createImage(byte[] imageData, int imageOffset, int imageLength)
	{
		//LOG.info("Create Image from image data ");
		return new PlatformImage(imageData, imageOffset, imageLength);
	}

	public static Image createImage(Image source)
	{
		//LOG.info("Create Image from Image ");
		return new PlatformImage(source);
	}

	public static Image createImage(Image img, int x, int y, int width, int height, int transform)
	{
		//LOG.info("Create Image from sub-image ");
		return new PlatformImage(width-x, height-y);
	}

	public static Image createImage(InputStream stream)
	{
		//LOG.info("Create Image stream");
		return new PlatformImage(stream);
	}

	public static Image createImage(int width, int height)
	{
		//LOG.info("Create Image w,h " + width + ", " + height);
		return new PlatformImage(width, height);
	}

	public static Image createImage(String name)
	{
		//LOG.info("Create Image " + name);
		return new PlatformImage(name);
	}

	public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha)
	{
		//LOG.info("Create Image RGB " + width + ", " + height);
		return new PlatformImage(rgb, width, height, processAlpha);
	}

	public Graphics getGraphics() { return platformImage.getGraphics(); }

	public int getHeight() { return height; }

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {  }

	public int getWidth() { return width; }

	public boolean isMutable() { return true; }

}
