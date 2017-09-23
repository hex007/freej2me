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

import java.util.ArrayList;

public class FigureLayout
{
	private int scalex=1;
	private int scaley=1;
	private int centerx=1;
	private int centery=1;

	private int pwidth=1;
	private int pheight=1;

	private int selected = 0;

	private ArrayList<AffineTrans> trans = new ArrayList<AffineTrans>();


	public FigureLayout() {  }

	public FigureLayout(AffineTrans atrans, int sx, int sy, int cx, int cy)
	{
		scalex = sx;
		scaley = sy;
		centerx = cx;
		centery = cy;
		trans.add(atrans);
	}


	public final AffineTrans getAffineTrans() { return trans.get(selected); }

	public final void setAffineTrans(AffineTrans atrans)
	{
		if(trans.size()==0)
		{
			trans.add(atrans);
		}
		else
		{
			trans.set(selected, atrans);
		}
	}

	public final void setAffineTransArray(AffineTrans[] atrans)
	{
		for(int i=0; i<atrans.length; i++)
		{
			trans.add(atrans[i]);
		}
	}

	public final void setAffineTrans(AffineTrans[] atrans) { setAffineTransArray(atrans); }

	public final void selectAffineTrans(int i) { selected = i; }

	public final int getScaleX() { return scalex; }

	public final int getScaleY() { return scaley; }

	public final void setScale(int x, int y) { scalex=x; scaley=y; }


	public final int getParallelWidth() { return pwidth; }

	public final int getParallelHeight() { return pheight; }

	public final void setParallelSize(int w, int h) { pwidth=w; pheight=h; }


	public final int getCenterX() { return centerx; }

	public final int getCenterY() { return centery; }

	public final void setCenter(int x, int y) { centerx=x; centery=y; }

	public final void setPerspective(int x, int y, int z) {  }

	public final void setPerspective(int x, int y, int z, int w) {  }
}
