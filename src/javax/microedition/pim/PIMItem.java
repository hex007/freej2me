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

public interface PIMItem 
{

	public static final int BINARY = 0;
	public static final int BOOLEAN = 1;
	public static final int DATE = 2;
	public static final int INT = 3;
	public static final int STRING = 4;
	public static final int STRING_ARRAY = 5;
	public static final int ATTR_NONE = 0;
	public static final int EXTENDED_FIELD_MIN_VALUE = 16777216;
	public static final int EXTENDED_ATTRIBUTE_MIN_VALUE = 16777216;

	public PIMList getPIMList();

	public void commit() throws SecurityException, PIMException;

	public boolean isModified();

	public int[] getFields();

	public byte[] getBinary(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addBinary(int field, int attributes, byte[] value, int offset, int length) 
        throws IllegalArgumentException, NullPointerException, UnsupportedFieldException, FieldFullException;

	public void setBinary(int field, int index, int attributes, byte[] value, int offset, int length)
        throws IllegalArgumentException, NullPointerException, UnsupportedFieldException, IndexOutOfBoundsException;

	public long getDate(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addDate(int field, int attributes, long value) throws IllegalArgumentException, 
        UnsupportedFieldException, FieldFullException;

	public void setDate(int field, int index, int attributes, long value) throws IllegalArgumentException, 
        UnsupportedFieldException, IndexOutOfBoundsException;

	public int getInt(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addInt(int field, int attributes, int value) throws IllegalArgumentException, 
        UnsupportedFieldException, FieldFullException;

	public void setInt(int field, int index, int attributes, int value) throws IllegalArgumentException, 
        UnsupportedFieldException, IndexOutOfBoundsException;

	public String getString(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addString(int field, int attributes, String value) throws IllegalArgumentException, 
        NullPointerException, UnsupportedFieldException, FieldFullException;

	public void setString(int field, int index, int attributes, String value) throws IllegalArgumentException, 
    NullPointerException, UnsupportedFieldException, IndexOutOfBoundsException;

	public boolean getBoolean(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addBoolean(int field, int attributes, boolean value) throws IllegalArgumentException, 
        UnsupportedFieldException, FieldFullException;

	public void setBoolean(int field, int index, int attributes, boolean value) throws IllegalArgumentException, 
        UnsupportedFieldException, IndexOutOfBoundsException;

	public String[] getStringArray(int field, int index) throws IllegalArgumentException, 
        IndexOutOfBoundsException, UnsupportedFieldException;

	public void addStringArray(int field, int attributes, String[] value) throws IllegalArgumentException, 
        NullPointerException, UnsupportedFieldException, FieldFullException;

	public void setStringArray(int field, int index, int attributes, String[] value) throws NullPointerException, 
        IllegalArgumentException, UnsupportedFieldException, IndexOutOfBoundsException;

	public int countValues(int field) throws IllegalArgumentException, UnsupportedFieldException;

	public void removeValue(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public int getAttributes(int field, int index) throws IllegalArgumentException, IndexOutOfBoundsException, 
        UnsupportedFieldException;

	public void addToCategory(String category) throws NullPointerException, PIMException;

	public void removeFromCategory(String category) throws NullPointerException;

	public String[] getCategories();

	public int maxCategories();
}
