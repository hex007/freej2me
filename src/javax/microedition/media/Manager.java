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
	public static Player midiChannel[] = new Player[36];
	public static byte midiChannelIndex = 0;

	public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException
	{
		//System.out.println("Create Player Stream "+type);
		if(type.equalsIgnoreCase("audio/mid") || type.equalsIgnoreCase("audio/midi") || type.equalsIgnoreCase("sp-midi") || type.equalsIgnoreCase("audio/spmidi"))
		{
			if(midiChannelIndex >= midiChannel.length) { midiChannelIndex = 0; }
			if(midiChannel[midiChannelIndex] != null)  { midiChannel[midiChannelIndex].deallocate(); }
			midiChannel[midiChannelIndex] = new PlatformPlayer(stream, type);
			midiChannelIndex++;
			return midiChannel[midiChannelIndex-1];
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

}
