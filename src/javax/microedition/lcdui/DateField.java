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

import java.util.Date;
import java.util.TimeZone;


public class DateField extends Item
{

	public static int DATE = 1;
	public static int DATE_TIME = 2;
	public static int TIME = 3;


	private int mode;
	private TimeZone timezone;


	public DateField(String label, int Mode)
	{
		setLabel(label);
		mode = Mode;
	}

	public DateField(String label, int Mode, TimeZone timeZone)
	{
		setLabel(label);
		mode = Mode;
		timezone = timezone;
	}

	public Date getDate() { return new Date(); }

	public int getInputMode() { return mode; }

	public void setDate(Date date) { }

	public void setInputMode(int Mode) { mode = Mode; }

}
