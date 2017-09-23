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

public class Vector3D
{
	public int x;
	public int y;
	public int z;

	public Vector3D() { x=0; y=0; z=0; }

	public Vector3D(Vector3D v) { x=v.x; y=v.y; z=v.z; }

	public Vector3D(int X, int Y, int Z) { x=X; y=Y; z=Z; }


	public final int getX() { return x; }

	public final int getY() { return y; }

	public final int getZ() { return z; }

	public final void setX(int X) { x=X; }

	public final void setY(int Y) { y=Y; }

	public final void setZ(int Z) { z=Z; }

	public final void set(Vector3D v) { x=v.x; y=v.y; z=v.z; }

	public final void set(int X, int Y, int Z) { x=X; y=Y; z=Z; }

	public final void unit()
	{
		double len = Math.sqrt(x*x + y*y + z*z);
		x = (int)(x/len);
		y = (int)(y/len);
		z = (int)(z/len);
	}

	public final int innerProduct(Vector3D v)
	{
		return (x*v.x)+(y*v.y)+(z*v.z);
	}

	public final void outerProduct(Vector3D v)
	{
		// needs to return a 3x3 matrix
		// Lets find the cross-product instead
		// as the exterior product is a generalization
		// of the cross product
		int t1 = (y*v.z) - (z*v.y);
		int t2 = (z*v.x) - (x*v.z);
		int t3 = (x*v.y) - (y*v.x);
		x = t1;
		y = t2;
		z = t3;
	}

	public static final int innerProduct(Vector3D a, Vector3D b)
	{
		// Dot product
		return (a.x*b.x)+(a.y*b.y)+(a.z*b.z);
	}

	public static final Vector3D outerProduct(Vector3D a, Vector3D b)
	{
		Vector3D t = new Vector3D(a);
		t.outerProduct(b);
		return t;
	}
}
