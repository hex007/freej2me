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

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection 
{

    long availableSize() throws SecurityException, IllegalModeException, ConnectionClosedException;

    boolean canRead() throws SecurityException, IllegalModeException, ConnectionClosedException;

    boolean canWrite() throws SecurityException, IllegalModeException, ConnectionClosedException;

    void create() throws IOException, SecurityException, IllegalModeException;

    void delete() throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    long directorySize(boolean includeSubDirs) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    boolean exists() throws SecurityException, IllegalModeException, ConnectionClosedException;

    long fileSize() throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    String getName();

    String getPath();

    String getURL();

    boolean isDirectory() throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    boolean isHidden() throws SecurityException, IllegalModeException, ConnectionClosedException;

    boolean isOpen();
    
    long lastModified() throws SecurityException, IllegalModeException, ConnectionClosedException;

    Enumeration list() throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    Enumeration list(String filter, boolean includeHidden) throws NullPointerException, IllegalArgumentException, IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    void mkdir() throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    @Override 
    DataInputStream openDataInputStream() throws SecurityException, IllegalModeException;

    @Override
    DataOutputStream openDataOutputStream() throws SecurityException, IllegalModeException;

    @Override
    InputStream openInputStream() throws SecurityException, IllegalModeException;

    @Override
    OutputStream openOutputStream() throws SecurityException, IllegalModeException;

    OutputStream openOutputStream(long byteOffset) throws IOException, SecurityException, IllegalModeException, IllegalArgumentException;

    void rename(String newName) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException, NullPointerException, IllegalArgumentException;

    void setFileConnection(String fileName) throws IOException, SecurityException, NullPointerException, IllegalArgumentException;

    void setHidden(boolean hidden) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    void setReadable(boolean readable) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    void setWritable(boolean writable) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException;

    long totalSize() throws SecurityException, IllegalModeException, ConnectionClosedException;

    void truncate(long byteOffset) throws IOException, SecurityException, IllegalModeException, ConnectionClosedException, IllegalArgumentException;

    long usedSize() throws SecurityException, IllegalModeException, ConnectionClosedException;

}
