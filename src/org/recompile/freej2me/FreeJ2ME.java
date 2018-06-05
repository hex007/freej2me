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

/*
	FreeJ2ME - AWT
*/

import org.recompile.mobile.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import javax.imageio.ImageIO;

public class FreeJ2ME
{
	public static void main(String args[])
	{
		FreeJ2ME app = new FreeJ2ME(args);
	}

	private Frame main;
	private int lcdWidth;
	private int lcdHeight;

	private LCD lcd;

	private int xborder;
	private int yborder;

	private PlatformImage img;

	private Config config;
	private boolean useNokiaControls = true;
	private boolean rotateDisplay = false;
	private int limitFPS = 0;

	public FreeJ2ME(String args[])
	{
		main = new Frame("FreeJ2ME");
		main.setSize(350,450);
		main.setBackground(new Color(0,0,64));
		try
		{
			main.setIconImage(ImageIO.read(main.getClass().getResourceAsStream("/org/recompile/icon.png")));	
		}
		catch (Exception e) { }

		main.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		// Setup Device //

		lcdWidth = 240;
		lcdHeight = 320;

		String jarfile = "";
		if(args.length>=1)
		{
			jarfile = args[0];
		}
		if(args.length>=3)
		{
			lcdWidth = Integer.parseInt(args[1]);
			lcdHeight = Integer.parseInt(args[2]);
		}

		Mobile.setPlatform(new MobilePlatform(lcdWidth, lcdHeight));

		lcd = new LCD();
		lcd.setFocusable(true);
		main.add(lcd);

		config = new Config();
		config.onChange = new Runnable() { public void run() { settingsChanged(); } };

		Mobile.getPlatform().setPainter(new Runnable()
		{
			public void run()
			{
				lcd.paint(lcd.getGraphics());
			}
		});

		lcd.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(config.isRunning)
				{
					config.keyPressed(getMobileKey(e.getKeyCode()));
				}
				else
				{
					Mobile.getPlatform().keyPressed(getMobileKey(e.getKeyCode()));
				}
			}

			public void keyReleased(KeyEvent e)
			{
				if(config.isRunning)
				{
					config.keyReleased(getMobileKey(e.getKeyCode()));
				}
				else
				{
					Mobile.getPlatform().keyReleased(getMobileKey(e.getKeyCode()));
				}
			}

