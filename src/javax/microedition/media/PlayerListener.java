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


public interface PlayerListener
{

	public static final String CLOSED  = "closed";
	public static final String DEVICE_AVAILABLE  = "deviceAvailable";
	public static final String DEVICE_UNAVAILABLE  = "deviceUnavailable";
	public static final String DURATION_UPDATED  = "durationUpdated";
	public static final String END_OF_MEDIA  = "endOfMedia";
	public static final String ERROR  = "error";
	public static final String STARTED  = "started";
	public static final String STOPPED  = "stopped";
	public static final String VOLUME_CHANGED  = "volumeChanged";


	public void playerUpdate(Player player, String event, Object eventData);

}