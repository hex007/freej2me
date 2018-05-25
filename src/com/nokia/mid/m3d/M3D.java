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

package com.nokia.mid.m3d;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;

import javax.microedition.lcdui.Graphics;

public class M3D
{
	private double[] matrix = new double[16];
	
	private double[] transm = new double[16];

	private double[] scalem = new double[16];
	
	private double[] q1m = new double[16];

	private double[] q2m = new double[16];

	private double[] stackm = new double[16];
	
	private double[] tempm = new double[16];

	private int width;

	private int height;

	private double[] verts = new double[300];
	private int vertCount;

	private PlatformImage platformImage;
	private PlatformGraphics gc;

	private M3D() {  }

	public static M3D createInstance()
	{
		//System.out.println("CREATE M3D");
		return new M3D();
	}


	public void setupBuffers(int a, int displayWidth, int displayHeight)
	{
		// a == 33 max faces?

		width = displayWidth;
		height = displayHeight;
		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
	}
	public void removeBuffers() {  }

	public void cullFace(int a) {  }

	public void viewport(int a, int b, int c, int d) {  }

	public void clear(int a)
	{
		//System.out.println("clear();");
		gc.setColor(0xFFFFFF);
		gc.fillRect(0, 0, width, height);
		gc.setColor(0x000000);
	}


	public void matrixMode(int a) {  }

	public void loadIdentity()
	{
		identity(matrix);
	}

	public void frustumxi(int a, int b, int c, int d, int e, int f)
	{
		//System.out.println("frustrumxi("+a+", "+b+", "+c+", "+d+", "+e+", "+f+");");
	}

	public void scalexi(int a, int b, int c)
	{ 
		//System.out.println("scalexi("+a+", "+b+", "+c+");");
	}

	public void translatexi(int x, int y, int z)
	{
		//System.out.println("translatexi("+x+", "+y+", "+z+");");
		x = (x/65536);
		y = (y/65536);
		z = (z/65536);
		transm[0]  = 1; transm[1]  = 0; transm[2]  = 0; transm[3]  = 0;
		transm[4]  = 0; transm[5]  = 1; transm[6]  = 0; transm[7]  = 0;
		transm[8]  = 0; transm[9]  = 0; transm[10] = 1; transm[11] = 0;
		transm[12] = x; transm[13] = y; transm[14] = z; transm[15] = 1;

		matmul(matrix, transm);
	}

	public void rotatexi(int X, int Y, int Z, int W) // quaternion? 
	{
		//System.out.println("rotatexi("+x+", "+y+", "+z+", "+w+");");
		double x = X/65536;
		double y = Y/65536;
		double z = Z/65536;
		double w = W/65536;

		// normalize //
		double l = Math.sqrt(x*x + y*y + z*z + w*w);
		x = x/l;
		y = y/l;
		z = z/l;
		w = w/l;

		// quat to mat4
		q1m[0]  =  w; q1m[1]  =  z; q1m[2]  =  y; q1m[3]  =  x;
		q1m[4]  = -z; q1m[5]  =  w; q1m[6]  =  x; q1m[7]  =  y;
		q1m[8]  =  y; q1m[9]  = -x; q1m[10] =  w; q1m[11] =  z;
		q1m[12] = -x; q1m[13] = -y; q1m[14] = -z; q1m[15] =  w;

		q2m[0]  =  w; q2m[1]  =  z; q2m[2]  = -y; q2m[3]  = -x;
		q2m[4]  = -z; q2m[5]  =  w; q2m[6]  =  x; q2m[7]  = -y;
		q2m[8]  =  y; q2m[9]  = -x; q2m[10] =  w; q2m[11] = -z;
		q2m[12] =  x; q2m[13] =  y; q2m[14] =  z; q2m[15] =  w;

		matmul(q1m, q2m);
		//matmul(matrix, q1m);
	}

	public void pushMatrix()
	{ 
		//System.out.println("pushMatrix();");
		clone(stackm, matrix);
	}

	public void popMatrix()
	{
		//System.out.println("popMatrix();");
		clone(matrix, stackm);
	}

	
	public void vertexPointerub(int a, int b, byte[] c)
	{
		/*
		System.out.print("vertexPointerub("+a+", "+b+", [ ");
		for(int i=0; i<c.length; i++)
		{
			System.out.print(""+((int)c[i])+", ");
		}
		System.out.println("]); // "+a+", "+b+", len "+c.length);
		*/

		for(int i=0; i<c.length; i++)
		{
			verts[i] = c[i];
		}
		vertCount = c.length;
	}

	public void color4ub(byte a, byte b, byte c, byte d) {  }

