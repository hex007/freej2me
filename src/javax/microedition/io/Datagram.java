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
package javax.microedition.io;

import java.io.DataInput;
import java.io.DataOutput;

public interface Datagram extends DataInput, DataOutput
{

	public String getAddress();

	public byte[] getData();

	public int getLength();

	public int getOffset();

	public void reset();

	public void setAddress(Datagram reference);

	public void setAddress(String addr);

	public void setData(byte[] buffer, int offset, int len);

	public void setLength(int len);

}
