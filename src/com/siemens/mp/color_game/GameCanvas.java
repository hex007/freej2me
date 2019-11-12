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


package com.siemens.mp.color_game;

public abstract class GameCanvas extends javax.microedition.lcdui.game.GameCanvas
{
	public static final int UP_PRESSED = 2; // 1
	public static final int LEFT_PRESSED = 4; // 2
	public static final int RIGHT_PRESSED = 32; // 5
	public static final int DOWN_PRESSED = 64; // 6
	public static final int FIRE_PRESSED = 256; // 8

	public static final int GAME_A_PRESSED = 512; // 9
	public static final int GAME_B_PRESSED = 1024; // 10
	public static final int GAME_C_PRESSED = 2048; // 11
	public static final int GAME_D_PRESSED = 4096; // 12

	private int keyStates = 0;

	protected GameCanvas(boolean suppressKeyEvents)
	{
		super(suppressKeyEvents);
	}

	public int getKeyStates()
	{
		int t = keyStates;
		keyStates = 0; // clear keyStates on read
		return keyStates;
	}

	public void keyPressed(int key)
	{
		keyStates |= (1<<getGameAction(key));
	}

	public void keyReleased(int key) { } // key presses latch until read

}
