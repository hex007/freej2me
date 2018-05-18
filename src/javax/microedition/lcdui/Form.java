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


public class Form extends Screen
{

	ArrayList<Item> items = new ArrayList<Item>();

	ItemStateListener listener;


	public Form(String title)
	{
		setTitle(title);
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
	}


	public int append(Image img) { items.add(new ImageItem("",img,0,"")); return items.size()-1; }

	public int append(Item item) { items.add(item); return items.size()-1; }

	public int append(String str) { items.add(new StringItem("",str)); return items.size()-1; }

	public void delete(int itemNum) { items.remove(itemNum); }

	public void deleteAll() { items.clear(); }

	public Item get(int itemNum) { return items.get(itemNum); }

	public int getHeight() { return 128; }

	public int getWidth() { return 64; }

	public void insert(int itemNum, Item item) { items.add(itemNum, item); }

	public void set(int itemNum, Item item) { items.set(itemNum, item); }

	public void setItemStateListener(ItemStateListener iListener) { listener = iListener; }

	public int size() { return items.size(); }

}
