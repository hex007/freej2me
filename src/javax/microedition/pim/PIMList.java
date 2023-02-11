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

import java.util.Enumeration;

public interface PIMList 
{

	public static final String UNCATEGORIZED = null;

	public String getName();

	public void close() throws PIMException;

	public Enumeration items() throws PIMException, SecurityException;

	public Enumeration items(PIMItem matchingItem) throws PIMException, IllegalArgumentException, 
        NullPointerException, SecurityException;

	public Enumeration items(String matchingValue) throws PIMException, NullPointerException, 
        SecurityException;

	public Enumeration itemsByCategory(String category) throws PIMException, SecurityException;

	public String[] getCategories() throws PIMException;

	public boolean isCategory(String category) throws PIMException, NullPointerException;

	public void addCategory(String category) throws PIMException, SecurityException, 
        NullPointerException;

	public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException, 
        SecurityException, NullPointerException;

	public void renameCategory(String currentCategory, String newCategory) throws PIMException, 
        SecurityException, NullPointerException;

	public int maxCategories();

	public boolean isSupportedField(int field);

	public int[] getSupportedFields();

	public boolean isSupportedAttribute(int field, int attribute);

	public int[] getSupportedAttributes(int field) throws IllegalArgumentException, 
        UnsupportedFieldException;

	public boolean isSupportedArrayElement(int stringArrayField, int arrayElement);

	public int[] getSupportedArrayElements(int stringArrayField) throws IllegalArgumentException, 
        UnsupportedFieldException;

	public int getFieldDataType(int field) throws IllegalArgumentException, UnsupportedFieldException;

	public String getFieldLabel(int field) throws IllegalArgumentException, 
        UnsupportedFieldException;

	public String getAttributeLabel(int attribute) throws IllegalArgumentException, 
        UnsupportedFieldException;

	public String getArrayElementLabel(int stringArrayField, int arrayElement) 
        throws IllegalArgumentException, UnsupportedFieldException;

	public int maxValues(int field) throws IllegalArgumentException;

	public int stringArraySize(int stringArrayField) throws IllegalArgumentException;
}
