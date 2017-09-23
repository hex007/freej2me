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



public class List extends Screen implements Choice
{

	public static Command SELECT_COMMAND = new Command("Select", Command.SCREEN, 0);


	private ArrayList<String> strings;

	private ArrayList<Image> images;

	private int fitPolicy = Choice.TEXT_WRAP_ON;

	private int type;


	public List(String title, int listType)
	{
		setTitle(title);
		type = listType;
	}

	public List(String title, int listType, String[] stringElements, Image[] imageElements)
	{
		setTitle(title);
		type = listType;

		for(int i=0; i<stringElements.length; i++)
		{
			strings.add(stringElements[i]);
			images.add(imageElements[i]);
		}
	}


	public int append(String stringPart, Image imagePart)
	{
		strings.add(stringPart);
		images.add(imagePart);
		return strings.size()-1;
	}

	public void delete(int elementNum) { strings.remove(elementNum); images.remove(elementNum); }

	public void deleteAll() { strings.clear(); images.clear(); }

	public int getFitPolicy() { return fitPolicy; }

	public Font getFont(int elementNum) { return Font.getDefaultFont(); }

	public Image getImage(int elementNum) { return images.get(elementNum); }

	public int getSelectedFlags(boolean[] selectedArray_return) { return 0; }

	public int getSelectedIndex() { return 0; }

	public String getString(int elementNum) { return strings.get(elementNum); }

	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		strings.add(elementNum, stringPart);
		images.add(elementNum, imagePart);
	}

	public boolean isSelected(int elementNum) { return false; }

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

	public void setSelectedIndex(int elementNum, boolean selected) { }

	//void setTicker(Ticker ticker)

	//void setTitle(String s)

	public int size() { return strings.size(); }
}
