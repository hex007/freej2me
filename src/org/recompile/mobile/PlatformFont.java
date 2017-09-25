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
package org.recompile.mobile;

import javax.microedition.lcdui.Font;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PlatformFont
{
	private Graphics2D gc;
	
	public java.awt.Font font;

	public PlatformFont(Font mobilefont)
	{
		// TODO: use info from mobilefont to construct font
		font = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, mobilefont.getPointSize());
		gc = new BufferedImage(1,1,BufferedImage.TYPE_INT_RGB).createGraphics();
		gc.setFont(font);
	}

	public int stringWidth(String str)
	{
		return gc.getFontMetrics().stringWidth(str);
	}
}