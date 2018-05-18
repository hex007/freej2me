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



public class ChoiceGroup extends Item implements Choice
{

	private String label;

    private int type;

	private ArrayList<String> strings = new ArrayList<String>();

	private ArrayList<Image> images = new ArrayList<Image>();

	private int fitPolicy;

	public ChoiceGroup(String choiceLabel, int choiceType)
	{
		label = choiceLabel;
		type = choiceType;
	}

	public ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements)
	{
		label = choiceLabel;
		type = choiceType;
		for(int i=0; i<stringElements.length; i++)
		{
			strings.add(stringElements[i]);
			images.add(imageElements[i]);
		}
	}

	ChoiceGroup(String choiceLabel, int choiceType, boolean validateChoiceType)
	{
		label = choiceLabel;
		type = choiceType;
	}

	ChoiceGroup(String choiceLabel, int choiceType, String[] stringElements, Image[] imageElements, boolean validateChoiceType)
	{
		label = choiceLabel;
		type = choiceType;
		for(int i=0; i<stringElements.length; i++)
		{
			strings.add(stringElements[i]);
			images.add(imageElements[i]);
		}
	}

	public int append(String stringPart, Image imagePart) { return strings.size(); }

	public void delete(int itemNum) { strings.remove(itemNum); images.remove(itemNum); }

	public void deleteAll() { strings.clear(); images.clear(); }

	public int getFitPolicy() { return fitPolicy; }

	public Font getFont(int itemNum) { return Font.getDefaultFont(); }

	public Image getImage(int elementNum) { return images.get(elementNum); }

	public int getSelectedFlags(boolean[] selectedArray) { return 1; }

  	public int getSelectedIndex() { return 1; }

	public String getString(int elementNum) { return strings.get(elementNum); }

  	public void insert(int elementNum, String stringPart, Image imagePart)
	{
		strings.add(elementNum, stringPart);
		images.add(elementNum, imagePart);
	}

  	public boolean isSelected(int elementNum) { return false; }

  	public void set(int elementNum, String stringPart, Image imagePart)
	{
		strings.set(elementNum, stringPart);
		images.set(elementNum, imagePart);
	}

	public void setFitPolicy(int policy) { fitPolicy = policy; }

	public void setFont(int itemNum, Font font) { }

  	public void setSelectedFlags(boolean[] selectedArray) { }

  	public void setSelectedIndex(int elementNum, boolean selected) { }

	public int size() { return strings.size(); }

}
