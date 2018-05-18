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

import javax.microedition.lcdui.Graphics;

public class M3D
{
	public M3D() { }

	public static M3D createInstance() { return new M3D(); }

	public void setupBuffers(int a, int b, int c) {  }

	public void color4ub(byte a, byte b, byte c, byte d) {  }

	public void clearColor4ub(byte a, byte b, byte c, byte d) {  }

	public void enable(int a) {  }

	public void cullFace(int a) {  }

	public void viewport(int a, int b, int c, int d) {  }

	public void matrixMode(int a) {  }

	public void frustumxi(int a, int b, int c, int d, int e, int f) {  }

	public void enableClientState(int a) {  }

	public void loadIdentity() {  }

	public void clear(int a) {  }

	public void scalexi(int a, int b, int c) {  }

	public void translatexi(int a, int b, int c) {  }

	public void vertexPointerub(int a, int b, byte[] c) {  }

	public void drawArrays(int a, int b, int c) {  }

	public void rotatexi(int a, int b, int c, int d) {  }

	public void pushMatrix() {  }

	public void drawElementsub(int a, int b, byte[] c) {  }

	public void popMatrix() {  }

	public void bindTexture(int a, Texture b) {  }

	public void texCoordPointerub(int a, int b, byte[] c) {  }

	public void disableClientState(int a) {  }

	public void disable(int a) {  }

	public void blit(Graphics a, int b, int c, int d, int e) {  }

}
