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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ScreenShot
{
	public static void takeScreenshot(boolean saveToHomeDir)
	{
		try
		{
			Date date = new Date();
			String fileName;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

			if (saveToHomeDir == true)
			{
				fileName = System.getProperty("user.home") + "/Pictures/Screenshot from " +
							dateFormat.format(date) + ".png";
			}
			else
			{
				String screenshotPath = Mobile.getPlatform().dataPath + "screenshots";
				try
				{
					Files.createDirectories(Paths.get(screenshotPath));
				}
				catch (Exception e)
				{
					System.out.println("Problem Creating Screenshot Path "+ screenshotPath);
					System.out.println(e.getMessage());
				}
				fileName = screenshotPath + "/Screenshot from " + 
							dateFormat.format(date) + ".png";
			}

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
