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
import java.awt.image.BufferedImage;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.net.URL;

public class Libretro
{
	private int lcdWidth;
	private int lcdHeight;

	private Runnable painter;

	private BufferedImage surface;
	private Graphics2D gc;

	private Config config;
	private boolean useNokiaControls = false;
	private boolean useSiemensControls = false;
	private boolean useMotorolaControls = false;
	private boolean rotateDisplay = false;
	private boolean soundEnabled = true;
	private int limitFPS = 0;

	private boolean[] pressedKeys = new boolean[128];

	private byte[] frameBuffer = new byte[800*800*3];
	private byte[] frameHeader = new byte[]{(byte)0xFE, 0, 0, 0, 0, 0};

	private int mousex;
	private int mousey;

	/* 
	 * StringBuilder used to get the updated configs from the libretro core
	 * String[] used to tokenize each setting as its own string.
	 */
	private StringBuilder cfgs;
	String[] cfgtokens;

	LibretroIO lio;

	public static void main(String args[])
	{
		Libretro app = new Libretro(args);
	}

	public Libretro(String args[])
	{
		lcdWidth  = 240;
		lcdHeight = 320;

		/* 
		 * Checks if the arguments were received from the commandline -> width, height, rotate, phonetype, fps, sound, ...
		 * 
		 * NOTE:
		 * Due to differences in how linux and win32 pass their cmd arguments, we can't explictly check for a given size
		 * on the argv array. Linux includes the "java", "-jar" and "path/to/freej2me" into the array while WIN32 doesn't.
		 */
		lcdWidth =  Integer.parseInt(args[0]);
		lcdHeight = Integer.parseInt(args[1]);

		if(Integer.parseInt(args[2]) == 1) { rotateDisplay = true; }

		if(Integer.parseInt(args[3]) == 1)      { useNokiaControls = true;    }
		else if(Integer.parseInt(args[3]) == 2) { useSiemensControls = true;  }
		else if(Integer.parseInt(args[3]) == 3) { useMotorolaControls = true; }

		limitFPS = Integer.parseInt(args[4]);

		if(Integer.parseInt(args[5]) == 0) { soundEnabled = false; }

		/* Once it finishes parsing all arguments, it's time to set up freej2me-lr */

		surface = new BufferedImage(lcdWidth, lcdHeight, BufferedImage.TYPE_INT_ARGB); // libretro display
		gc = (Graphics2D)surface.getGraphics();

		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));

		config = new Config();
		config.onChange = new Runnable() { public void run() { settingsChanged(); } };

		lio = new LibretroIO();

		lio.start();

		painter = new Runnable()
		{
			public void run()
			{
				try
				{
					gc.drawImage(Mobile.getPlatform().getLCD(), 0, 0, lcdWidth, lcdHeight, null);
				}
				catch (Exception e) { }
			}
		};
		
		Mobile.getPlatform().setPainter(painter);

		System.out.println("+READY");
		System.out.flush();
	}

	private class LibretroIO
	{
		private Timer keytimer;
		private TimerTask keytask;

		public void start()
		{
			keytimer = new Timer();
			keytask = new LibretroTimerTask();
			keytimer.schedule(keytask, 0, 1);
		}

		public void stop()
		{
			keytimer.cancel();
		}

		private class LibretroTimerTask extends TimerTask
		{
			private int bin;
			private int[] din = new int[5];
			private int count = 0;
			private int code;
			private int mobikey;
			private StringBuilder path;
			private URL url;

			public void run()
			{
				try // to read keys
				{
					while(true)
					{
						bin = System.in.read();
						if(bin==-1) { return; }
						//System.out.print(" "+bin);
						din[count] = (int)(bin & 0xFF);
						count++;
						if (count==5)
						{
							count = 0;
							code = (din[1]<<24) | (din[2]<<16) | (din[3]<<8) | din[4];
							switch(din[0])
							{
								case 0: // keyboard key up
									mobikey = getMobileKey(code);
									if (mobikey != 0)
									{
										keyUp(mobikey);
									}
								break;

								case 1:	// keyboard key down
									mobikey = getMobileKey(code);
									if (mobikey != 0)
									{
										keyDown(mobikey);
									}
								break;

								case 2:	// joypad key up
									mobikey = getMobileKeyJoy(code);
									if (mobikey != 0)
									{
										keyUp(mobikey);
									}
								break;

								case 3: // joypad key down
									mobikey = getMobileKeyJoy(code);
									if (mobikey != 0)
									{
										keyDown(mobikey);
									}
								break;

								case 4: // mouse up
									mousex = (din[1]<<8) | din[2];
									mousey = (din[3]<<8) | din[4];
									if(!rotateDisplay)
									{
										Mobile.getPlatform().pointerReleased(mousex, mousey);
									}
									else
									{
										Mobile.getPlatform().pointerReleased(lcdWidth-mousey, mousex);
									}
								break;

								case 5: // mouse down
									mousex = (din[1]<<8) | din[2];
									mousey = (din[3]<<8) | din[4];
									if(!rotateDisplay)
									{
										Mobile.getPlatform().pointerPressed(mousex, mousey);
									}
									else
									{
										Mobile.getPlatform().pointerPressed(lcdWidth-mousey, mousex);
									}
								break;

								case 6: // mouse drag
									mousex = (din[1]<<8) | din[2];
									mousey = (din[3]<<8) | din[4];
									if(!rotateDisplay)
									{
										Mobile.getPlatform().pointerDragged(mousex, mousey);
									}
									else
									{
										Mobile.getPlatform().pointerDragged(lcdWidth-mousey, mousex);
									}
								break;

								case 10: // load jar
									path = new StringBuilder();
									for(int i=0; i<code; i++)
									{
										bin = System.in.read();
										path.append((char)bin);
									}
									url = (new File(path.toString())).toURI().toURL();
									if(Mobile.getPlatform().loadJar(url.toString()))
									{
										// Check config
										config.init();

										/* Override configs with the ones passed through commandline */
										config.settings.put("width",  ""+lcdWidth);
										config.settings.put("height", ""+lcdHeight);

										if(rotateDisplay)   { config.settings.put("rotate", "on");  }
										if(!rotateDisplay)  { config.settings.put("rotate", "off"); }

										if(useNokiaControls)         { config.settings.put("phone", "Nokia");    }
										else if(useSiemensControls)  { config.settings.put("phone", "Siemens");  }
										else if(useMotorolaControls) { config.settings.put("phone", "Motorola"); }
										else                         { config.settings.put("phone", "Standard"); }

										if(soundEnabled)   { config.settings.put("sound", "on");  }
										if(!soundEnabled)  { config.settings.put("sound", "off"); }

										config.settings.put("fps", ""+limitFPS);

										config.saveConfig();
										settingsChanged();

										// Run jar
										Mobile.getPlatform().runJar();
									}
									else
									{
										System.out.println("Couldn't load jar...");
										System.exit(0);
									}
								break;

								case 11: // set save path //
									path = new StringBuilder();
									for(int i=0; i<code; i++)
									{
										bin = System.in.read();
										path.append((char)bin);
									}
									Mobile.getPlatform().dataPath = path.toString();
								break;

								case 13:
									/* Received updated settings from libretro core */
									cfgs = new StringBuilder();
									for(int i=0; i<code; i++)
									{
										bin = System.in.read();
										cfgs.append((char)bin);
									}
									String cfgvars = cfgs.toString();
									/* Tokens: [0]="FJ2ME_LR_OPTS:", [1]=width, [2]=height, [3]=rotate, [4]=phone, [5]=fps, ... */
									cfgtokens = cfgvars.split("[| x]", 0);
									/* 
									 * cfgtokens[0] is the string used to indicate that the 
									 * received string is a config update. Only useful for debugging, 
									 * but better leave it in there as we might make adjustments later.
									 */
									config.settings.put("width",  ""+Integer.parseInt(cfgtokens[1]));
									config.settings.put("height", ""+Integer.parseInt(cfgtokens[2]));

									if(Integer.parseInt(cfgtokens[3])==1) { config.settings.put("rotate", "on");  }
									if(Integer.parseInt(cfgtokens[3])==0) { config.settings.put("rotate", "off"); }

									if(Integer.parseInt(cfgtokens[4])==0) { config.settings.put("phone", "Standard"); }
									if(Integer.parseInt(cfgtokens[4])==1) { config.settings.put("phone", "Nokia");    }
									if(Integer.parseInt(cfgtokens[4])==2) { config.settings.put("phone", "Siemens");  }
									if(Integer.parseInt(cfgtokens[4])==3) { config.settings.put("phone", "Motorola"); }

									config.settings.put("fps", ""+cfgtokens[5]);

									if(Integer.parseInt(cfgtokens[6])==1) { config.settings.put("sound", "on");  }
									if(Integer.parseInt(cfgtokens[6])==0) { config.settings.put("sound", "off"); }

									config.saveConfig();
									settingsChanged();
								break;
								
								case 15:
									// Send Frame Libretro //
									try
									{
										int[] data;
										if(config.isRunning)
										{
											data = config.getLCD().getRGB(0, 0, lcdWidth, lcdHeight, null, 0, lcdWidth);
										}
										else
										{
											data = surface.getRGB(0, 0, lcdWidth, lcdHeight, null, 0, lcdWidth);
											if(limitFPS>0)
											{
												Thread.sleep(limitFPS);
											}
										}
										int bufferLength = data.length*3;
										int cb = 0;
										for(int i=0; i<data.length; i++)
										{
											frameBuffer[cb]   = (byte)((data[i]>>16)&0xFF);
											frameBuffer[cb+1] = (byte)((data[i]>>8)&0xFF);
											frameBuffer[cb+2] = (byte)((data[i])&0xFF);
											cb+=3;
										}
										//frameHeader[0] = (byte)0xFE;
										frameHeader[1] = (byte)((lcdWidth>>8)&0xFF);
										frameHeader[2] = (byte)((lcdWidth)&0xFF);
										frameHeader[3] = (byte)((lcdHeight>>8)&0xFF);
										frameHeader[4] = (byte)((lcdHeight)&0xFF);
										//frameHeader[5] = rotate - set from config
										System.out.write(frameHeader, 0, 6);
										System.out.write(frameBuffer, 0, bufferLength);
										System.out.flush();
									}
									catch (Exception e)
									{
										System.out.print("Error sending frame: "+e.getMessage());
										System.exit(0);
									}
								break;
							}
							//System.out.println(" ("+code+") <- Key");
							//System.out.flush();
						}
					}
				}
				catch (Exception e) { System.exit(0); }
			}
		} // timer
	} // LibretroIO

	private void settingsChanged()
	{
		int w = Integer.parseInt(config.settings.get("width"));
		int h = Integer.parseInt(config.settings.get("height"));

		limitFPS = Integer.parseInt(config.settings.get("fps"));
		if(limitFPS>0) { limitFPS = 1000 / limitFPS; }

		String sound = config.settings.get("sound");
		Mobile.sound = false;
		if(sound.equals("on")) { Mobile.sound = true; }

		String phone = config.settings.get("phone");
		useNokiaControls = false;
		useSiemensControls = false;
		useMotorolaControls = false;
		Mobile.nokia = false;
		Mobile.siemens = false;
		Mobile.motorola = false;
		if(phone.equals("Nokia")) { Mobile.nokia = true; useNokiaControls = true; }
		if(phone.equals("Siemens")) { Mobile.siemens = true; useSiemensControls = true; }
		if(phone.equals("Motorola")) { Mobile.motorola = true; useMotorolaControls = true; }

		String rotate = config.settings.get("rotate");
		if(rotate.equals("on")) { rotateDisplay = true; frameHeader[5] = (byte)1; }
		if(rotate.equals("off")) { rotateDisplay = false; frameHeader[5] = (byte)0; }


		if(lcdWidth != w || lcdHeight != h)
		{
			lcdWidth = w;
			lcdHeight = h;
			Mobile.getPlatform().resizeLCD(w, h);
			surface = new BufferedImage(lcdWidth, lcdHeight, BufferedImage.TYPE_INT_ARGB); // libretro display
			gc = (Graphics2D)surface.getGraphics();
		}
	}

	private void keyDown(int key)
	{
		int mobikeyN = (key + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
		if(config.isRunning)
		{
			config.keyPressed(key);
		}
		else
		{
			if (pressedKeys[mobikeyN] == false)
			{
				Mobile.getPlatform().keyPressed(key);
			}
			else
			{
				Mobile.getPlatform().keyRepeated(key);
			}
		}
		pressedKeys[mobikeyN] = true;
	}

	private void keyUp(int key)
	{
		int mobikeyN = (key + 64) & 0x7F; //Normalized value for indexing the pressedKeys array
		if(!config.isRunning)
		{
			Mobile.getPlatform().keyReleased(key);
		}
		pressedKeys[mobikeyN] = false;
	}

	private int getMobileKey(int keycode)
	{
		if(useNokiaControls)
		{
			switch(keycode)
			{
				case 273: return Mobile.NOKIA_UP; // Up
				case 274: return Mobile.NOKIA_DOWN; // Down
				case 276: return Mobile.NOKIA_LEFT; // Left
				case 275: return Mobile.NOKIA_RIGHT; // Right
				case 13: return Mobile.NOKIA_SOFT3; // Middle
			}
		}
		if(useSiemensControls)
		{
			switch(keycode)
			{
				case 273: return Mobile.SIEMENS_UP;
				case 274: return Mobile.SIEMENS_DOWN;
				case 276: return Mobile.SIEMENS_LEFT;
				case 275: return Mobile.SIEMENS_RIGHT;
				case 113: return Mobile.SIEMENS_SOFT1;
				case 119: return Mobile.SIEMENS_SOFT2;
				case 91: return Mobile.SIEMENS_SOFT1;
				case 93: return Mobile.SIEMENS_SOFT2;
				case 13: return Mobile.SIEMENS_FIRE;
			}
		}
		if(useMotorolaControls)
		{
			switch(keycode)
			{
				case 273: return Mobile.MOTOROLA_UP;
				case 274: return Mobile.MOTOROLA_DOWN;
				case 276: return Mobile.MOTOROLA_LEFT;
				case 275: return Mobile.MOTOROLA_RIGHT;
				case 113: return Mobile.MOTOROLA_SOFT1;
				case 119: return Mobile.MOTOROLA_SOFT2;
				case 91: return Mobile.MOTOROLA_SOFT1;
				case 93: return Mobile.MOTOROLA_SOFT2;
				case 13: return Mobile.MOTOROLA_FIRE;
			}
		}

		switch(keycode)
		{
			case 48: return Mobile.KEY_NUM0;
			case 49: return Mobile.KEY_NUM1;
			case 50: return Mobile.KEY_NUM2;
			case 51: return Mobile.KEY_NUM3;
			case 52: return Mobile.KEY_NUM4;
			case 53: return Mobile.KEY_NUM5;
			case 54: return Mobile.KEY_NUM6;
			case 55: return Mobile.KEY_NUM7;
			case 56: return Mobile.KEY_NUM8;
			case 57: return Mobile.KEY_NUM9;
			case 42: return Mobile.KEY_STAR;
			case 35: return Mobile.KEY_POUND;

			// Arrow U,D,L,R
			case 273: return Mobile.KEY_NUM2;
			case 274: return Mobile.KEY_NUM8;
			case 276: return Mobile.KEY_NUM4;
			case 275: return Mobile.KEY_NUM6;

			// Inverted Numpad
			case 256: return Mobile.KEY_NUM0;
			case 257: return Mobile.KEY_NUM7;
			case 258: return Mobile.KEY_NUM8;
			case 259: return Mobile.KEY_NUM9;
			case 260: return Mobile.KEY_NUM4;
			case 261: return Mobile.KEY_NUM5;
			case 262: return Mobile.KEY_NUM6;
			case 263: return Mobile.KEY_NUM1;
			case 264: return Mobile.KEY_NUM2;
			case 265: return Mobile.KEY_NUM3;

			// Enter
			case 13: return Mobile.KEY_NUM5;

			case 113: return Mobile.NOKIA_SOFT1; // q
			case 119: return Mobile.NOKIA_SOFT2; // w
			case 101: return Mobile.KEY_STAR;  // e
			case 114: return Mobile.KEY_POUND; // r

			case 91: return Mobile.NOKIA_SOFT1; // [
			case 93: return Mobile.NOKIA_SOFT2; // ]

			// ESC - Config Menu
			//case 27: config.start(); /* This menu won't be available on the Libretro frontend */

		}
		return 0;
	}

	private int getMobileKeyJoy(int keycode)
	{
		if(useNokiaControls)
		{
			switch(keycode)
			{
				case 0: return Mobile.NOKIA_UP; // Up
				case 1: return Mobile.NOKIA_DOWN; // Down
				case 2: return Mobile.NOKIA_LEFT; // Left
				case 3: return Mobile.NOKIA_RIGHT; // Right
			}
		}
		if(useSiemensControls)
		{
			switch(keycode)
			{
				case 0: return Mobile.SIEMENS_UP; // Up
				case 1: return Mobile.SIEMENS_DOWN; // Down
				case 2: return Mobile.SIEMENS_LEFT; // Left
				case 3: return Mobile.SIEMENS_RIGHT; // Right
				case 8: return Mobile.SIEMENS_SOFT2; // Start
				case 9: return Mobile.SIEMENS_SOFT1; // Select
			}
		}
		if(useMotorolaControls)
		{
			switch(keycode)
			{
				case 0: return Mobile.MOTOROLA_UP; // Up
				case 1: return Mobile.MOTOROLA_DOWN; // Down
				case 2: return Mobile.MOTOROLA_LEFT; // Left
				case 3: return Mobile.MOTOROLA_RIGHT; // Right
				case 8: return Mobile.MOTOROLA_SOFT2; // Start
				case 9: return Mobile.MOTOROLA_SOFT1; // Select
			}
		}

		switch(keycode)
		{
			case 0: return Mobile.KEY_NUM2; // Up
			case 1: return Mobile.KEY_NUM8; // Down
			case 2: return Mobile.KEY_NUM4; // Left
			case 3: return Mobile.KEY_NUM6; // Right
			case 4: return Mobile.KEY_NUM9; // A
			case 5: return Mobile.KEY_NUM7; // B
			case 6: return Mobile.KEY_NUM0; // X
			case 7: return Mobile.KEY_NUM5; // Y
			case 8: return Mobile.NOKIA_SOFT2; // Start
			case 9: return Mobile.NOKIA_SOFT1; // Select
			case 10: return Mobile.KEY_NUM1; // L
			case 11: return Mobile.KEY_NUM3; // R
			case 12: return Mobile.KEY_STAR; // L2
			case 13: return Mobile.KEY_POUND; // R2
		}
		return Mobile.KEY_NUM5;
	}
}
