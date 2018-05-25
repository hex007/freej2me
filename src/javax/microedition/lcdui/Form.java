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


public class Form extends Screen
{

	private ArrayList<Item> items = new ArrayList<Item>();

	public ItemStateListener listener;

	private int currentItem = -1;

	private Graphics gc;

	public Form(String title)
	{
		setTitle(title);
		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
		render();
	}

	public Form(String title, Item[] itemarray)
	{
		setTitle(title);

		if (items != null)
		{
			for (int i=0; i<itemarray.length; i++)
			{
				items.add(itemarray[i]);
			}
		}
		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
		render();
	}


	public int append(Image img) { items.add(new ImageItem("",img,0,"")); render(); return items.size()-1;  }

	public int append(Item item) { items.add(item);  render(); return items.size()-1; }

	public int append(String str) { items.add(new StringItem("",str)); render(); return items.size()-1;  }

	public void delete(int itemNum) { items.remove(itemNum); render(); }

	public void deleteAll() { items.clear(); render(); }

	public Item get(int itemNum) { return items.get(itemNum); }

	public int getHeight() { return 128; }

	public int getWidth() { return 64; }

	public void insert(int itemNum, Item item) { items.add(itemNum, item); render(); }

	public void set(int itemNum, Item item) { items.set(itemNum, item); render(); }

	public void setItemStateListener(ItemStateListener iListener) { listener = iListener; }

	public int size() { return items.size(); }

	/*
		Draw form, handle input
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
		if (currentItem<0) { currentItem = 0; }
		render();
	}

	private void doDefaultCommand()
	{
		if(commands.size()>0)
		{
			if(commandlistener!=null)
			{
				commandlistener.commandAction(commands.get(0), this);
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

		if(items.size()>0)
		{
			if(currentItem<0) { currentItem = 0; }
			// Draw list items //
			int ah = height - 50; // allowed height
			int max = (int)Math.floor(ah / 15); // max items per page
			if (max==0) { max = 1; }
			int page = 0;
			
			if(items.size()<max) { max = items.size(); }
			
			page = (int)Math.floor(currentItem/max); // current page

			int first = page * max; // first item to show
			
			int y = 25;
			for(int i=0; i<max; i++)
			{	
				if(currentItem == (first+i))
				{
					gc.fillRect(0,y,width,15);
					gc.setColor(0xFFFFFF);
				}
				gc.drawString(items.get(first+i).getLabel(), width/2, y, Graphics.HCENTER);
				if(items.get(first+i) instanceof StringItem)
				{
					gc.drawString(((StringItem)items.get(first+i)).getText(), width/2, y, Graphics.HCENTER);
				}
				gc.setColor(0x000000);
				if(items.get(first+i) instanceof ImageItem)
				{
					gc.drawImage(((ImageItem)items.get(first+i)).getImage(), width/2, y, Graphics.HCENTER);
				}
				y+=15;
			}
		}
		// Draw Commands
		switch(commands.size())
		{
			case 0: break;
			case 1:
				gc.drawString(""+(currentItem+1)+" of "+items.size(), width/2, height-17, Graphics.HCENTER);
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

	public void notifySetCurrent()
	{
		render();
	}

}
