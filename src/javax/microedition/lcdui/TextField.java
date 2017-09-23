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



public class TextField extends Item
{
	public static final int ANY = 0;
	public static final int CONSTRAINT_MASK = 0xFFFF;
	public static final int DECIMAL = 5;
	public static final int EMAILADDR = 1;
	public static final int INITIAL_CAPS_SENTENCE = 0x200000;
	public static final int INITIAL_CAPS_WORD = 0x100000;
	public static final int NON_PREDICTIVE = 0x80000;
	public static final int NUMERIC = 2;
	public static final int PASSWORD = 0x10000;
	public static final int PHONENUMBER = 3;
	public static final int SENSITIVE = 0x40000;
	public static final int UNEDITABLE = 0x20000;
	public static final int URL = 4;


	private String text;
	private int max;
	private int constraints;
	private String mode;

	public TextField(String label, String value, int maxSize, int Constraints)
	{
		setLabel(label);
		text = value;
		max = maxSize;
		constraints = Constraints;
	}

	void delete(int offset, int length)
	{
		text = text.substring(0, offset) + text.substring(offset+length);
	}

	public int getCaretPosition() { return 0; }

	public int getChars(char[] data)
	{
		for(int i=0; i<text.length(); i++)
		{
			data[i] = text.charAt(i);
		}
		return text.length();
	}

	public int getConstraints() { return constraints; }

	public int getMaxSize() { return max; }

	public String getString() { return text; }

	public void insert(char[] data, int offset, int length, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(data, offset, length);
		out.append(text.substring(position));
		text = out.toString();
	}

	public void insert(String src, int position)
	{
		StringBuilder out = new StringBuilder();
		out.append(text, 0, position);
		out.append(src);
		out.append(text.substring(position));
		text = out.toString();
	}

	public void setChars(char[] data, int offset, int length)
	{
		StringBuilder out = new StringBuilder();
		out.append(data, offset, length);
		text = out.toString();
	}

	public void setConstraints(int Constraints) { constraints = Constraints; }

	public void setInitialInputMode(String characterSubset) { mode = characterSubset; }

	public int setMaxSize(int maxSize) { max = maxSize; return max; }

	public void setString(String value) { text = value; }

	public int size() { return text.length(); }

}
