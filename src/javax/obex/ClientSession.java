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

import java.io.IOException;

import javax.microedition.io.Connection;

public interface ClientSession extends Connection 
{

    void setAuthenticator(Authenticator auth) throws NullPointerException;

    HeaderSet createHeaderSet();

    void setConnectionID(long id) throws IllegalArgumentException ;

    long getConnectionID();

    HeaderSet connect(HeaderSet headers) throws IOException, IllegalArgumentException;

    HeaderSet disconnect(HeaderSet headers) throws IOException, IllegalArgumentException;

    HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException, 
    IllegalArgumentException;

    HeaderSet delete(HeaderSet headers) throws IOException, IllegalArgumentException;

    Operation get(HeaderSet headers) throws IOException, IllegalArgumentException;

    Operation put(HeaderSet headers) throws IOException, IllegalArgumentException;

}
