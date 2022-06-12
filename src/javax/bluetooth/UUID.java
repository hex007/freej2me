 
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
package javax.bluetooth;

public class UUID extends Object
{
	UUID uuid;
	private static final String BASE_UUID_VALUE = "0x0000000000001000800000805F9B34FB";

    public UUID(long uuidValue) throws IllegalArgumentException { System.out.println("UUID Value:" + uuidValue); }

    public UUID(String uuidValue, boolean shortUUID) throws IllegalArgumentException, NullPointerException,
	NumberFormatException { System.out.println("UUID Value:" + uuidValue); }

    public boolean equals(Object value) { return false; }

    public int hashCode() { return uuid.hashCode(); }

    public String toString() { return BASE_UUID_VALUE; }
}
 