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
package org.recompile.freej2me;

import org.recompile.mobile.*;

import java.awt.Image;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder;

public class RetroPie
{

	public static void main(String[] args)
	{
		RetroPie app = new RetroPie(args);
	}

	private SDL sdl;

	private int lcdWidth;
	private int lcdHeight;

	private Runnable painter;

	public RetroPie(String args[])
	{
		lcdWidth = 240;
		lcdHeight = 320;

		if (args.length < 3)
		{
			System.out.println("Insufficient parameters provided");
			return;
		}
		lcdWidth = Integer.parseInt(args[1]);
		lcdHeight = Integer.parseInt(args[2]);

		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));

		painter = new Runnable()
		{
			public void run()
			{
				try
				{
					// Send Frame to SDL interface
					int[] data = Mobile.getPlatform().getLCD().getRGB(0, 0, lcdWidth, lcdHeight, null, 0, lcdWidth);
					byte[] frame = new byte[data.length * 3];
					int cb = 0;
					for(int i = 0; i < data.length; i++)
					{
						frame[cb + 0] = (byte)(data[i] >> 16);
						frame[cb + 1] = (byte)(data[i] >> 8);
						frame[cb + 2] = (byte)(data[i]);
						cb += 3;
					}
					sdl.frame.write(frame);
				}
				catch (Exception e) { }
			}
		};

		Mobile.getPlatform().setPainter(painter);

		String file = getFormattedLocation(args[0]);
		System.out.println(file);

		if(Mobile.getPlatform().loadJar(file))
		{
			// Check config

			// Start SDL
			sdl = new SDL();
			sdl.start(args);

			// Run jar
			Mobile.getPlatform().runJar();
		}
		else
		{
			System.out.println("Couldn't load jar...");
			System.exit(0);
		}
	}

	private static String getFormattedLocation(String loc)
	{
		if (loc.startsWith("file://") || loc.startsWith("http://"))
			return loc;

		File file = new File(loc);
		if(!file.exists() || file.isDirectory())
		{
			System.out.println("File not found...");
			System.exit(0);
		}

		return "file://" + file.getAbsolutePath();
	}

	private class SDL
	{
		private Timer keytimer;
		private TimerTask keytask;

		private Process proc;
		private InputStream keys;
		public OutputStream frame;

		public void start(String args[])
		{
			try
			{
				args[0] = "/usr/local/bin/sdl_interface";
				proc = new ProcessBuilder(args).start();

				keys = proc.getInputStream();
				frame = proc.getOutputStream();

				keytimer = new Timer();
				keytask = new SDLKeyTimerTask();
				keytimer.schedule(keytask, 0, 5);
			}
			catch (Exception e)
			{
				System.out.println("Failed to start sdl_interface");
				System.out.println(e.getMessage());
				System.exit(0);
			}
		}

		public void stop()
		{
			proc.destroy();
			keytimer.cancel();
		}

		private class SDLKeyTimerTask extends TimerTask
		{
			private int bin;
			private byte[] din = new byte[6];
			private int count = 0;
			private int code;

			public void run()
			{
				try // to read keys
				{
					while(true)
					{
						bin = keys.read();
						if(bin==-1) { return; }
						System.out.print(" "+bin);
						din[count] = (byte)(bin & 0xFF);
						count++;
						if (count==5)
						{
							count = 0;
							code = (din[1]<<24) | (din[2]<<16) | (din[3]<<8) | din[4];
							switch(din[0])
							{
								case 0: Mobile.getPlatform().keyReleased(getMobileKey(code)); break;
								case 1: Mobile.getPlatform().keyPressed(getMobileKey(code)); break;
								case 2: Mobile.getPlatform().keyReleased(getMobileKeyPad(code)); break;
								case 3: Mobile.getPlatform().keyPressed(getMobileKeyPad(code)); break;
								case 4: Mobile.getPlatform().keyReleased(getMobileKeyJoy(code)); break;
								case 5: Mobile.getPlatform().keyPressed(getMobileKeyJoy(code)); break;

							}
							System.out.println(" ("+code+") <- Key");
						}
					}
				}
				catch (Exception e) { }
			}
		} // timer

		private int getMobileKey(int keycode)
		{
			switch(keycode)
			{
				case 0x30: return Mobile.KEY_NUM0;
				case 0x31: return Mobile.KEY_NUM1;
				case 0x32: return Mobile.KEY_NUM2;
				case 0x33: return Mobile.KEY_NUM3;
				case 0x34: return Mobile.KEY_NUM4;
				case 0x35: return Mobile.KEY_NUM5;
				case 0x36: return Mobile.KEY_NUM6;
				case 0x37: return Mobile.KEY_NUM7;
				case 0x38: return Mobile.KEY_NUM8;
				case 0x39: return Mobile.KEY_NUM9;
				case 0x2A: return Mobile.KEY_STAR;
				case 0x23: return Mobile.KEY_POUND;

				case 0x40000052: return Mobile.KEY_NUM2;
				case 0x40000051: return Mobile.KEY_NUM8;
				case 0x40000050: return Mobile.KEY_NUM4;
				case 0x4000004F: return Mobile.KEY_NUM6;

				case 0x0D: return Mobile.KEY_NUM5;

				case 0x71: return Mobile.NOKIA_SOFT1;
				case 0x77: return Mobile.NOKIA_SOFT2;
				case 0x65: return Mobile.KEY_STAR;
				case 0x72: return Mobile.KEY_POUND;


				// Inverted Num Pad
				case 0x40000059: return Mobile.KEY_NUM7; // SDLK_KP_1
				case 0x4000005A: return Mobile.KEY_NUM8; // SDLK_KP_2
				case 0x4000005B: return Mobile.KEY_NUM9; // SDLK_KP_3
				case 0x4000005C: return Mobile.KEY_NUM4; // SDLK_KP_4
				case 0x4000005D: return Mobile.KEY_NUM5; // SDLK_KP_5
				case 0x4000005E: return Mobile.KEY_NUM6; // SDLK_KP_6
				case 0x4000005F: return Mobile.KEY_NUM1; // SDLK_KP_7
				case 0x40000060: return Mobile.KEY_NUM2; // SDLK_KP_8
				case 0x40000061: return Mobile.KEY_NUM3; // SDLK_KP_9
				case 0x40000062: return Mobile.KEY_NUM0; // SDLK_KP_0


				// F4 - Quit
				case -1: System.exit(0);

				// ESC - Quit
				case 0x1B: System.exit(0);

				case 112: ScreenShot.takeScreenshot();

				/*
				case : return Mobile.GAME_UP;
				case : return Mobile.GAME_DOWN;
				case : return Mobile.GAME_LEFT;
				case : return Mobile.GAME_RIGHT;
				case : return Mobile.GAME_FIRE;

				case : return Mobile.GAME_A;
				case : return Mobile.GAME_B;
				case : return Mobile.GAME_C;
				case : return Mobile.GAME_D;

				// Nokia //
				case : return Mobile.NOKIA_UP;
				case : return Mobile.NOKIA_DOWN;
				case : return Mobile.NOKIA_LEFT;
				case : return Mobile.NOKIA_RIGHT;
				case : return Mobile.NOKIA_SOFT1;
				case : return Mobile.NOKIA_SOFT2;
				case : return Mobile.NOKIA_SOFT3;
				*/
			}
			return keycode;
		}

		private int getMobileKeyPad(int keycode)
		{
			switch(keycode)
			{
				//  A:1 B:0 X: 3 Y:2 L:4 R:5 St:6 Sl:7
				case 0x03: return Mobile.KEY_NUM0;
				case 0x02: return Mobile.KEY_NUM5;
				case 0x00: return Mobile.KEY_STAR;
				case 0x01: return Mobile.KEY_POUND;

				case 0x04: return Mobile.KEY_NUM1;
				case 0x05: return Mobile.KEY_NUM3;

				case 0x06: return Mobile.NOKIA_SOFT1;
				case 0x07: return Mobile.NOKIA_SOFT2;
			}
			return keycode;
		}

		private int getMobileKeyJoy(int keycode)
		{
			switch(keycode)
			{
				case 0x04: return Mobile.KEY_NUM2;
				case 0x01: return Mobile.KEY_NUM4;
				case 0x02: return Mobile.KEY_NUM6;
				case 0x08: return Mobile.KEY_NUM8;
			}
			return keycode;
		}

	} // sdl
}
