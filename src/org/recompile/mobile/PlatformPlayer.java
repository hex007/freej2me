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
package org.recompile.mobile;

import java.io.InputStream;
import java.util.Vector;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;

import javax.microedition.media.Control;
import javax.microedition.media.Controllable;

public class PlatformPlayer implements Player
{

	private String contentType = "";

	private audioplayer player;

	private int state = Player.UNREALIZED;

	private Vector<PlayerListener> listeners;

	private Control[] controls;

	public PlatformPlayer(InputStream stream, String type)
	{
		listeners = new Vector<PlayerListener>();
		controls = new Control[3];

		contentType = type;

		if(type.equals("audio/midi") || type.equals("sp-midi") || type.equals("audio/spmidi"))
		{
			player = new midiPlayer(stream);
		}
		else
		{
			if(type.equals("audio/x-wav"))
			{
				player = new wavPlayer(stream);
			}
			else
			{
				System.out.println("No Player For: "+contentType);
				player = new audioplayer();
			}
		}

		controls[0] = new volumeControl();
		controls[1] = new tempoControl();
		controls[2] = new midiControl();

		//System.out.println("media type: "+type);
	}

	public PlatformPlayer(String locator)
	{
		listeners = new Vector<PlayerListener>();
		controls = new Control[3];
		System.out.println("Player locator: "+locator);
	}


	public void close()
	{
		player.stop();
		state = Player.CLOSED;
		notifyListeners(PlayerListener.CLOSED, null);
	}

	public int getState()
	{
		if(player.isRunning()==false)
		{
			state = Player.PREFETCHED;
		}
		return state;
	}

	public void start()
	{
		//System.out.println("Play "+contentType);
		if(Mobile.getPlatform().sound)
		{
			try
			{
				player.start();
			}
			catch (Exception e) {  }
		}
	}

	public void stop()
	{
		try
		{
			player.stop();
		}
		catch (Exception e) { }
	}

	public void addPlayerListener(PlayerListener playerListener)
	{
		//System.out.println("Add Player Listener");
		listeners.add(playerListener);
	}

	public void removePlayerListener(PlayerListener playerListener)
	{
		listeners.remove(playerListener);
	}

	private void notifyListeners(String event, Object eventData)
	{
		for(int i=0; i<listeners.size(); i++)
		{
			listeners.get(i).playerUpdate(this, event, eventData);
		}
	}

	public void deallocate() {
		stop();
		if (player instanceof midiPlayer) {
			((midiPlayer) player).midi.close();
		}
		player = null;
		state = Player.CLOSED;
	}

	public String getContentType() { return contentType; }

	public long getDuration() { return Player.TIME_UNKNOWN; }

	public long getMediaTime() { return player.getMediaTime(); }

	public void prefetch() { state = Player.PREFETCHED; }

	public void realize() { state = Player.REALIZED; }

	public void setLoopCount(int count) { player.setLoopCount(count); }

	public long setMediaTime(long now) { return player.setMediaTime(now); }

	// Controllable interface //

	public Control getControl(String controlType)
	{
		if(controlType.equals("VolumeControl")) { return controls[0]; }
		if(controlType.equals("TempoControl")) { return controls[1]; }
		if(controlType.equals("MIDIControl")) { return controls[2]; }
		if(controlType.equals("javax.microedition.media.control.VolumeControl")) { return controls[0]; }
		if(controlType.equals("javax.microedition.media.control.TempoControl")) { return controls[1]; }
		if(controlType.equals("javax.microedition.media.control.MIDIControl")) { return controls[2]; }
		return null;
	}

	public Control[] getControls()
	{
		return controls;
	}

	// Players //

	private class audioplayer
	{
		public void start() {  }
		public void stop() {  }
		public void setLoopCount(int count) {  }
		public long setMediaTime(long now) { return now; }
		public long getMediaTime() { return 0; }
		public boolean isRunning() { return false; }
	}

