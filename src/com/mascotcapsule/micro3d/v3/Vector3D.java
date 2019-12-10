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
	// componets are fixed-point numbers in which 1.0 is equivalent to 4096 
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

	public final void unit() // unit vector will have a length of 4096 
	{
		double len = Math.sqrt(x*x + y*y + z*z);
		x = (int)((x/len)*4096);
		y = (int)((y/len)*4096);
		z = (int)((z/len)*4096);
	}

	public final int innerProduct(Vector3D v)
	{
		double X = x/4096;
		double Y = y/4096;
		double Z = z/4096;
		double vX = v.x/4096;
		double vY = v.y/4096;
		double vZ = v.z/4096;
		return (int)((X*vX)+(Y*vY)+(Z*vZ));
	}

	public final void outerProduct(Vector3D v)
	{
		// needs to return a 3x3 matrix
		// Lets find the cross-product instead
		// as the exterior product is a generalization
		// of the cross product
		double X = x/4096;
		double Y = y/4096;
		double Z = z/4096;
		double vX = v.x/4096;
		double vY = v.y/4096;
		double vZ = v.z/4096;

		double t1 = (Y*vZ) - (Z*vY);
		double t2 = (Z*vX) - (X*vZ);
		double t3 = (X*vY) - (Y*vX);
		x = (int)t1*4096;
		y = (int)t2*4096;
		z = (int)t3*4096;
		unit();
	}

	public static final int innerProduct(Vector3D a, Vector3D b)
	{
		// Dot product
		double aX = a.x/4096;
		double aY = a.y/4096;
		double aZ = a.z/4096;
		double bX = b.x/4096;
		double bY = b.y/4096;
		double bZ = b.z/4096;
		return (int)((aX*bX)+(aY*bY)+(aZ*bZ));
	}

	public static final Vector3D outerProduct(Vector3D a, Vector3D b)
	{
		Vector3D t = new Vector3D(a);
		t.outerProduct(b);
		return t;
	}
}
