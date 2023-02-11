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
package javax.microedition.pim;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public abstract class PIM 
{

	public static final int CONTACT_LIST = 1;
	public static final int EVENT_LIST = 2;
	public static final int TODO_LIST = 3;
	public static final int READ_ONLY = 1;
	public static final int WRITE_ONLY = 2;
	public static final int READ_WRITE = 3;

	protected PIM() { }

	public static PIM getInstance() 
	{
		throw new SecurityException("PIM is stubbed, can't access this device's instance");
	}

	public abstract PIMList openPIMList(int pimListType, int mode) throws SecurityException, 
        IllegalArgumentException, PIMException;

	public abstract PIMList openPIMList(int pimListType, int mode, String name) throws SecurityException, 
        IllegalArgumentException, NullPointerException, PIMException;

	public abstract String[] listPIMLists(int pimListType) throws SecurityException, IllegalArgumentException;

	public abstract PIMItem[] fromSerialFormat(InputStream is, String enc) throws NullPointerException, 
        PIMException, UnsupportedEncodingException;

	public abstract void toSerialFormat(PIMItem item, OutputStream os, String enc, String dataFormat)
		throws NullPointerException, IllegalArgumentException, PIMException, UnsupportedEncodingException;

	public abstract String[] supportedSerialFormats(int pimListType) throws IllegalArgumentException;
}
