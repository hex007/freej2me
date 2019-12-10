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
package com.mascotcapsule.micro3d.v3;

import java.io.IOException;

import org.recompile.mobile.Mobile;

public class ActionTable
{

	protected int numFrames;
	protected int numActions;
	private byte[] rawdata;

	public ActionTable(byte[] b) 
	{
		/*
		System.out.println("Action Table");
		for(int i=0; i<b.length; i++)
		{
			System.out.printf("%02X ", b[i]);
		}
		*/
		rawdata = b;
		numFrames = 1;
		numActions = 1;
	}

	public ActionTable(String name) throws IOException
	{
		System.out.println("Action Table "+name);
		rawdata = Mobile.getPlatform().loader.getMIDletResourceAsByteArray(name);

		numFrames = 1;
		numActions = 1;
	}


	public final void dispose() {  }

	public final int getNumAction() { return getNumActions(); }

	public final int getNumActions() { return numActions; }

	public final int getNumFrame(int idx) { return getNumFrames(idx); }

	public final int getNumFrames(int idx) { return numFrames; }

}
