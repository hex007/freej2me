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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection 
{
    public long availableSize();

    public boolean canRead();

    public boolean canWrite();

    public void create();

    public void delete();

    public long directorySize(boolean includeSubDirs);

    public boolean exists();

    public long fileSize();

    public String getName();

    public String getPath();

    public String getURL();

    public boolean isDirectory();

    public boolean isHidden();

    public boolean isOpen();
    
    public long lastModified();

    public Enumeration list();

    public Enumeration list(String filter, boolean includeHidden);

    public void mkdir();

    public DataInputStream openDataInputStream();

    public DataOutputStream openDataOutputStream();

    public InputStream openInputStream();

    public OutputStream openOutputStream();

    public OutputStream openOutputStream(long byteOffset);

    public void rename();

    public void setFileConnection(String fileName);

    public void setHidden(boolean hidden);

    public void setReadable(boolean readable);

    public void setWritable(boolean writable);

    public long totalSize();

    public void truncate(long byteOffset);

    public long usedSize();
}