	private class midiPlayer extends audioplayer
	{
		private Sequencer midi;

		private int loops = 0;

		public midiPlayer(InputStream stream)
		{
			try
			{
				midi = MidiSystem.getSequencer();
				midi.open();
				midi.setSequence(stream);
				state = Player.PREFETCHED;
			}
			catch (Exception e) { }
		}

		public void start()
		{
			midi.start();
			state = Player.STARTED;
			notifyListeners(PlayerListener.STARTED, new Long(0));
		}

		public void stop()
		{
			midi.stop();
			state = Player.REALIZED;
		}
		public void setLoopCount(int count)
		{
			loops = count;
			midi.setLoopCount(count);
		}
		public long setMediaTime(long now)
		{
			try
			{
				midi.setTickPosition(now);
			}
			catch (Exception e) { }
			return now;
		}
		public long getMediaTime()
		{
			return 0;
		}
		public boolean isRunning()
		{
			return midi.isRunning();
		}
	}

	private class wavPlayer extends audioplayer
	{

		private AudioInputStream wavStream;
		private Clip wavClip;

		private int loops = 0;

		private Long time = new Long(0);

		public wavPlayer(InputStream stream)
		{
			try
			{
				wavStream = AudioSystem.getAudioInputStream(stream);
		        wavClip = AudioSystem.getClip();
				wavClip.open(wavStream);
				state = Player.PREFETCHED;
			}
			catch (Exception e) { }
		}

		public void start()
		{
			if(isRunning()) { wavClip.setFramePosition(0); }
			time = wavClip.getMicrosecondPosition();
			wavClip.start();
			state = Player.STARTED;
			notifyListeners(PlayerListener.STARTED, time);
		}

		public void stop()
		{
			wavClip.stop();
			time = wavClip.getMicrosecondPosition();
			state = Player.PREFETCHED;
			notifyListeners(PlayerListener.STOPPED, time);
		}

		public void setLoopCount(int count)
		{
			loops = count;
			wavClip.loop(count);
		}

		public long setMediaTime(long now)
		{
			wavClip.setMicrosecondPosition(now);
			return now;
		}
		public long getMediaTime()
		{
			return wavClip.getMicrosecondPosition();
		}

		public boolean isRunning()
		{
			return wavClip.isRunning();
		}
	}

	// Controls //

	private class midiControl implements javax.microedition.media.control.MIDIControl
	{
		public int[] getBankList(boolean custom) { return new int[]{}; }

		public int getChannelVolume(int channel) { return 0; }

		public java.lang.String getKeyName(int bank, int prog, int key) { return ""; }

		public int[] getProgram(int channel) { return new int[]{}; }

		public int[] getProgramList(int bank) { return new int[]{}; }

		public java.lang.String getProgramName(int bank, int prog) { return ""; }

		public boolean isBankQuerySupported() { return false; }

		public int longMidiEvent(byte[] data, int offset, int length) { return 0; }

		public void setChannelVolume(int channel, int volume) {  }

		public void setProgram(int channel, int bank, int program) {  }

		public void shortMidiEvent(int type, int data1, int data2) {  }
	}

	private class volumeControl implements javax.microedition.media.control.VolumeControl
	{
		private int level = 100;
		private boolean muted = false;

		public int getLevel() { return level; }

		public boolean isMuted() { return muted; }

		public int setLevel(int value) { level = value; return level; }

		public void setMute(boolean mute) { muted = mute; }
	}

	private class tempoControl implements javax.microedition.media.control.TempoControl
	{
		int tempo = 5000;
		int rate = 5000;

		public int getTempo() { return tempo; }

		public int setTempo(int millitempo) { tempo = millitempo; return tempo; }

		// RateControl interface
		public int getMaxRate() { return rate; }

		public int getMinRate() { return rate; }

		public int getRate() { return rate; }

		public int setRate(int millirate) { rate=millirate; return rate; }
	}
}
