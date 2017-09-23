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
package com.mascotcapsule.micro3d.v3;

import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import org.recompile.mobile.Mobile;

public class Figure
{

	private byte[] figure;

	private ArrayList<Texture> textures = new ArrayList<Texture>();

	private int selected = 0;

	private int pattern = 0;

	public Figure(byte[] fig) { figure = fig; }

	public Figure(String name) throws IOException
	{
		InputStream stream = Mobile.getPlatform().loader.getResourceAsStream(name);
		try
		{
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int count=0;
			byte[] data = new byte[4096];
			while (count!=-1)
			{
				count = stream.read(data);
				if(count!=-1) { buffer.write(data, 0, count); }
			}
			figure = buffer.toByteArray();
		}
		catch (Exception e) {  }
	}


	public final void dispose() {  }

	public final void setPosture(ActionTable actiontable, int i, int j) {  }

	public final Texture getTexture() { return null; }

	public final void setTexture(Texture texture) {  }

	public final void setTexture(Texture[] atexture) {  }

	public final int getNumTextures() { return textures.size(); }

	public final void selectTexture(int i) { selected = i; }

	public final int getNumPattern() { return pattern; }

	public final void setPattern(int value) { pattern = value; }
}
