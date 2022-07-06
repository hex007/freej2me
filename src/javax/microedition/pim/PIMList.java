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

	String getName();

	void close() throws PIMException;

	Enumeration items() throws PIMException, SecurityException;

	Enumeration items(PIMItem matchingItem) throws PIMException, IllegalArgumentException, 
        NullPointerException, SecurityException;

	Enumeration items(String matchingValue) throws PIMException, NullPointerException, 
        SecurityException;

	Enumeration itemsByCategory(String category) throws PIMException, SecurityException;

	String[] getCategories() throws PIMException;

	boolean isCategory(String category) throws PIMException, NullPointerException;

	void addCategory(String category) throws PIMException, SecurityException, 
        NullPointerException;

	void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException, 
        SecurityException, NullPointerException;

	void renameCategory(String currentCategory, String newCategory) throws PIMException, 
        SecurityException, NullPointerException;

	int maxCategories();

	boolean isSupportedField(int field);

	int[] getSupportedFields();

	boolean isSupportedAttribute(int field, int attribute);

	int[] getSupportedAttributes(int field) throws IllegalArgumentException, 
        UnsupportedFieldException;

	boolean isSupportedArrayElement(int stringArrayField, int arrayElement);

	int[] getSupportedArrayElements(int stringArrayField) throws IllegalArgumentException, 
        UnsupportedFieldException;

	int getFieldDataType(int field) throws IllegalArgumentException, UnsupportedFieldException;

	String getFieldLabel(int field) throws IllegalArgumentException, 
        UnsupportedFieldException;

	String getAttributeLabel(int attribute) throws IllegalArgumentException, 
        UnsupportedFieldException;

	String getArrayElementLabel(int stringArrayField, int arrayElement) 
        throws IllegalArgumentException, UnsupportedFieldException;

	int maxValues(int field) throws IllegalArgumentException;

	int stringArraySize(int stringArrayField) throws IllegalArgumentException;
}
