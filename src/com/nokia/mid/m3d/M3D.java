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

	private double[] scalem = new double[16];

	private double[] stackm = new double[16];

	private double[] transm = new double[16];

	private double[] rotm   = new double[16];
	
	private double[] stackr = new double[16];
	
	private double[] stackt = new double[16];
	
	private double[] tempm = new double[16];

	private double[] tempr = new double[16];

	private double[] tempt = new double[16];

	private int width;

	private int height;

	private double[] verts = new double[300];
	private int vertCount;

	private PlatformImage platformImage;
	private PlatformGraphics gc;

	private int color = 0xFF000000;
	private int clearcolor = 0xFFFFFFFF;


	private M3D() {  }

	public static M3D createInstance()
	{
		return new M3D();
	}


	public void setupBuffers(int a, int displayWidth, int displayHeight)
	{
		// a == flags c.c (init?) passes 0x20 | 0x1

		width = displayWidth;
		height = displayHeight;
		platformImage = new PlatformImage(width, height);
		gc = platformImage.getGraphics();
	}

	public void removeBuffers() {  } // runs only on app shutdown

	public void cullFace(int a) {  } // guessing front or back facing? set to 1029

	public void viewport(int a, int b, int c, int d) {  }

	public void clear(int a) // what is a? c.z (Get Ready) passes 0x4100 (16640)
	{
		////System.out.println("clear();");
		gc.setColor(clearcolor);
		gc.fillRect(0, 0, width, height);
		gc.setColor(color);
		identity(matrix);
	}


	public void matrixMode(int mode)
	{
		//System.out.println("matrixMode("+mode+");");
		// mode is 5889 for frustrum, 5888 , 
	}

	public void loadIdentity()
	{
		identity(matrix);
	}

	public void frustumxi(int a, int b, int c, int d, int near, int far)
	{
		//System.out.println("frustrumxi("+a+", "+b+", "+c+", "+d+", "+near+", "+far+");");

		// c.c: bu.frustumxi(-bp << 11, bp << 11, -bo << 11, bo << 11, 196608, 65536000);
	}

	public void scalexi(int x, int y, int z)
	{ 
		//System.out.println("scalexi("+x+", "+y+", "+z+");");
	}

	public void translatexi(int x, int y, int z) // This seems to work correctly
	{
		//System.out.println("translatexi("+x+", "+y+", "+z+");");
		x = (x/65536);
		y = (y/65536);
		z = (z/65536);
		tempt[0]  = 1; tempt[1]  = 0; tempt[2]  = 0; tempt[3]  = 0;
		tempt[4]  = 0; tempt[5]  = 1; tempt[6]  = 0; tempt[7]  = 0;
		tempt[8]  = 0; tempt[9]  = 0; tempt[10] = 1; tempt[11] = 0;
		tempt[12] = x; tempt[13] = y; tempt[14] = z; tempt[15] = 1;

		clone(transm, tempt);
	}

	public void rotatexi(int Y, int Z, int X, int W) // probably not a quaternion 
	{
		//System.out.println("rotatexi("+Y+", "+Z+", "+X+", "+W+");");
		// 1 degree = 0.0174533 rad
		// from d:1343 rotatexi(1310720, 65536, 0, 0);
		// from d:1347 rotatexi(c0, 65536, 0, 0);
		// from d:1354 rotatexi(cx * 90, 0, 65536, 0);

		double x = (X/65536)*0.0174533;
		double y = (Y/65536)*0.0174533;
		double z = (Z/65536)*0.0174533;

		/*
		// rotate on x
		q1m[0]  =  1; q1m[1]  =  0;            q1m[2]  =  0;           q1m[3]  =  0;
		q1m[4]  =  0; q1m[5]  =  Math.cos(x);  q1m[6]  =  Math.sin(x); q1m[7]  =  0;
		q1m[8]  =  0; q1m[9]  = -Math.sin(x);  q1m[10] =  Math.cos(x); q1m[11] =  0;
		q1m[12] =  0; q1m[13] =  0;            q1m[14] =  0;           q1m[15] =  1;
		matmul(matrix, q1m);
		*/

		// rotate on y
		tempr[0]  =  Math.cos(y); tempr[1]  =  0; tempr[2]  = -Math.sin(y); tempr[3]  =  0;
		tempr[4]  =  0;           tempr[5]  =  1; tempr[6]  =  0;           tempr[7]  =  0;
		tempr[8]  =  Math.sin(y); tempr[9]  =  0; tempr[10] =  Math.cos(y); tempr[11] =  0;
		tempr[12] =  0;           tempr[13] =  0; tempr[14] =  0;           tempr[15] =  1;
		clone(rotm, tempr);

		/*
		// rotate on z
		q1m[0]  =  Math.cos(z); q1m[1]  =  Math.sin(z); q1m[2]  =  0; q1m[3]  =  0;
		q1m[4]  = -Math.sin(z); q1m[5]  =  Math.cos(z); q1m[6]  =  0; q1m[7]  =  0;
		q1m[8]  =  0;           q1m[9]  =  0;           q1m[10] =  1; q1m[11] =  0;
		q1m[12] =  0;           q1m[13] =  0;           q1m[14] =  0; q1m[15] =  1;
		matmul(matrix, q1m);
		*/
	}

	public void pushMatrix() // game doesn't seem to push more than one thing at a time
	{ 
		//System.out.println("pushMatrix();");
		//clone(stackm, matrix);
		clone(stackr, rotm);
		clone(stackt, transm);
	}

	public void popMatrix()
	{
		//System.out.println("popMatrix();");
		//clone(matrix, stackm);
		clone(rotm, stackr);
		clone(transm, stackt);
	}

	
	public void vertexPointerub(int a, int b, byte[] vertices) 
	{
		//System.out.println("vertexPointerub");

		for(int i=0; i<vertices.length; i++)
		{
			verts[i] = vertices[i];
		}
		vertCount = vertices.length;
	}

	public void color4ub(byte r, byte g, byte b, byte a)
	{
		color = ((r&0xFF)<<16) | ((g&0xFF)<<8) | (b&0xFF);
	}

	public void clearColor4ub(byte r, byte g, byte b, byte a)
	{
		clearcolor = ((r&0xFF)<<16) | ((g&0xFF)<<8) | (b&0xFF);
	}

	
	public void drawElementsub(int a, int b, byte[] faces)
	{
		gc.setColor(color);
		
		//System.out.println("drawElementsub");

		double x, y, z, theta;

		identity(matrix);
		matmul(matrix, rotm);
		matmul(matrix, transm);
		matmul(matrix, stackt);
		matmul(matrix, stackr);
		applyMatrix(matrix);

		for(int i=0; i<vertCount; i+=3)
		{
			x = verts[i];
			y = verts[i+1]; 
			z = verts[i+2];
			verts[i] *=0.45;
			verts[i+1]*=0.45; 
			verts[i+2]*=0.45;
		}

		// draw elements
		int x1, y1, z1, x2, y2, z2, x3, y3, z3;
		x1=0; y1=0; z1=0;
		for(int i=0; i<faces.length; i+=3)
		{
			x1 = (int)verts[(faces[i]*3)];
			y1 = (int)verts[(faces[i]*3)+1];
			z1 = (int)verts[(faces[i]*3)+2];

			x2 = (int)verts[(faces[i+1]*3)];
			y2 = (int)verts[(faces[i+1]*3)+1];
			z2 = (int)verts[(faces[i+1]*3)+2];
			
			x3 = (int)verts[(faces[i+2]*3)];
			y3 = (int)verts[(faces[i+2]*3)+1];
			z3 = (int)verts[(faces[i+2]*3)+2];
			
			int ox = 50;
			int oy = 30;
			x1 = x1+ox;
			x2 = x2+ox;
			x3 = x3+ox;
			y1 = z1+oy;
			y2 = z2+oy;
			y3 = z3+oy;

			gc.fillTriangle(x1, y1, x2, y2, x3, y3, color);
			gc.drawTriangle(x1, y1, x2, y2, x3, y3, 0);
			
		}
		//System.out.println("one of em: "+x1+", "+z1);
	}

	public void drawArrays(int a, int b, int c)  // called after clear -- background?
	{
		//System.out.println("drawArrays");
	}


	public void bindTexture(int a, Texture b)
	{
		//System.out.println("bindTexture");
	}

	public void texCoordPointerub(int a, int b, byte[] UVs)
	{
		//System.out.println("texCoordPointerub");
	}


	public void enableClientState(int flags)
	{
		//System.out.println("enableClientState("+flags+")");
	}

	public void disableClientState(int flags)
	{
		//System.out.println("disableClientState("+flags+")");
	}

	public void enable(int feature)
	{
		//System.out.println("enable("+feature+")");
	}

	public void disable(int feature)
	{
		//System.out.println("disable("+feature+")");
	}

	public void blit(Graphics g, int x, int y, int w, int h) // 0, 0, 95, 65
	{
		//System.out.println("blit("+x+", "+y+", "+w+", "+h+");");
		g.drawImage(platformImage, x, y, Graphics.LEFT|Graphics.TOP);
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
	void applyMatrix(double[] m)
	{
		for(int i=0; i<vertCount; i+=3)
		{	
			double x = verts[i];
			double y = verts[i+1]; 
			double z = verts[i+2];
			verts[i]   = x*m[0] + y*m[4] + z*m[8]  + m[12];
			verts[i+1] = x*m[1] + y*m[5] + z*m[9]  + m[13];
			verts[i+2] = x*m[2] + y*m[6] + z*m[10] + m[14]; 
		}
	}
}
