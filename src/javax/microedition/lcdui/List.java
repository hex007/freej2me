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

	
	private ArrayList<String> strings = new ArrayList<String>();
	
	private ArrayList<Image> images = new ArrayList<Image>();

	private int fitPolicy = Choice.TEXT_WRAP_ON;

	private int type;

	private int selectedItem = -1;

	private Graphics gc;

	public List(String title, int listType)
	{
		setTitle(title);
		type = listType;

		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();

		render();
	}

	public List(String title, int listType, String[] stringElements, Image[] imageElements)
	{
		setTitle(title);
		type = listType;

		for(int i=0; i<stringElements.length; i++)
		{
			strings.add(stringElements[i]);
			if(i<images.size())
			{
				images.add(imageElements[i]);
			}	
		}

		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
		render();
	}

	public int append(String stringPart, Image imagePart)
	{ 
			strings.add(stringPart);
			images.add(imagePart);
			render();
			return strings.size()-1;
	}

	public void delete(int elementNum)
	{
		strings.remove(elementNum);
		images.remove(elementNum);
		render();
	}

	public void deleteAll() { strings.clear(); images.clear(); render(); }

	public int getFitPolicy() { return fitPolicy; }

	public Font getFont(int elementNum) { return Font.getDefaultFont(); }

	public Image getImage(int elementNum) { return images.get(elementNum); }
	
	public int getSelectedFlags(boolean[] selectedArray_return) { return 0; }

	public int getSelectedIndex() { return selectedItem; }

	public String getString(int elementNum) { return strings.get(elementNum); }

	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		strings.add(elementNum, stringPart);
		images.add(elementNum, imagePart);
		render();
	}

	public boolean isSelected(int elementNum) { return elementNum==selectedItem; }

	public void removeCommand(Command cmd) { super.removeCommand(cmd); }

	public void set(int elementNum, String stringPart, Image imagePart)
	{
		strings.set(elementNum, stringPart);
		images.set(elementNum, imagePart);
	}
	
	public void setFitPolicy(int fitpolicy) { fitPolicy = fitpolicy; }

	public void setFont(int elementNum, Font font) { }
		
	public void setSelectCommand(Command command) { SELECT_COMMAND = command; }
 
	public void setSelectedFlags(boolean[] selectedArray) { }

	public void setSelectedIndex(int elementNum, boolean selected)
	{
		if(selected == true)
		{
			selectedItem = elementNum;
		}
		else
		{
			selectedItem = 0;
		}
		render();
	}

	//void setTicker(Ticker ticker)
	
	//void setTitle(String s)

	public int size() { return strings.size(); }

	/*
		Draw list, handle input
	*/

	public void keyPressed(int key)
	{
		if(strings.size()<1) { return; }
		switch(key)
		{
			case Mobile.KEY_NUM2: selectedItem--; break;
			case Mobile.KEY_NUM8: selectedItem++; break;
			case Mobile.NOKIA_UP: selectedItem--; break;
			case Mobile.NOKIA_DOWN: selectedItem++; break;
			case Mobile.NOKIA_SOFT1: doLeftCommand(); break;
			case Mobile.NOKIA_SOFT2: doRightCommand(); break;
			case Mobile.KEY_NUM5: doDefaultCommand(); break;
		}
		if (selectedItem>=strings.size()) { selectedItem=0; }
		if (selectedItem<0) { selectedItem = 0; }
		render();
	}

	private void doDefaultCommand()
	{
		if(SELECT_COMMAND!=null)
		{
			if(commandlistener!=null)
			{
				commandlistener.commandAction(SELECT_COMMAND, this);
			}
		}
	}

	private void doLeftCommand()
	{
		if(commands.size()>1)
		{
			if(commandlistener!=null)
			{
				commandlistener.commandAction(commands.get(1), this);
			}
		}
	}

	private void doRightCommand()
	{
		if(commands.size()>2)
		{
			if(commandlistener!=null)
			{
				commandlistener.commandAction(commands.get(2), this);
			}
		}
	}

	private void render()
	{
		// platformImage
		
		// Draw Background:
		gc.setColor(0xFFFFFF);
		gc.fillRect(0,0,width,height);
		gc.setColor(0x000000);
		
		// Draw Title:
		gc.drawString(title, width/2, 2, Graphics.HCENTER);
		gc.drawLine(0, 20, width, 20);
		gc.drawLine(0, height-20, width, height-20);

		if(strings.size()>0)
		{
			if(selectedItem<0) { selectedItem = 0; }
			// Draw list items //
			int ah = height - 50; // allowed height
			int max = (int)Math.floor(ah / 15); // max items per page
			if (max==0) { max = 1; }
			int page = 0;
			
			if(strings.size()<max) { max = strings.size(); }
			
			page = (int)Math.floor(selectedItem/max); // current page

			int first = page * max; // first item to show
			
			int y = 25;
			for(int i=0; i<max; i++)
			{	
				if(selectedItem == (first+i))
				{
					gc.fillRect(0,y,width,15);
					gc.setColor(0xFFFFFF);
				}
				gc.drawString(strings.get(first+i), width/2, y, Graphics.HCENTER);
				gc.setColor(0x000000);

				if((first+i)<images.size())
				{
					gc.drawImage(images.get(first+i), 0, y, Graphics.LEFT);
				}
				y+=15;
			}
		}
		// Draw Commands
		switch(commands.size())
		{
			case 0: break;
			case 1:
				gc.drawString(""+(selectedItem+1)+" of "+strings.size(), width/2, height-17, Graphics.HCENTER);
				break;
			case 2:
				gc.drawString(commands.get(1).getLabel(), 3, height-17, Graphics.LEFT);
				break;
			default:
				gc.drawString(commands.get(1).getLabel(), 3, height-17, Graphics.LEFT);
				gc.drawString(commands.get(2).getLabel(), width-3, height-17, Graphics.RIGHT);
		}
		if(this.getDisplay().getCurrent() == this)
		{
			Mobile.getPlatform().repaint(platformImage, 0, 0, width, height);
		}
	}
}
