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

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import javax.microedition.lcdui.Graphics;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;

public class Config
{
	public boolean isRunning = false;

	private PlatformImage lcd;
	private Graphics gc;
	private int width;
	private int height;

	private ArrayList<String[]> menu;
	private int menuid = 0;
	private int itemid = 0;

	private File file;
	private String configPath = "";
	private String configFile = "";

	public Runnable onChange;

	HashMap<String, String> settings = new HashMap<String, String>(4);

	public Config()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;

		lcd = new PlatformImage(width, height);
		gc = lcd.getGraphics();

		menu = new ArrayList<String[]>();
		menu.add(new String[]{"Resume Game", "Display Size", "Sound", "Limit FPS", "Phone", "Rotate", "Exit"}); // 0 - Main Menu
		menu.add(new String[]{"96x65","96x96","104x80","128x128","132x176","128x160","176x208","176x220", "208x208", "240x320", "320x240", "240x400", "352x416", "360x640", "640x360" ,"480x800", "800x480"}); // 1 - Size
		menu.add(new String[]{"Quit", "Main Menu"}); // 2 - Restart Notice
		menu.add(new String[]{"On", "Off"}); // 3 - sound
		menu.add(new String[]{"Standard", "Nokia", "Siemens","Motorola"}); // 4 - Phone 
		menu.add(new String[]{"On", "Off"}); // 5 - rotate 
		menu.add(new String[]{"Auto", "60 - Fast", "30 - Slow", "15 - Turtle"}); // 6 - FPS


