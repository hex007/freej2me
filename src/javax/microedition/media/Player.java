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
package javax.microedition.media;


public interface Player extends Controllable
{
	
	public static final int CLOSED = 0;
	public static final int PREFETCHED = 300;
	public static final int REALIZED = 200;
	public static final int STARTED = 400;
	public static final long TIME_UNKNOWN = -1;
	public static final int UNREALIZED = 100;


	public void addPlayerListener(PlayerListener playerListener);

	public void close();

	public void deallocate();

	public String getContentType();

	public long getDuration();

	public long getMediaTime();

	public int getState();

	public void prefetch();

	public void realize();

	public void removePlayerListener(PlayerListener playerListener);

	public void setLoopCount(int count);

	public long setMediaTime(long now);

	public void start();

	public void stop();

}