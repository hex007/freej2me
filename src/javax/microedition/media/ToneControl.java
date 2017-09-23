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
package javax.microedition.media;


public interface ToneControl extends Control
{

	public static final byte BLOCK_END  = -6;
	public static final byte BLOCK_START  = -5;
	public static final byte C4  = 60;
	public static final byte PLAY_BLOCK  = -7;
	public static final byte REPEAT  = -9;
	public static final byte RESOLUTION  = -4;
	public static final byte SET_VOLUME  = -8;
	public static final byte SILENCE  = -1;
	public static final byte TEMPO  = -3;
	public static final byte VERSION  = -2;


	public void setSequence(byte[] sequence);

}