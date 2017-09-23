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

public class Util3D
{
	private Util3D() {  }

	// why return ints?
	// it must do something more...
	public static final int sqrt(int t)
	{
		return (int)Math.sqrt(t);
	}

	public static final int sin(int t)
	{
		return (int)Math.sin(t);
	}

	public static final int cos(int t)
	{
		return (int)Math.cos(t);
	}
}