	public void clearColor4ub(byte a, byte b, byte c, byte d) {  }

	
	public void drawElementsub(int a, int b, byte[] c)
	{
		/*
		System.out.print("drawElements("+a+", "+b+", [ ");
		for(int i=0; i<c.length; i++)
		{
			System.out.print(""+((int)c[i])+", ");
		}
		System.out.println("]); // "+a+", "+b+", len "+c.length);
		*/

		double x, y, z, theta;
		for(int i=0; i<verts.length; i+=3)
		{
			x = verts[i];
			y = verts[i+1]; 
			z = verts[i+2];
			verts[i]   = x*matrix[0] + y*matrix[4] + z*matrix[8]  + matrix[12];
			verts[i+1] = x*matrix[1] + y*matrix[5] + z*matrix[9]  + matrix[13];
			verts[i+2] = x*matrix[2] + y*matrix[6] + z*matrix[10] + matrix[14]; 
		}

		for(int i=0; i<vertCount; i+=3)
		{
			x = verts[i];
			y = verts[i+1]; 
			z = verts[i+2];
			verts[i] *=0.25;
			verts[i+1]*=0.25; 
			verts[i+2]*=0.25;
			//theta = 4;
			//verts[i+1] = y*Math.cos(theta) - z*Math.sin(theta);
			//verts[i+2] = y*Math.sin(theta) + z*Math.cos(theta); 
		}

		// draw elements
		double x1, y1, z1, x2, y2, z2, x3, y3, z3;
		for(int i=0; i<c.length; i+=3)
		{
			x1 = verts[(c[i]*3)];
			y1 = verts[(c[i]*3)+1];
			z1 = verts[(c[i]*3)+2];

			x2 = verts[(c[i+1]*3)];
			y2 = verts[(c[i+1]*3)+1];
			z2 = verts[(c[i+1]*3)+2];

			x3 = verts[(c[i+2]*3)];
			y3 = verts[(c[i+2]*3)+1];
			z3 = verts[(c[i+2]*3)+2];

			int tx=30; int ty=30; int tz=30;

			gc.drawLine((int)x1+tx, (int)z1+tz, (int)x2+tx, (int)z2+tz);
			gc.drawLine((int)x2+tx, (int)z2+tz, (int)x3+tx, (int)z3+tz);
			gc.drawLine((int)x3+tx, (int)z3+tz, (int)x1+tx, (int)z1+tz);
		}
	}

	public void drawArrays(int a, int b, int c)
	{
		//System.out.println("drawArrays("+a+", "+b+", "+c+");");
	}


	public void bindTexture(int a, Texture b)
	{
		//System.out.println("bindTexture("+a+");");
	}

	public void texCoordPointerub(int a, int b, byte[] c)
	{
		/*
		System.out.print("texCoords("+a+", "+b+", [ ");
		for(int i=0; i<c.length; i++)
		{
			System.out.print(""+((int)c[i])+", ");
		}
		System.out.println("]); // "+a+", "+b+", len "+c.length);
		*/
	}


	public void enableClientState(int a) {  }

	public void disableClientState(int a) {  }

	public void enable(int a) {  }

	public void disable(int a) {  }

	public void blit(Graphics a, int b, int c, int d, int e)
	{
		//System.out.println("blit("+b+", "+c+", "+d+", "+e+");");
		a.drawImage(platformImage, 0, 0, Graphics.LEFT|Graphics.TOP);
		//g.drawLine(0,0,96,65);
		//for(int i=0; i<65; i++)
		//{
		//	g.drawLine(0,i,96,i);
		//}
	}


	private void identity(double[] m)
	{
		m[0]  = 1; m[1]  = 0; m[2]  = 0; m[3]  = 0;
		m[4]  = 0; m[5]  = 1; m[6]  = 0; m[7]  = 0;
		m[8]  = 0; m[9]  = 0; m[10] = 1; m[11] = 0;
		m[12] = 0; m[13] = 0; m[14] = 0; m[15] = 1;
	}

	private void clone(double[] m1, double[] m2)
	{
		for(int i=0; i<16; i++)
		{
			m1[i] = m2[i];
		}
	}

	private void matmul(double[] m1, double[] m2)
	{
		tempm[0]  = m1[0]*m2[0] + m1[1]*m2[4]  + m1[2]*m2[8]   + m1[3]*m2[12];
		tempm[1]  = m1[0]*m2[1] + m1[1]*m2[5]  + m1[2]*m2[9]   + m1[3]*m2[13];
		tempm[2]  = m1[0]*m2[2] + m1[1]*m2[6]  + m1[2]*m2[10]  + m1[3]*m2[14];
		tempm[3]  = m1[0]*m2[3] + m1[1]*m2[7]  + m1[2]*m2[11]  + m1[3]*m2[15];

		tempm[4]  = m1[4]*m2[0] + m1[5]*m2[4]  + m1[6]*m2[8]   + m1[7]*m2[12];
		tempm[5]  = m1[4]*m2[1] + m1[5]*m2[5]  + m1[6]*m2[9]   + m1[7]*m2[13];
		tempm[6]  = m1[4]*m2[2] + m1[5]*m2[6]  + m1[6]*m2[10]  + m1[7]*m2[14];
		tempm[7]  = m1[4]*m2[3] + m1[5]*m2[7]  + m1[6]*m2[11]  + m1[7]*m2[15];

		tempm[8]  = m1[8]*m2[0] + m1[9]*m2[4]  + m1[10]*m2[8]   + m1[11]*m2[12];
		tempm[9]  = m1[8]*m2[1] + m1[9]*m2[5]  + m1[10]*m2[9]   + m1[11]*m2[13];
		tempm[10] = m1[8]*m2[2] + m1[9]*m2[6]  + m1[10]*m2[10]  + m1[11]*m2[14];
		tempm[11] = m1[8]*m2[3] + m1[9]*m2[7]  + m1[10]*m2[11]  + m1[11]*m2[15];
		
		tempm[12] = m1[12]*m2[0] + m1[13]*m2[4]  + m1[14]*m2[8 ]  + m1[15]*m2[12];
		tempm[13] = m1[12]*m2[1] + m1[13]*m2[5]  + m1[14]*m2[9 ]  + m1[15]*m2[13];
		tempm[14] = m1[12]*m2[2] + m1[13]*m2[6]  + m1[14]*m2[10]  + m1[15]*m2[14];
		tempm[15] = m1[12]*m2[3] + m1[13]*m2[7]  + m1[14]*m2[11]  + m1[15]*m2[15];

		clone(m1, tempm);
	}

}
