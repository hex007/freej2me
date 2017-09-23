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

public class Material extends Object3D
{

	public static final int AMBIENT = 1024;
	public static final int DIFFUSE = 2048;
	public static final int EMISSIVE = 4096;
	public static final int SPECULAR = 8192;


	private int color;
	private float shine;
	private boolean tracking;


	public Material() {  }


	public int getColor(int target) { return color; }

	public float getShininess() { return shine; }

	public boolean isVertexColorTrackingEnabled() { return tracking; }

	public void setColor(int target, int ARGB) { color=ARGB; }

	public void setShininess(float shininess) { shine=shininess; }

	public void setVertexColorTrackingEnable(boolean enable) { tracking = enable; }

}
