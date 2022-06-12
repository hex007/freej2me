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
package javax.microedition.io.file;

import java.util.Enumeration;

public class FileSystemRegistry extends Object
{
	
	Enumeration roots; /* A zero-length Enumeration to be used below */

    public static boolean addFileSystemListener(FileSystemListener listener) throws SecurityException, 
	NullPointerException { return false; } /* Returns if the fileSystemListener was added, similar for the remove below */

    public Enumeration listRoots() { return roots; }; /*If no roots are found, or it's not supported, return a zero-len enum*/

    public static boolean removeFileSystemListener(FileSystemListener listener) throws NullPointerException { return false; };

}
