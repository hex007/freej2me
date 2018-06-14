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

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import java.util.ArrayList;

public class List extends Screen implements Choice
{

	public static Command SELECT_COMMAND = new Command("Select", Command.SCREEN, 0);

	private int fitPolicy = Choice.TEXT_WRAP_ON;

	private int type;

	public List(String title, int listType)
	{
		setTitle(title);
		type = listType;

		platformImage = new PlatformImage(width, height);

		render();
	}

	public List(String title, int listType, String[] stringElements, Image[] imageElements)
	{
		setTitle(title);
		type = listType;

		for(int i=0; i<stringElements.length; i++)
		{
			if(imageElements!=null)
			{
				items.add(new ImageItem(stringElements[i], imageElements[i], 0, stringElements[i]));
			}	
			else
			{
				items.add(new StringItem(stringElements[i], stringElements[i]));
			}
		}

		platformImage = new PlatformImage(width, height);
		render();
	}

	public int append(String stringPart, Image imagePart)
	{ 
			if(imagePart!=null)
			{
				items.add(new ImageItem(stringPart, imagePart, 0, stringPart));
			}
			else
			{
				items.add(new StringItem(stringPart, stringPart));
			}
			render();
			return items.size()-1;
	}

	public void delete(int elementNum)
	{
		try
		{
			items.remove(elementNum);
		}
		catch (Exception e) { }
		render();
	}

	public void deleteAll() { items.clear(); render(); }

	public int getFitPolicy() { return fitPolicy; }

	public Font getFont(int elementNum) { return Font.getDefaultFont(); }

	public Image getImage(int elementNum) { return ((ImageItem)(items.get(elementNum))).getImage(); }
	
	public int getSelectedFlags(boolean[] selectedArray_return) { return 0; }

	public int getSelectedIndex() { return currentItem; }

	public String getString(int elementNum) { return ((StringItem)(items.get(elementNum))).getText(); }

	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		if(elementNum<items.size() && elementNum>0)
		{
			try
			{
				if(imagePart!=null)
				{
					items.add(elementNum, new ImageItem(stringPart, imagePart, 0, stringPart));
				}
				else
				{
					items.add(elementNum, new StringItem(stringPart, stringPart));
				}
			}
			catch(Exception e)
			{
				append(stringPart, imagePart);
			} 
		}
		else
		{
			append(stringPart, imagePart);
		}
		render();
	}

	public boolean isSelected(int elementNum) { return elementNum==currentItem; }

	// public void removeCommand(Command cmd) {  }

	public void set(int elementNum, String stringPart, Image imagePart)
	{
		if(imagePart!=null)
		{
			items.set(elementNum, new ImageItem(stringPart, imagePart, 0, stringPart));
		}
		else
		{
			items.set(elementNum, new StringItem(stringPart, stringPart));
		}
	}
	
	public void setFitPolicy(int fitpolicy) { fitPolicy = fitpolicy; }

	public void setFont(int elementNum, Font font) { }
		
	public void setSelectCommand(Command command) { SELECT_COMMAND = command; }
 
	public void setSelectedFlags(boolean[] selectedArray) { }

	public void setSelectedIndex(int elementNum, boolean selected)
	{
		if(selected == true)
		{
			currentItem = elementNum;
		}
		else
		{
			currentItem = 0;
		}
		render();
	}

	//void setTicker(Ticker ticker)
	
	//void setTitle(String s)

	public int size() { return items.size(); }

	/*
		Draw list, handle input
	*/

	public void keyPressed(int key)
	{
		if(items.size()<1) { return; }
		switch(key)
		{
			case Mobile.KEY_NUM2: currentItem--; break;
			case Mobile.KEY_NUM8: currentItem++; break;
			case Mobile.NOKIA_UP: currentItem--; break;
			case Mobile.NOKIA_DOWN: currentItem++; break;
			case Mobile.NOKIA_SOFT1: doLeftCommand(); break;
			case Mobile.NOKIA_SOFT2: doRightCommand(); break;
			case Mobile.KEY_NUM5: doDefaultCommand(); break;
		}
		if (currentItem>=items.size()) { currentItem=0; }
		if (currentItem<0) { currentItem = items.size()-1; }
		render();
	}

	protected void doDefaultCommand()
	{
		if(commandlistener!=null)
		{
			commandlistener.commandAction(SELECT_COMMAND, this);
		}
	}

	public void notifySetCurrent()
	{
		render();
	}
}
