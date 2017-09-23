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


public class Spacer extends Item
{

	private int minWidth;
	private int minHeight;


	public Spacer(int minw, int minh)
	{
		minWidth = minw;
		minHeight = minh;
	}

	public void addCommand(Command cmd) { }

	public void setDefaultCommand(Command cmd) { }

	public void setLabel(String label) { }

	public void setMinimumSize(int minwidth, int minheight)
	{
		minWidth = minwidth;
		minHeight = minHeight;
	}

}
