/**
 * MicroEmulator
 * Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 * Copyright (C) 2006-2007 Vlad Skarzhevskyy
 * <p>
 * It is licensed under the following two licenses as alternatives:
 * 1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 * 2. Apache License (the "AL") Version 2.0
 * <p>
 * You may not use this file except in compliance with at least one of
 * the above two licenses.
 * <p>
 * You may obtain a copy of the LGPL at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 * <p>
 * You may obtain a copy of the AL at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the LGPL or the AL for the specific language governing permissions and
 * limitations.
 *
 * @version $Id$
 */

/*
	The above disclaimer demands compliance with at least one of the licenses
	listed. Apache 2.0 can be embedded into GPLv3 according to Apache:
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

package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {

	public abstract boolean isOpen();

	@Override
	public abstract InputStream openInputStream();

	@Override
	public abstract DataInputStream openDataInputStream();

	@Override
	public abstract OutputStream openOutputStream();

	@Override
	public abstract DataOutputStream openDataOutputStream();

	public abstract OutputStream openOutputStream(long byteOffset) throws IOException;

	public abstract long totalSize();

	public abstract long availableSize();

	public abstract long usedSize();

	public abstract long directorySize(boolean includeSubDirs) throws IOException;

	public abstract long fileSize() throws IOException;

	public abstract boolean canRead();

	public abstract boolean canWrite();

	public abstract boolean isHidden();

	public abstract void setReadable(boolean readable) throws IOException;

	public abstract void setWritable(boolean writable) throws IOException;

	public abstract void setHidden(boolean hidden) throws IOException;

	public abstract Enumeration list() throws IOException;

	public abstract Enumeration list(String filter, boolean includeHidden) throws IOException;

	public abstract void create() throws IOException;

	public abstract void mkdir() throws IOException;

	public abstract boolean exists();

	public abstract boolean isDirectory();

	public abstract void delete() throws IOException;

	public abstract void rename(String newName) throws IOException;

	public abstract void truncate(long byteOffset) throws IOException;

	public abstract void setFileConnection(String s) throws IOException;

	public abstract String getName();

	public abstract String getPath();

	public abstract String getURL();

	public abstract long lastModified();
}
