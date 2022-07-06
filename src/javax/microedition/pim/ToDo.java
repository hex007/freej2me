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

public interface ToDo extends PIMItem 
{

	public static final int CLASS = 100;
	public static final int COMPLETED = 101;
	public static final int COMPLETION_DATE = 102;
	public static final int DUE = 103;
	public static final int NOTE = 104;
	public static final int PRIORITY = 105;
	public static final int REVISION = 106;
	public static final int SUMMARY = 107;
	public static final int UID = 108;
	public static final int CLASS_CONFIDENTIAL = 200;
	public static final int CLASS_PRIVATE = 201;
	public static final int CLASS_PUBLIC = 202;
}
