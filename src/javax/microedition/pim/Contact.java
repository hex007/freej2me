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

public interface Contact extends PIMItem 
{
    
	public static final int ADDR = 100;
	public static final int BIRTHDAY = 101;
	public static final int CLASS = 102;
	public static final int EMAIL = 103;
	public static final int FORMATTED_ADDR = 104;
	public static final int FORMATTED_NAME = 105;
	public static final int NAME = 106;
	public static final int NICKNAME = 107;
	public static final int NOTE = 108;
	public static final int ORG = 109;
	public static final int PHOTO = 110;
	public static final int PHOTO_URL = 111;
	public static final int PUBLIC_KEY = 112;
	public static final int PUBLIC_KEY_STRING = 113;
	public static final int REVISION = 114;
	public static final int TEL = 115;
	public static final int TITLE = 116;
	public static final int UID = 117;
	public static final int URL = 118;
	public static final int ATTR_ASST = 1;
	public static final int ATTR_AUTO = 2;
	public static final int ATTR_FAX = 4;
	public static final int ATTR_HOME = 8;
	public static final int ATTR_MOBILE = 16;
	public static final int ATTR_OTHER = 32;
	public static final int ATTR_PAGER = 64;
	public static final int ATTR_PREFERRED = 128;
	public static final int ATTR_SMS = 256;
	public static final int ATTR_WORK = 512;
	public static final int ADDR_POBOX = 0;
	public static final int ADDR_EXTRA = 1;
	public static final int ADDR_STREET = 2;
	public static final int ADDR_LOCALITY = 3;
	public static final int ADDR_REGION = 4;
	public static final int ADDR_POSTALCODE = 5;
	public static final int ADDR_COUNTRY = 6;
	public static final int NAME_FAMILY = 0;
	public static final int NAME_GIVEN = 1;
	public static final int NAME_OTHER = 2;
	public static final int NAME_PREFIX = 3;
	public static final int NAME_SUFFIX = 4;
	public static final int CLASS_CONFIDENTIAL = 200;
	public static final int CLASS_PRIVATE = 201;
	public static final int CLASS_PUBLIC = 202;

	public int getPreferredIndex(int field) throws IllegalArgumentException, UnsupportedFieldException;
}
