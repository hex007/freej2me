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
package com.nokia.mid.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

public abstract class FullCanvas extends javax.microedition.lcdui.Canvas
{

	public static final int KEY_DOWN_ARROW = -2;
	public static final int KEY_END = -11;
	public static final int KEY_LEFT_ARROW = -3;
	public static final int KEY_RIGHT_ARROW = -4;
	public static final int KEY_SEND = -10;
	public static final int KEY_SOFTKEY1 = -6;
	public static final int KEY_SOFTKEY2 = -7;
	public static final int KEY_SOFTKEY3 = -5;
	public static final int KEY_UP_ARROW = -1;


	protected FullCanvas()
	{
		//System.out.println("Nokia FullCanvas");
	}

/*
	public void addCommand(Command cmd)
	{
		System.out.println("Nokia FullCanvas addCommand");
	}

	public void setCommandListener(CommandListener l)
	{
		System.out.println("Nokia FullCanvas setCommandListener");
	}

*/
}
