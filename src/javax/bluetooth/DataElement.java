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
package javax.bluetooth;


public class DataElement
{
		   
	static int NULL		= 0;
	static int INT_1	= 0x10;
	static int INT_2	= 0x11;
	static int INT_4	= 0x12;
	static int INT_8	= 0x13;
	static int INT_16	= 0x14;
	static int U_INT_1	= 0x08;
	static int U_INT_2	= 0x09;
	static int U_INT_4	= 0x0A;
	static int U_INT_8	= 0x0B;
	static int U_INT_16 = 0x0C;
	static int UUID		= 0x18;
	static int STRING	= 0x20;
	static int BOOL    	= 0x28;
	static int DATSEQ	= 0x30;
	static int DATALT	= 0x38;
	static int URL		= 0x40;


	private boolean boolvalue = false;

	private int intvalue = 0;

	private long longvalue = 0L;

	private Object objvalue = null;

	DataElement(boolean bool) { boolvalue = bool; }

	DataElement(int valueType) { intvalue = valueType; }
	
	DataElement(int valueType, long value) { intvalue = valueType; longvalue = value; }
	
	DataElement(int valueType, Object value) { intvalue = valueType; objvalue = value; }


	public void addElement(DataElement elem) {  }

	public boolean getBoolean() { return boolvalue; }
	
	public int getDataType() { return intvalue; }
	
	public long getLong() { return longvalue; }
	
	public int getSize() { return 0; }
	
	public Object getValue() { return objvalue; }
	
	public void insertElementAt(DataElement elem, int index) {  }
	
	public boolean removeElement(DataElement elem) { return true; }
	
	public String toString() { return ""; }
}