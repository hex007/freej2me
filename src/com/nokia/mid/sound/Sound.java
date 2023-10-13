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
package com.nokia.mid.sound;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;


public class Sound
{
	public static final int FORMAT_TONE = 1;
	public static final int FORMAT_WAV = 5;
	public static final int SOUND_PLAYING = 0;
	public static final int SOUND_STOPPED = 1;
	public static final int SOUND_UNINITIALIZED = 3;

	private Player player;

	public Sound(byte[] data, int type) 
	{ 
		try 
		{
			if (type == FORMAT_TONE) 
			{
				player = Manager.createPlayer(new ByteArrayInputStream(data), "audio/x-tone-seq");
			}
			else if (type == FORMAT_WAV) 
			{
				player = Manager.createPlayer(new ByteArrayInputStream(data), "audio/wav");
			}
		}
		catch (MediaException exception) { }
		catch (IOException exception) { }
	}

	public Sound(int freq, long duration) { }

	public static int getConcurrentSoundCount(int type) { return 1; }

	public int getGain() { return 0; }

	public int getState() 
	{ 
		int state = player.getState();

		switch (state)
		{
			case Player.STARTED:
				return SOUND_PLAYING;
			case Player.PREFETCHED:
			case Player.REALIZED:
				return SOUND_STOPPED;
			case Player.UNREALIZED:
			case Player.CLOSED:
			default:
				return SOUND_UNINITIALIZED;
		}
	}

	public static int[] getSupportedFormats() { return new int[]{}; }

	public void init(byte[] data, int type) 
	{ 
		// init's functionality is covered when Manager creates the player - leave empty
	}

	public void init(int freq, long duration) { }

	public void play(int loop) 
	{ 
		player.setLoopCount(loop);
		player.start();
	}

	public void release() { }

	public void resume() { }

	public void setGain(int gain) { }

	public void setSoundListener(SoundListener listener) { }

	public void stop() 
	{
		player.stop();
	}

}
