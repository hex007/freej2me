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
package javax.microedition.media.control;

public interface MetaDataControl extends javax.microedition.media.Control
{

	public static final java.lang.String AUTHOR_KEY = "author";
	public static final java.lang.String COPYRIGHT_KEY = "copyright";
	public static final java.lang.String DATE_KEY = "date";
	public static final java.lang.String TITLE_KEY = "title";

	public java.lang.String[] getKeys();

	public java.lang.String getKeyValue(java.lang.String key);

}
