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


public class Command
{
	public static final int SCREEN = 1;
	public static final int BACK = 2;
	public static final int CANCEL = 3;
	public static final int OK = 4;
	public static final int HELP = 5;
	public static final int STOP = 6;
	public static final int EXIT = 7;
	public static final int ITEM = 8;

	private static final String[] labels = {"-","SCR","Back","Cancel","OK","Help","Stop","Exit","Item"};

	private String label;

	private String shortLabel;

	private int type;

	private int priority;


	public Command(String text, int cmdType, int cmdPriority)
	{
		if(text.equals(""))
		{
			text = "---";
			if(cmdType>=0 && cmdType<=8) { text = labels[cmdType]; }
		}
		label = text;
		type = cmdType;
		priority = cmdPriority;
		shortLabel = text;
	}

	public Command(String shorttext, String text, int cmdType, int cmdPriority)
	{
		if(text.equals(""))
		{
			text = "---";
			if(cmdType>=0 && cmdType<=8) { text = labels[cmdType]; }
		}
		if(shorttext.equals("")) { shorttext = text; }

		label = text;
		type = cmdType;
		priority = cmdPriority;
		shortLabel = shorttext;
	}

	public int getCommandType() { return type; }

	public String getLabel() { return shortLabel; }

	public String getLongLabel() { return label; }

	public int getPriority() { return priority; }

}
