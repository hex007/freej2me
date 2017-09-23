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

public class Fog extends Object3D
{

	public static final int	EXPONENTIAL = 80;
	public static final int LINEAR = 81;


	private float near;
	private float far;
	private int mode;
	private int color;
	private float density;


	public Fog() {  }


	public int getColor() { return color; }

	public float getDensity() { return density; }

	public float getFarDistance() { return far; }

	public int getMode() { return mode; }

	public float getNearDistance() { return near; }

	public void setColor(int RGB) { color = RGB; }

	public void setDensity(float value) { density = value; }

	public void setLinear(float Near, float Far) { near=Near; far=Far; }

	public void setMode(int value) { mode = value; }

}
