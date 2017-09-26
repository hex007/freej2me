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
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class FileSystemRegistry {
	public static Enumeration listRoots() {
		List<String> fs = new ArrayList<String>();
		fs.add("dummy1");
		fs.add("dummy2");
		Enumeration<String> ret = Collections.enumeration(fs);
		
		return ret;
	};
}