		onChange = new Runnable()
		{
			public void run()
			{
				// placeholder
			}
		};
	}

	public void init()
	{
		String appname = Mobile.getPlatform().loader.suitename;
		configPath = Mobile.getPlatform().dataPath + "./config/"+appname;
		configFile = configPath + "/game.conf";
		// Load Config //
		try
		{
			Files.createDirectories(Paths.get(configPath));
		}
		catch (Exception e)
		{
			System.out.println("Problem Creating Config Path "+configPath);
			System.out.println(e.getMessage());
		}

		try // Check Config File
		{
			file = new File(configFile);
			if(!file.exists())
			{
				file.createNewFile();
				settings.put("width", ""+width);
				settings.put("height", ""+height);
				settings.put("sound", "on");
				settings.put("phone", "Standard");
				settings.put("rotate", "off");
				settings.put("fps", "0");
				saveConfig();
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem Opening Config "+configFile);
			System.out.println(e.getMessage());
		}

		try // Read Records
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			String[] parts;
			while((line = reader.readLine())!=null)
			{
				parts = line.split(":");
				if(parts.length==2)
				{
					parts[0] = parts[0].trim();
					parts[1] = parts[1].trim();
					if(parts[0]!="" && parts[1]!="")
					{
						//Compatibility with the deprecated "nokia" boolean setting
						if(parts[0].equals("nokia"))
						{
							parts[0] = "phone";
							if(parts[1].equals("on")) { parts[1] = "Nokia"; } else { parts[0] = "Standard"; }
						}
						settings.put(parts[0], parts[1]);
					}
				}
			}
			if(!settings.containsKey("width")) { settings.put("width", ""+width); }
			if(!settings.containsKey("height")) { settings.put("height", ""+height); }
			if(!settings.containsKey("sound")) { settings.put("sound", "on"); }
			if(!settings.containsKey("phone")) { settings.put("phone", "Standard"); }
			if(!settings.containsKey("rotate")) { settings.put("rotate", "off"); }
			if(!settings.containsKey("fps")) { settings.put("fps", "0"); }

			int w = Integer.parseInt(settings.get("width"));
			int h = Integer.parseInt(settings.get("height"));
			if(width!=w || height!=h)
			{
				width = w;
				height = h;
				lcd = new PlatformImage(width, height);
				gc = lcd.getGraphics();
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem Reading Config: "+configFile);
			System.out.println(e.getMessage());
		}

	}

	public void saveConfig()
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(file);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fout));

			for (String key : settings.keySet())
			{
				writer.write(key+":"+settings.get(key)+"\n");
			}
			writer.close();
		}
		catch (Exception e)
		{
			System.out.println("Problem Opening Config "+configFile);
			System.out.println(e.getMessage());
		}
	}

	public void start()
	{
		isRunning = true;
		render();
		Mobile.getPlatform().painter.run();
	}

	public void stop()
	{
		isRunning = false;
		Mobile.getPlatform().painter.run();
	}

	public void keyPressed(int key)
	{
		switch(key)
		{
			case Mobile.KEY_NUM2: itemid--; break;
			case Mobile.KEY_NUM8: itemid++; break;
			case Mobile.KEY_NUM5: doMenuAction(); break;
			default:
				if(settings.get("phone").equals("Nokia"))
				{
					switch(key)
					{
						case Mobile.NOKIA_UP: itemid--; break;
						case Mobile.NOKIA_DOWN: itemid++; break;
						case Mobile.NOKIA_SOFT1: menuid=0; break;
						case Mobile.NOKIA_SOFT3: doMenuAction(); break;
					}
				}
				if(settings.get("phone").equals("Siemens"))
				{
					switch(key)
					{
						case Mobile.SIEMENS_UP: itemid--; break;
						case Mobile.SIEMENS_DOWN: itemid++; break;
						case Mobile.SIEMENS_SOFT1: menuid=0; break;
					}
				}
				if(settings.get("phone").equals("Motorola"))
				{
					switch(key)
					{
						case Mobile.MOTOROLA_UP: itemid--; break;
						case Mobile.MOTOROLA_DOWN: itemid++; break;
						case Mobile.MOTOROLA_SOFT1: menuid=0; break;
						case Mobile.MOTOROLA_FIRE: doMenuAction(); break;
					}
				}
		}
		if (menuid<0) { menuid=0; itemid=0; }
		if (itemid>=menu.get(menuid).length) { itemid = menu.get(menuid).length-1; }
		if (itemid<0) { itemid = 0; }

		render();
	}

	public void keyReleased(int key) { }
	public void mousePressed(int key) { }
	public void mouseReleased(int key) { }

	public BufferedImage getLCD()
	{
		return lcd.getCanvas();
	}

	public void render()
	{
		String label;
		String title = "Game Options";

		switch(menuid)
		{
			case 1: title = "Screen Size"; break;
			case 2: title = "Restart Required"; break;
			case 3: title = "Sound"; break;
		}

		gc.setColor(0x000080);
		gc.fillRect(0,0,width,height);
		gc.setColor(0xFFFFFF);
		gc.drawString(title, width/2, 2, Graphics.HCENTER);
		gc.drawLine(0, 20, width, 20);
		gc.drawLine(0, height-20, width, height-20);

		/*
		gc.setColor(0x00FF00);
		gc.drawRect(0, 0, 128, 128);
		height = 128;
		width = 128;
		*/

		if (menuid>0)
		{
			gc.setColor(0xFFFFFF);
			gc.drawString("Back", 3, height-17, Graphics.LEFT);
		}

		String[] t = menu.get(menuid);

		int ah = (int)((height-50)/(t.length+1));
		if(ah<15) { ah=15; }

		int space = 0;
		if(ah>15) { space = (ah-15) / 2; }

		int max = (int)Math.floor((height-50)/ah);
		int page = (int)Math.floor(itemid/max);
		int start = (int)(max*page);
		int pages = (int)Math.ceil(t.length/max);

		if(pages>=1)
		{
			gc.setColor(0xFFFFFF);
			gc.drawString("Page "+(page+1)+" of "+(pages+1), width-3, height-17, Graphics.RIGHT);
		}

		for(int i=start; (i<(start+max))&(i<t.length); i++)
		{
			label = t[i];
			if(menuid==0 && i>1 && i<7)
			{
				switch(i)
				{
					case 2: label = label+": "+settings.get("sound"); break;
					case 3: label = label+": "+settings.get("fps"); break;
					case 4: label = label+": "+settings.get("phone"); break;
					case 5: label = label+": "+settings.get("rotate"); break;
				}
			}
			if(i==itemid)
			{
				gc.setColor(0xFFFF00);
				gc.drawString("> "+label+" <", width/2, (25+space)+(ah*(i-start)), Graphics.HCENTER);
			}
			else
			{
				gc.setColor(0xFFFFFF);
				gc.drawString(label, width/2, (25+space)+(ah*(i-start)), Graphics.HCENTER);
			}
		}

		Mobile.getPlatform().painter.run();
	}

	private void doMenuAction()
	{
		switch(menuid)
		{
			case 0:  // Main Menu
				switch(itemid)
				{
					case 0: stop(); break; // resume
					case 1: menuid=1; itemid=0; break; // display size
					case 2: menuid=3; itemid=0; break; // sound
					case 3: menuid=6; itemid=0; break; // fps
					case 4: menuid=4; itemid=0; break; // phone
					case 5: menuid=5; itemid=0; break; // rotate
					case 6: System.exit(0); break;
				}
			break;

			case 1: // Display Size
				String[] t = menu.get(1)[itemid].split("x");

				updateDisplaySize(Integer.parseInt(t[0]), Integer.parseInt(t[1]));

				menuid=2; itemid=0;
			break;

			case 2: // Restart Required Notice
				switch(itemid)
				{
					case 0: System.exit(0); break;
					case 1: menuid=0; itemid=0;
				}
			break;

			case 3: // Turn Sound On/Off
				if(itemid==0) { updateSound("on"); }
				if(itemid==1) { updateSound("off"); }
				menuid=0; itemid=0;
			break;

			case 4: // Switch Phone Mode
				if(itemid==0) { updatePhone("Standard"); }
				if(itemid==1) { updatePhone("Nokia"); }
				if(itemid==2) { updatePhone("Siemens"); }
				if(itemid==3) { updatePhone("Motorola"); }
				menuid=0; itemid=0;
			break;

			case 5: // Turn Rotation On/Off
				if(itemid==0) { updateRotate("on"); }
				if(itemid==1) { updateRotate("off"); }
				menuid=0; itemid=0;
			break;

			case 6: // FPS
				if(itemid==0) { updateFPS("0"); }
				if(itemid==1) { updateFPS("60"); }
				if(itemid==2) { updateFPS("30"); }
				if(itemid==3) { updateFPS("15"); }
				menuid=0; itemid=0;
			break;

		}

		render();
	}

	private void updateDisplaySize(int w, int h)
	{
		settings.put("width", ""+w);
		settings.put("height", ""+h);
		saveConfig();
		onChange.run();
		width = w;
		height = h;
		lcd = new PlatformImage(width, height);
		gc = lcd.getGraphics();

	}

	private void updateSound(String value)
	{
		System.out.println("Config: sound "+value);
		settings.put("sound", value);
		saveConfig();
		onChange.run();
	}

	private void updatePhone(String value)
	{
		System.out.println("Config: phone "+value);
		settings.put("phone", value);
		saveConfig();
		onChange.run();
	}

	private void updateRotate(String value)
	{
		System.out.println("Config: rotate "+value);
		settings.put("rotate", value);
		saveConfig();
		onChange.run();
	}

	private void updateFPS(String value)
	{
		System.out.println("Config: fps "+value);
		settings.put("fps", value);
		saveConfig();
		onChange.run();
	}
}
