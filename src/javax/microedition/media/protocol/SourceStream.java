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
package javax.microedition.media.protocol;

public interface SourceStream extends javax.microedition.media.Controllable
{

	public static final int NOT_SEEKABLE = 0;
	public static final int RANDOM_ACCESSIBLE = 2;
	public static final int SEEKABLE_TO_START = 1;


	ContentDescriptor getContentDescriptor();

	public long getContentLength();

	public int getSeekType();

	public int getTransferSize();

	public int read(byte[] b, int off, int len);

	public long seek(long where);

	public long tell();

}
