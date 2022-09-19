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
import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.pki.*;
import javax.microedition.rms.*;

public abstract class MIDlet
{

	public static HashMap<String, String> properties;

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

	public String getAppProperty(String key)
	{ 
		return properties.get(key);
	}

	public static void initAppProperties(HashMap<String, String> initProperties)
	{
		properties = initProperties;
	}

	public final void notifyDestroyed()
	{ 
		System.out.println("MIDlet sent Destroyed Notification");
		System.exit(0);
	}

	public final void notifyPaused() { }

	protected abstract void pauseApp();

	public final boolean platformRequest(String URL) throws ConnectionNotFoundException { 
		try {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				desktop.browse(URI.create(URL));
			}
		} catch (IOException ex) {
			throw new ConnectionNotFoundException(URL);
		}
		return false;
	}

	public final void resumeRequest() { }

	protected abstract void startApp() throws MIDletStateChangeException;

	public Display getDisplay() { return display; }

}
