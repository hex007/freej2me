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
package javax.microedition.media.control;

public interface VideoControl extends GUIControl
{

	public static int USE_DIRECT_VIDEO = 1;


	public int getDisplayHeight();

	public int getDisplayWidth();

	public int getDisplayX();

	public int getDisplayY();

	public byte[] getSnapshot(java.lang.String imageType);

	public int getSourceHeight();

	public int getSourceWidth();

	public java.lang.Object initDisplayMode(int mode, java.lang.Object arg);

	public void setDisplayFullScreen(boolean fullScreenMode);

	public void setDisplayLocation(int x, int y);

	public void setDisplaySize(int width, int height);

	public void setVisible(boolean visible);

}