			public void keyTyped(KeyEvent e) { }

		});

		lcd.addMouseListener(new MouseListener()
		{
			public void mousePressed(MouseEvent e)
			{
				int x = (int)((e.getX()-lcd.cx) * lcd.scalex);
				int y = (int)((e.getY()-lcd.cy) * lcd.scaley);
				Mobile.getPlatform().pointerPressed(x, y);
			}

			public void mouseReleased(MouseEvent e)
			{
				int x = (int)((e.getX()-lcd.cx) * lcd.scalex);
				int y = (int)((e.getY()-lcd.cy) * lcd.scaley);
				Mobile.getPlatform().pointerReleased(x, y);
			}

			public void mouseExited(MouseEvent e) { }
			public void mouseEntered(MouseEvent e) { }
			public void mouseClicked(MouseEvent e) { }

		});

		main.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				resize();
			}
		});

		main.setVisible(true);
		main.pack();

		resize();
		main.setSize(lcdWidth+xborder, lcdHeight+yborder);

		if(args.length<1)
		{
			FileDialog t = new FileDialog(main, "Open JAR File", FileDialog.LOAD);
			t.setFilenameFilter(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().endsWith(".jar");
				}
			});
			t.setVisible(true);
			jarfile = new File(t.getDirectory()+File.separator+t.getFile()).toURI().toString();
		}
		if(Mobile.getPlatform().loadJar(jarfile))
		{
			config.init();
			settingsChanged();

			Mobile.getPlatform().runJar();
		}
		else
		{
			System.out.println("Couldn't load jar...");
		}
	}

	private void settingsChanged()
	{
		int w = Integer.parseInt(config.settings.get("width"));
		int h = Integer.parseInt(config.settings.get("height"));

		limitFPS = Integer.parseInt(config.settings.get("fps"));
		if(limitFPS>0) { limitFPS = 1000 / limitFPS; }

		String sound = config.settings.get("sound");
		if(sound.equals("on")) { Mobile.getPlatform().sound = true; }
		if(sound.equals("off")) { Mobile.getPlatform().sound = false; }

		String nokia = config.settings.get("nokia");
		if(nokia.equals("on")) { useNokiaControls = true; }
		if(nokia.equals("off")) { useNokiaControls = false; }

		if(lcdWidth != w || lcdHeight != h)
		{
			lcdWidth = w;
			lcdHeight = h;

			Mobile.getPlatform().resizeLCD(w, h);

			resize();
			main.setSize(lcdWidth+xborder, lcdHeight+yborder);
		}
	}

	private int getMobileKey(int keycode)
	{
		if(useNokiaControls)
		{
			switch(keycode)
			{
				case KeyEvent.VK_UP: return Mobile.NOKIA_UP;
				case KeyEvent.VK_DOWN: return Mobile.NOKIA_DOWN;
				case KeyEvent.VK_LEFT: return Mobile.NOKIA_LEFT;
				case KeyEvent.VK_RIGHT: return Mobile.NOKIA_RIGHT;

			}
		}

		switch(keycode)
		{
			case KeyEvent.VK_0: return Mobile.KEY_NUM0;
			case KeyEvent.VK_1: return Mobile.KEY_NUM1;
			case KeyEvent.VK_2: return Mobile.KEY_NUM2;
			case KeyEvent.VK_3: return Mobile.KEY_NUM3;
			case KeyEvent.VK_4: return Mobile.KEY_NUM4;
			case KeyEvent.VK_5: return Mobile.KEY_NUM5;
			case KeyEvent.VK_6: return Mobile.KEY_NUM6;
			case KeyEvent.VK_7: return Mobile.KEY_NUM7;
			case KeyEvent.VK_8: return Mobile.KEY_NUM8;
			case KeyEvent.VK_9: return Mobile.KEY_NUM9;
			case KeyEvent.VK_ASTERISK: return Mobile.KEY_STAR;
			case KeyEvent.VK_NUMBER_SIGN: return Mobile.KEY_POUND;

			case KeyEvent.VK_NUMPAD0: return Mobile.KEY_NUM0;
			case KeyEvent.VK_NUMPAD7: return Mobile.KEY_NUM1;
			case KeyEvent.VK_NUMPAD8: return Mobile.KEY_NUM2;
			case KeyEvent.VK_NUMPAD9: return Mobile.KEY_NUM3;
			case KeyEvent.VK_NUMPAD4: return Mobile.KEY_NUM4;
			case KeyEvent.VK_NUMPAD5: return Mobile.KEY_NUM5;
			case KeyEvent.VK_NUMPAD6: return Mobile.KEY_NUM6;
			case KeyEvent.VK_NUMPAD1: return Mobile.KEY_NUM7;
			case KeyEvent.VK_NUMPAD2: return Mobile.KEY_NUM8;
			case KeyEvent.VK_NUMPAD3: return Mobile.KEY_NUM9;

			case KeyEvent.VK_UP: return Mobile.KEY_NUM2;
			case KeyEvent.VK_DOWN: return Mobile.KEY_NUM8;
			case KeyEvent.VK_LEFT: return Mobile.KEY_NUM4;
			case KeyEvent.VK_RIGHT: return Mobile.KEY_NUM6;

			case KeyEvent.VK_ENTER: return Mobile.KEY_NUM5;

			case KeyEvent.VK_Q: return Mobile.NOKIA_SOFT1;
			case KeyEvent.VK_W: return Mobile.NOKIA_SOFT2;
			case KeyEvent.VK_E: return Mobile.KEY_STAR;
			case KeyEvent.VK_R: return Mobile.KEY_POUND;

			case KeyEvent.VK_A: return -1;
			case KeyEvent.VK_Z: return -2;

			// Config //
			case KeyEvent.VK_ESCAPE: config.start();
		}
		return keycode;
	}

	private void resize()
	{
		xborder = main.getInsets().left+main.getInsets().right;
		yborder = main.getInsets().top+main.getInsets().bottom;

		double vw = (main.getWidth()-xborder)*1;
		double vh = (main.getHeight()-yborder)*1;

		double nw = lcdWidth;
		double nh = lcdHeight;

		nw = vw;
		nh = nw*((double)lcdHeight/(double)lcdWidth);

		if(nh>vh)
		{
			nh = vh;
			nw = nh*((double)lcdWidth/(double)lcdHeight);
		}

		lcd.updateScale((int)nw, (int)nh);
	}

	private class LCD extends Canvas
	{
		public int cx=0;
		public int cy=0;
		public int cw=240;
		public int ch=320;

		public double scalex=1;
		public double scaley=1;

		public void updateScale(int vw, int vh)
		{
			cx = (this.getWidth()-vw)/2;
			cy = (this.getHeight()-vh)/2;
			cw = vw;
			ch = vh;
			scalex = (double)lcdWidth/(double)vw;
			scaley = (double)lcdHeight/(double)vh;
		}

		public void paint(Graphics g)
		{
			try
			{
				Graphics2D cgc = (Graphics2D)this.getGraphics();
				if (config.isRunning)
				{
					g.drawImage(config.getLCD(), cx, cy, cw, ch, null);
				}
				else
				{
					g.drawImage(Mobile.getPlatform().getLCD(), cx, cy, cw, ch, null);
					if(limitFPS>0)
					{
						Thread.sleep(limitFPS);
					}
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}
