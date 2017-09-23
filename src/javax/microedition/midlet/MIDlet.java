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
package javax.microedition.midlet;

import java.util.HashMap;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.pki.*;
import javax.microedition.rms.*;

public abstract class MIDlet
{

	public HashMap<String, String> properties = new HashMap<String, String>(32); 

	private Display display = new Display();

	protected MIDlet()
	{
		System.out.println("Create MIDlet");
	}


	public final int checkPermission(String permission)
	{
		// 0 - denied; 1 - allowed; -1 unknown
		System.out.println("checkPermission: "+permission);
		return -1;
	}

	protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

	public final String getAppProperty(String key)
	{ 
		//System.out.print("getAppProperty: " + key);
		if(properties.containsKey(key))
		{
			//System.out.println(" "+properties.get(key));
			return properties.get(key);
		}
		//System.out.println(" null");
		return null;
	}

	public final void notifyDestroyed()
	{ 
		System.out.println("MIDlet sent Destroyed Notification");
		System.exit(0);
	}

	public final void notifyPaused() { }

	protected abstract void pauseApp();

	public final boolean platformRequest(String URL) { return false; }

	public final void resumeRequest() { }

	protected abstract void startApp() throws MIDletStateChangeException;

	public Display getDisplay() { return display; }

}
