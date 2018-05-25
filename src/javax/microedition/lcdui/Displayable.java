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
package javax.microedition.lcdui;

import java.util.ArrayList;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

public abstract class Displayable
{

	public PlatformImage platformImage;

	public int width = 0;

	public int height = 0;

	public boolean fullScreen = false;
	
	protected String title = "";

	protected ArrayList<Command> commands = new ArrayList<Command>();

	protected CommandListener commandlistener;

	public Ticker ticker;

	public Displayable()
	{
		width = Mobile.getPlatform().lcdWidth;
		height = Mobile.getPlatform().lcdHeight;
	}

	public void addCommand(Command cmd)
	{ 
		try
		{
			commands.add(cmd);	
		}
		catch (Exception e)
		{
			System.out.println("Problem Adding Command: "+e.getMessage());
		}
	}

	public void removeCommand(Command cmd) { commands.remove(cmd); }
	
	public int getWidth() { return width; }

	public int getHeight() { return height; }
	
	public String getTitle() { return title; }

	public void setTitle(String text) { title = text; }        

	public boolean isShown() { return true; }

	public Ticker getTicker() { return ticker; }

	public void setTicker(Ticker tick) { ticker = tick; }
	
	public void setCommandListener(CommandListener listener) { commandlistener = listener; }

	protected void sizeChanged(int width, int height) { }

	public Display getDisplay() { return Mobile.getDisplay(); }

	public ArrayList<Command> getCommands() { return commands; }


	public void keyPressed(int key) { }
	public void keyReleased(int key) { }
	public void pointerDragged(int x, int y) { }
	public void pointerPressed(int x, int y) { }
	public void pointerReleased(int x, int y) { }

	public void notifySetCurrent() { }

}
