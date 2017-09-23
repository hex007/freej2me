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



public interface SocketConnection extends StreamConnection
{

	public static final byte DELAY = 0;
	public static final byte KEEPALIVE = 2;
	public static final byte LINGER = 1;
	public static final byte RCVBUF = 3;
	public static final byte SNDBUF = 4;


	public String getAddress();

	public String getLocalAddress();

	public int getLocalPort();

	public int getPort();

	public int getSocketOption(byte option);

	public void setSocketOption(byte option, int value);

}
