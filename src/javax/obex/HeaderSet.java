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

public interface HeaderSet
{

    static final int COUNT = 0xC0;

    static final int NAME =  0x01;
    
    static final int TYPE = 0x42;
    
    static final int LENGTH = 0xC3;
    
    static final int TIME_ISO_8601 = 0x44;
    
    static final int TIME_4_BYTE =  0xC4;
    
    static final int DESCRIPTION = 0x05;
    
    static final int TARGET = 0x46;
    
    static final int HTTP = 0x47;
    
    static final int WHO = 0x4A;
    
    static final int OBJECT_CLASS = 0x4F;
    
    static final int APPLICATION_PARAMETER = 0x4C;

    void setHeader(int headerID, Object headerValue) throws IllegalArgumentException;

    Object getHeader(int headerID) throws IllegalArgumentException, IOException;

    int[] getHeaderList() throws IOException;

    void createAuthenticationChallenge(java.lang.String realm, boolean userID, 
    boolean access);

    int getResponseCode() throws IOException;    

}
