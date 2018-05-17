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

import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.lang.Exception;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ScreenShot
{
	public static void takeScreenshot()
	{
		try
		{
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			String fileName = System.getProperty("user.home") + "/Pictures/Screenshot from "
							+ dateFormat.format(date) + ".png";

			File outputfile = new File(fileName);
			ImageIO.write(Mobile.getPlatform().getLCD(), "png", outputfile);
			System.out.printf("Saved screenshot: %s\n", outputfile.toString());
		}
		catch (Exception e)
		{
			System.out.println("Error saving screenshot");
			e.printStackTrace();
		}
	}
}