/**
 *  Java docs licensed under the Apache License, Version 2.0
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *   (c) Copyright 2001, 2002 Motorola, Inc.  ALL RIGHTS RESERVED.
 *
 *
 *  @version $Id$
*/ 

/*
    Apache 2.0 can be embedded into GPLv3 according to Apache:
    https://www.apache.org/licenses/GPL-compatibility.html

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


public class UUID {

	public UUID(long uuidValue) {
		if (uuidValue < 0 || uuidValue > 0xffffffffl) {
			throw new IllegalArgumentException("uuidValue is not in the range [0, 2^32 -1]");
		}
	}

	public UUID(String uuidValue, boolean shortUUID) {
		if (uuidValue == null) {
            throw new NullPointerException("uuidValue is null");
		}
	}

	public String toString() {
		return null;
	}

	public boolean equals(Object value) {
		if (value == null || !(value instanceof UUID)) {
			return false;
		}
		return false;
	}

	public int hashCode() {
		return 0;
	}
}
