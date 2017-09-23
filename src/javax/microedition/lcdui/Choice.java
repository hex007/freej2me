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


public interface Choice
{

	static final int EXCLUSIVE = 1;
	static final int MULTIPLE = 2;
	static final int IMPLICIT = 3;
	static final int POPUP = 4;

	static final int TEXT_WRAP_DEFAULT = 0;
	static final int TEXT_WRAP_ON = 1;
	static final int TEXT_WRAP_OFF = 2;


	int append(String stringPart, Image imagePart);

	void delete(int elementNum);

	void deleteAll();

	int getFitPolicy();

	Font getFont(int elementNum);

	Image getImage(int elementNum);

	int getSelectedFlags(boolean[] selectedArray_return);

	int getSelectedIndex();

	String getString(int elementNum);

	void insert(int elementNum, String stringPart, Image imagePart);

	boolean isSelected(int elementNum);

	void set(int elementNum, String stringPart, Image imagePart);

	void setFitPolicy(int fitPolicy);

	void setFont(int elementNum, Font font);

	void setSelectedFlags(boolean[] selectedArray);

	void setSelectedIndex(int elementNum, boolean selected);

	int size();

}
