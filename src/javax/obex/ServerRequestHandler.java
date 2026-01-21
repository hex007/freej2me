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
package javax.obex; 

public class ServerRequestHandler extends Object
{

    private long connectionID = -1;

    protected ServerRequestHandler() { this.connectionID = -1; };

    public final HeaderSet createHeaderSet() { return null; }

    public void setConnectionID(long id) throws IllegalArgumentException { }

    public long getConnectionID() { return this.connectionID; }

    public int onConnect(HeaderSet request, HeaderSet reply) { return ResponseCodes.OBEX_HTTP_OK; }

    public void onDisconnect(HeaderSet request, HeaderSet reply) { }

    public int onSetPath(HeaderSet request, HeaderSet reply, boolean backup, boolean create) { return ResponseCodes.OBEX_HTTP_NOT_IMPLEMENTED; }

    public int onDelete(HeaderSet request, HeaderSet reply) { return ResponseCodes.OBEX_HTTP_NOT_IMPLEMENTED; }

    public int onPut(Operation op) { return ResponseCodes.OBEX_HTTP_NOT_IMPLEMENTED; }

    public void onAuthenticationFailure(byte[] userName) { }

}
