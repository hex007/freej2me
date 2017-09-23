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

import java.util.ArrayList;



public abstract class Item
{

	public static final int BUTTON = 2;
	public static final int HYPERLINK = 1;

	public static final int LAYOUT_DEFAULT = 0;

	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;

	public static final int LAYOUT_TOP = 0x10;
	public static final int LAYOUT_BOTTOM  = 0x20;
	public static final int LAYOUT_VCENTER = 0x30;

	public static final int LAYOUT_NEWLINE_BEFORE = 0x100;
	public static final int LAYOUT_NEWLINE_AFTER = 0x200;

	public static final int LAYOUT_SHRINK = 0x400;
	public static final int LAYOUT_VSHRINK = 0x1000;
	public static final int LAYOUT_EXPAND = 0x800;
	public static final int LAYOUT_VEXPAND = 0x2000;

	public static final int LAYOUT_2 = 0x4000;

	public static final int PLAIN = 0;


	private String label;

	private ArrayList<Command> commands;

	private int layout;

	private Command defaultCommand;

	private ItemCommandListener commandListener;

	private int prefWidth = 64;

	private int prefHeight = 16;


	public void addCommand(Command cmd) { commands.add(cmd); }

	public String getLabel() { return label; }

	public int getLayout() { return layout; }

	public int getMinimumHeight() { return 16; }

	public int getMinimumWidth() { return 64; }

	public int getPreferredHeight() { return prefHeight; }

	public int getPreferredWidth() { return prefWidth; }

	public void notifyStateChanged() { }

	public void removeCommand(Command cmd) { commands.remove(cmd); }

	public void setDefaultCommand(Command cmd) { defaultCommand = cmd; }

	public void setItemCommandListener(ItemCommandListener listener) { commandListener = listener; }

	public void setLabel(String text) { label = text; }

	public void setLayout(int value) { layout = value; }

	public void setPreferredSize(int width, int height)
	{
		prefWidth = width;
		prefHeight = height;
	}

}
