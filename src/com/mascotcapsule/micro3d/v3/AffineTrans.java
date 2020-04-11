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

public class AffineTrans
{
	public int m00; // m{row}{col}
	public int m01; 
	public int m02; // m00  m01  m02  m03
	public int m03; // m10  m11  m12  m13
	public int m10; // m20  m21  m22  m23
	public int m11;	// m30  m31  m32  m33
	public int m12;
	public int m13; // eqv. to the common array[16]:
	public int m20; //  0   1   2   3
	public int m21; //  4   5   6   7
	public int m22; //  8   9  10  11
	public int m23; // 12  13  14  15  <- Really, it's only 3x4

	public AffineTrans()
	{
		m00 = m01 = m02 = m03 = 0; 
		m10 = m11 = m12 = m13 = 0; 
		m20 = m21 = m22 = m23 = 0; 
	}

	public AffineTrans(int m00, int m01, int m02, int m03, int m10, int m11, int m12, int m13, int m20, int m21, int m22, int m23)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
	}

	public AffineTrans(AffineTrans a)
	{
		m00 = a.m00;
		m01 = a.m01;
		m02 = a.m02;
		m03 = a.m03;
		m10 = a.m10;
		m11 = a.m11;
		m12 = a.m12;
		m13 = a.m13;
		m20 = a.m20;
		m21 = a.m21;
		m22 = a.m22;
		m23 = a.m23;
	}

	public AffineTrans(int[][] a)
	{
		m00 = a[0][0];
		m01 = a[0][1];
		m02 = a[0][2];
		m03 = a[0][3];
		m10 = a[1][0];
		m11 = a[1][1];
		m12 = a[1][2];
		m13 = a[1][3];
		m20 = a[2][0];
		m21 = a[2][1];
		m22 = a[2][2];
		m23 = a[2][3];
	}

	public AffineTrans(int[] a)
	{
		m00 = a[0];
		m01 = a[1];
		m02 = a[2];
		m03 = a[3];
		m10 = a[4];
		m11 = a[5];
		m12 = a[6];
		m13 = a[7];
		m20 = a[8];
		m21 = a[9];
		m22 = a[10];
		m23 = a[11];
	}

	public AffineTrans(int[] a, int offset)
	{
		m00 = a[offset+0];
		m01 = a[offset+1];
		m02 = a[offset+2];
		m03 = a[offset+3];
		m10 = a[offset+4];
		m11 = a[offset+5];
		m12 = a[offset+6];
		m13 = a[offset+7];
		m20 = a[offset+8];
		m21 = a[offset+9];
		m22 = a[offset+10];
		m23 = a[offset+11];
	}

	public final void get(int[] a)
	{
		a[0] = m00;
		a[1] = m01;
		a[2] = m02;
		a[3] = m03;
		a[4] = m10;
		a[5] = m11;
		a[6] = m12;
		a[7] = m13;
		a[8] = m20;
		a[9] = m21;
		a[10] = m22;
		a[11] = m23;
	}

	public final void get(int[] a, int offset)
	{
		a[offset+0]  = m00;
		a[offset+1]  = m01;
		a[offset+2]  = m02;
		a[offset+3]  = m03;
		a[offset+4]  = m10;
		a[offset+5]  = m11;
		a[offset+6]  = m12;
		a[offset+7]  = m13;
		a[offset+8]  = m20;
		a[offset+9]  = m21;
		a[offset+10] = m22;
		a[offset+11] = m23;
	}

	public final void set(int[] a, int offset)
	{
		m00 = a[offset+0];
		m01 = a[offset+1];
		m02 = a[offset+2];
		m03 = a[offset+3];
		m10 = a[offset+4];
		m11 = a[offset+5];
		m12 = a[offset+6];
		m13 = a[offset+7];
		m20 = a[offset+8];
		m21 = a[offset+9];
		m22 = a[offset+10];
		m23 = a[offset+11];
	}

	public final void set(int m00, int m01, int m02, int m03, int m10, int m11, int m12, int m13, int m20, int m21, int m22, int m23)
	{
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
	}

	public final void set(AffineTrans a)
	{
		m00 = a.m00;
		m01 = a.m01;
		m02 = a.m02;
		m03 = a.m03;
		m10 = a.m10;
		m11 = a.m11;
		m12 = a.m12;
		m13 = a.m13;
		m20 = a.m20;
		m21 = a.m21;
		m22 = a.m22;
		m23 = a.m23;
	}

	public final void set(int[][] a) // int[row][col] -- must be at least int[3][4]
	{
		m00 = a[0][0];
		m01 = a[0][1];
		m02 = a[0][2];
		m03 = a[0][3];
		m10 = a[1][0];
		m11 = a[1][1];
		m12 = a[1][2];
		m13 = a[1][3];
		m20 = a[2][0];
		m21 = a[2][1];
		m22 = a[2][2];
		m23 = a[2][3];
	}

	public final void set(int[] a)
	{
		m00 = a[0];
		m01 = a[1];
		m02 = a[2];
		m03 = a[3];
		m10 = a[4];
		m11 = a[5];
		m12 = a[6];
		m13 = a[7];
		m20 = a[8];
		m21 = a[9];
		m22 = a[10];
		m23 = a[11];
	}

	public final Vector3D transPoint(Vector3D v)
	{
		return transform(v);
	}

	public final Vector3D transform(Vector3D v)
	{
		double x = v.x/4096D;
		double y = v.y/4096D;
		double z = v.z/4096D;

		int X = (int)( x*m00 + y*m10 + z*m20 );
		int Y = (int)( x*m01 + y*m11 + z*m21 );
		int Z = (int)( x*m02 + y*m12 + z*m22 ); 
		
		return new Vector3D(X,Y,Z);
	}

	public final void multiply(AffineTrans a) { mul(this, a); }

	public final void mul(AffineTrans a) { mul(this, a); }

	public final void multiply(AffineTrans a1, AffineTrans a2) { mul(a1, a2); }

	public final void mul(AffineTrans a1, AffineTrans a2)
	{
		double b00 = a2.m00/4096D; // Fixed point arithmetic where 1.0 = 4096
		double b01 = a2.m00/4096D; // The multiplier/divisor must not be
		double b02 = a2.m00/4096D; // adjusted.  e.g. a * b will give the
		double b03 = a2.m00/4096D; // wrong result and should be a * (b/4096)
		double b10 = a2.m00/4096D;
		double b11 = a2.m00/4096D;
		double b12 = a2.m00/4096D;
		double b13 = a2.m00/4096D;
		double b20 = a2.m00/4096D;
		double b21 = a2.m00/4096D;
		double b22 = a2.m00/4096D;
		double b23 = a2.m00/4096D;

		int t00  = (int)(a1.m00*a2.m00  + a1.m01*a2.m10  + a1.m02*a2.m20);
		int t01  = (int)(a1.m00*a2.m01  + a1.m01*a2.m11  + a1.m02*a2.m21);
		int t02  = (int)(a1.m00*a2.m02  + a1.m01*a2.m12  + a1.m02*a2.m22);
		int t03  = (int)(a1.m00*a2.m03  + a1.m01*a2.m13  + a1.m02*a2.m23);

		int t10  = (int)(a1.m10*a2.m00  + a1.m11*a2.m10  + a1.m12*a2.m20);
		int t11  = (int)(a1.m10*a2.m01  + a1.m11*a2.m11  + a1.m12*a2.m21);
		int t12  = (int)(a1.m10*a2.m02  + a1.m11*a2.m12  + a1.m12*a2.m22);
		int t13  = (int)(a1.m10*a2.m03  + a1.m11*a2.m13  + a1.m12*a2.m23);

		int t20  = (int)(a1.m20*a2.m00  + a1.m21*a2.m10  + a1.m22*a2.m20);
		int t21  = (int)(a1.m20*a2.m01  + a1.m21*a2.m11  + a1.m22*a2.m21);
		int t22  = (int)(a1.m20*a2.m02  + a1.m21*a2.m12  + a1.m22*a2.m22);
		int t23  = (int)(a1.m20*a2.m03  + a1.m21*a2.m13  + a1.m22*a2.m23);
		
		m00 = t00;  m01 = t01;  m02 = t02;  m03 = t03;
		m10 = t10;  m11 = t11;  m12 = t12;  m13 = t13;
		m20 = t20;  m21 = t21;  m22 = t22;  m23 = t23;
	}

	public final void setIdentity()
	{
		m00 = 4096;  m01 = 0;     m02 = 0;     m03 = 0; 
		m10 = 0;     m11 = 4096;  m12 = 0;     m13 = 0; 
		m20 = 0;     m21 = 0;     m22 = 4096;  m23 = 0; 
	}
	
	public final void setRotationX(int x) { rotationX(x); }
	
	public final void setRotationY(int y) { rotationY(y); }
	
	public final void setRotationZ(int z) { rotationZ(z); }

	public final void rotationV(Vector3D v, int r) { setRotation(v, r);  }

	public final void rotationX(int x)
	{
		m00 =  1; m01 =  0;              m02 =  0;             m03 =  0;
		m10 =  0; m11 =  Util3D.cos(x);  m12 =  Util3D.sin(x); m13 =  0;
		m20 =  0; m21 = -Util3D.sin(x);  m22 =  Util3D.cos(x); m23 =  0;
	}

	public final void rotationY(int y)
	{
		m00 =  Util3D.cos(y); m01 =  0; m02 = -Util3D.sin(y); m03 =  0;
		m10 =  0;             m11 =  1; m12 =  0;             m13 =  0;
		m20 =  Util3D.sin(y); m21 =  0; m22 =  Util3D.cos(y); m23 =  0;
	}

	public final void rotationZ(int z)
	{
		m00 =  Util3D.cos(z); m01 =  Util3D.sin(z); m02 =  0; m03 =  0;
		m10 = -Util3D.sin(z); m11 =  Util3D.cos(z); m12 =  0; m13 =  0;
		m20 =  0;             m21 =  0;             m22 =  1; m23 =  0;
	}

	public final void setRotation(Vector3D v, int r)
	{
		// I'm guessing this is like the other setRotation methods but around an arbitrary axis.
		// A rotation matrix from axis and angle -- this is ugly
		double Ux = v.x/4096D;
		double Uy = v.y/4096D;
		double Uz = v.z/4096D;
		double theta = (r/4096D) * Math.PI*2;
		double cost = Math.cos(theta);
		double sint = Math.sin(theta);

		// v must be a unit vector - normalize
		double vLen = Math.sqrt( (Ux*Ux) + (Uy*Uy) + (Uz*Uz) );
		if(vLen!=0)
		{
			Ux = Ux / vLen;
			Uy = Uy / vLen;
			Uz = Uz / vLen;
		} else { Ux = Uy = Uz = 0; }
		
		m00 = (int)(( cost + Ux*Ux*(1-cost)    )*4096);
		m01 = (int)(( Ux*Uy*(1-cost) - Uz*sint )*4096);
		m02 = (int)(( Ux*Uz*(1-cost) + Uy*sint )*4096);
		m03 = 0;

		m10 = (int)(( Uy*Ux*(1-cost) + Uz*sint )*4096);
		m11 = (int)(( cost + Uy*Uy*(1-cost)    )*4096);
		m12 = (int)(( Uy*Uz*(1-cost) - Ux*sint )*4096);
		m13 = 0;

		m20 = (int)(( Uz*Ux*(1-cost) - Uy*sint )*4096);
		m21 = (int)(( Uz*Uy*(1-cost) + Ux*sint )*4096);
		m22 = (int)(( cost + Uz*Uz*(1-cost)    )*4096);
		m23 = 0;
	}

	public final void setViewTrans(Vector3D pos, Vector3D look, Vector3D up) { lookAt(pos, look, up); }

	public final void lookAt(Vector3D pos, Vector3D look, Vector3D up)
	{
		double Ux = up.x/4096D;
		double Uy = up.y/4096D;
		double Uz = up.z/4096D;

		// z-axis 
		//	- Find difference in position 
		//    between camera and thing we want to look at
		double Zx = (pos.x - look.x)/4096D;
		double Zy = (pos.y - look.y)/4096D;
		double Zz = (pos.z - look.z)/4096D;
		//	- Normalize
		double vLen = Math.sqrt( (Zx*Zx) + (Zy*Zy) + (Zz*Zz) );
		if(vLen!=0)
		{
			Zx = Zx / vLen;
			Zy = Zy / vLen;
			Zz = Zz / vLen;
		} else { Zx = Zy = Zz = 0; }

		// x-axis
		// - Find cross product of z-axis and up vector
		double Xx = Zy * Uz - Zz * Uy;  // C.x = A.y * B.z - A.z * B.y
		double Xy = Zz * Ux - Zx * Uz;  // C.y = A.z * B.x - A.x * B.z
		double Xz = Zx * Uy - Zy * Ux;  // C.z = A.x * B.y - A.y * B.x
		//	- Normalize
		vLen = Math.sqrt( (Xx*Xx) + (Xy*Xy) + (Xz*Xz) );
		if(vLen!=0)
		{
			Xx = Xx / vLen;
			Xy = Xy / vLen;
			Xz = Xz / vLen;
		} else { Xx = Xy = Xz = 0; }

		// y-axis
		// - Find cross product of z-axis and x-axis
		double Yx = Zy * Xz - Zz * Xy;  // C.x = A.y * B.z - A.z * B.y
		double Yy = Zz * Xx - Zx * Xz;  // C.y = A.z * B.x - A.x * B.z
		double Yz = Zx * Xy - Zy * Xx;  // C.z = A.x * B.y - A.y * B.x
		//	No need to normalize

		// Create Matrix
		m00 = (int)(Xx*4096); m01 = (int)(Xy*4096); m02 = (int)(Xz*4096); m03 = 0;
		m10 = (int)(Yx*4096); m11 = (int)(Yy*4096); m12 = (int)(Yz*4096); m13 = 0;
		m20 = (int)(Zx*4096); m21 = (int)(Zy*4096); m22 = (int)(Zz*4096); m23 = 0;
	}
}
