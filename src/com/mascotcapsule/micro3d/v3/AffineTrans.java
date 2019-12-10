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
	public int m00;
	public int m01;
	public int m02;
	public int m03;
	public int m10;
	public int m11;
	public int m12;
	public int m13;
	public int m20;
	public int m21;
	public int m22;
	public int m23;

	public int m30;
	public int m31;
	public int m32;
	public int m33;

	private int rotx;
	private int roty;
	private int rotz;

	public AffineTrans()
	{
		m00 = m01 = m02 = m03 = 0; // row 1, columns 1-4
		m10 = m11 = m12 = m13 = 0; // row 2, columns 1-4
		m20 = m21 = m22 = m23 = 0; // etc...
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = a.m30;
		m31 = a.m31;
		m32 = a.m32;
		m33 = a.m33;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
	}

	public final void set(int[][] a)
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
		m30 = m31 = m32 = m33 = 0;
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
		m30 = m31 = m32 = m33 = 0;
	}

	public final Vector3D transPoint(Vector3D v)
	{
		return v; // ?
	}

	public final Vector3D transform(Vector3D v)
	{
		return v; // ?
	}

	public final void multiply(AffineTrans a)
	{
		mul(this, a);
	}

	public final void mul(AffineTrans a)
	{
		mul(this, a);
	}

	public final void multiply(AffineTrans a1, AffineTrans a2)
	{
		mul(a1, a2);
	}

	public final void mul(AffineTrans a1, AffineTrans a2)
	{
		int t00  = a1.m00*a2.m00  + a1.m01*a2.m10  + a1.m02*a2.m20  + a1.m03*a2.m30;
		int t01  = a1.m00*a2.m01  + a1.m01*a2.m11  + a1.m02*a2.m21  + a1.m03*a2.m31;
		int t02  = a1.m00*a2.m02  + a1.m01*a2.m12  + a1.m02*a2.m22  + a1.m03*a2.m32;
		int t03  = a1.m00*a2.m03  + a1.m01*a2.m13  + a1.m02*a2.m23  + a1.m03*a2.m33;

		int t10  = a1.m10*a2.m00  + a1.m11*a2.m10  + a1.m12*a2.m20  + a1.m13*a2.m30;
		int t11  = a1.m10*a2.m01  + a1.m11*a2.m11  + a1.m12*a2.m21  + a1.m13*a2.m31;
		int t12  = a1.m10*a2.m02  + a1.m11*a2.m12  + a1.m12*a2.m22  + a1.m13*a2.m32;
		int t13  = a1.m10*a2.m03  + a1.m11*a2.m13  + a1.m12*a2.m23  + a1.m13*a2.m33;

		int t20  = a1.m20*a2.m00  + a1.m21*a2.m10  + a1.m22*a2.m20  + a1.m23*a2.m30;
		int t21  = a1.m20*a2.m01  + a1.m21*a2.m11  + a1.m22*a2.m21  + a1.m23*a2.m31;
		int t22  = a1.m20*a2.m02  + a1.m21*a2.m12  + a1.m22*a2.m22  + a1.m23*a2.m32;
		int t23  = a1.m20*a2.m03  + a1.m21*a2.m13  + a1.m22*a2.m23  + a1.m23*a2.m33;
		
		int t30  = a1.m30*a2.m00  + a1.m31*a2.m10  + a1.m32*a2.m20  + a1.m33*a2.m30;
		int t31  = a1.m30*a2.m01  + a1.m31*a2.m11  + a1.m32*a2.m21  + a1.m33*a2.m31;
		int t32  = a1.m30*a2.m02  + a1.m31*a2.m12  + a1.m32*a2.m22  + a1.m33*a2.m32;
		int t33  = a1.m30*a2.m03  + a1.m31*a2.m13  + a1.m32*a2.m23  + a1.m33*a2.m33;

		m00 = t00;
		m01 = t01;
		m02 = t02;
		m03 = t03;
		m10 = t10;
		m11 = t11;
		m12 = t12;
		m13 = t13;
		m20 = t20;
		m21 = t21;
		m22 = t22;
		m23 = t23;
		m30 = t30;
		m31 = t31;
		m32 = t32;
		m33 = t33;
	}

	public final void setRotationX(int x) { rotx = x; }

	public final void setRotationY(int y) { roty = y; }

	public final void setRotationZ(int z) { rotz = z; }

	public final void setIdentity()
	{
		m00 = 1;  m01 = 0;  m02 = 0;  m03 = 0; 
		m10 = 0;  m11 = 1;  m12 = 0;  m13 = 0; 
		m20 = 0;  m21 = 0;  m22 = 1;  m23 = 0; 
		m30 = 0;  m31 = 0;  m32 = 0;  m33 = 1;
	}

	public final void rotationX(int x) { rotx = x; }

	public final void rotationY(int y) { roty = y; }

	public final void rotationZ(int z) { rotz = z; }

	public final void rotationV(Vector3D vector3d, int i) {  }

	public final void setRotation(Vector3D vector3d, int i) {  }

	public final void setViewTrans(Vector3D vector3d, Vector3D vector3d1, Vector3D vector3d2) {  }

	public final void lookAt(Vector3D vector3d, Vector3D vector3d1, Vector3D vector3d2) {  }
}
