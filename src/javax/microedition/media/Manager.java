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

import java.io.InputStream;
import java.io.IOException;

import org.recompile.mobile.PlatformPlayer;

public final class Manager
{
	public static final String TONE_DEVICE_LOCATOR = "device://tone";
	public static Player midiPlayers[] = new Player[32]; /* Default max amount of players in FreeJ2ME's config  */
	public static byte midiPlayersIndex = 0;

	public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException
	{
		//System.out.println("Create Player Stream "+type);
		if(type.equalsIgnoreCase("audio/mid") || type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("sp-midi") || type.equalsIgnoreCase("audio/spmidi"))
		{
			if(midiPlayersIndex >= midiPlayers.length) { midiPlayersIndex = 0; }
			for(; midiPlayersIndex < midiPlayers.length; midiPlayersIndex++) 
			{
				if(midiPlayers[midiPlayersIndex] == null) { break; } /* A null position means we can use it right away */
				/* Otherwise, we only deallocate a position if it is not playing (running). */
				else if(midiPlayers[midiPlayersIndex] != null && midiPlayers[midiPlayersIndex].getState() == Player.PREFETCHED)
				{ 
					midiPlayers[midiPlayersIndex].deallocate();
					break;
				}
				/* If we ever reach this one, it's because all the other slots are used, and are playing */
				else if(midiPlayersIndex == midiPlayers.length-1)
				{
					midiPlayers[midiPlayersIndex].deallocate();
					break;
				}
			}
			midiPlayers[midiPlayersIndex] = new PlatformPlayer(stream, type);
			return midiPlayers[midiPlayersIndex++];
		}
		else 
		{
			return new PlatformPlayer(stream, type);
		}
	}

	public static Player createPlayer(String locator) throws MediaException
	{
		System.out.println("Create Player "+locator);
		return new PlatformPlayer(locator);
	}
	
	public static String[] getSupportedContentTypes(String protocol)
	{
		//System.out.println("Get Supported Media Content Types");
		return new String[]{"audio/midi", "audio/x-wav", 
		"audio/amr", "audio/mpeg"};
	}
	
	public static String[] getSupportedProtocols(String content_type)
	{
		System.out.println("Get Supported Media Protocols");
		return new String[]{};
	}
	
	public static void playTone(int note, int duration, int volume)
	{
		System.out.println("Play Tone");
	}

	public static void updatePlayerNum(byte num) 
	{
		midiPlayers = new Player[num];
	}
}
