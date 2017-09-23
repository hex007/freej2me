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
package javax.microedition.m3g;

import java.util.Hashtable;

import org.recompile.mobile.Mobile;

public class Graphics3D
{

	public static final int ANTIALIAS = 2;
	public static final int DITHER = 4;
	public static final int OVERWRITE = 16;
	public static final int TRUE_COLOR = 8;


	private static Hashtable properties;

	private int viewx;
	private int viewy;
	private int vieww;
	private int viewh;
	private float near;
	private float far;


	public int addLight(Light light, Transform transform) { return 0; }

	public void bindTarget(java.lang.Object target) {  }

	public void bindTarget(java.lang.Object target, boolean depthBuffer, int hints) {  }

	public void clear(Background background) {  }

	public Camera getCamera(Transform transform) { return new Camera(); }

	public float getDepthRangeFar() { return far; }

	public float getDepthRangeNear() { return near; }

	public int getHints() { return 0; }

	public static Graphics3D getInstance() { return Mobile.getGraphics3D(); }

	public Light getLight(int index, Transform transform) { return new Light(); }

	public int getLightCount() { return 0; }

	public static Hashtable getProperties() { return properties; }

	public Object getTarget() { return null; }

	public int getViewportHeight() { return viewh; }

	public int getViewportWidth() { return vieww; }

	public int getViewportX() { return viewx; }

	public int getViewportY() { return viewy; }

	public boolean isDepthBufferEnabled() { return false; }

	public void releaseTarget() {  }

	public void render(Node node, Transform transform) {  }

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform) {  }

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform, int scope) {  }

	public void render(World world) {  }

	public void resetLights() {  }

	public void setCamera(Camera camera, Transform transform) {  }

	public void setDepthRange(float Near, float Far) { near=Near; far=Far; }

	public void setLight(int index, Light light, Transform transform) {  }

	public void setViewport(int x, int y, int width, int height) { viewx=x; viewy=y; vieww=width; viewh=height; }

}
