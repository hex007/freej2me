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

public class Light extends Node
{

	public static final int AMBIENT = 128;
	public static final int DIRECTIONAL = 129;
	public static final int OMNI = 130;
	public static final int SPOT = 131;


	private int mode;
	private int color;
	private float intensity;
	private float linear;
	private float quadratic;
	private float constant;
	private float angle;
	private float exponent;


	public Light() {  }


	public int getColor() { return color; }

	public float getConstantAttenuation() { return constant; }

	public float getIntensity() { return intensity; }

	public float getLinearAttenuation() { return linear; }

	public int getMode() { return mode; }

	public float getQuadraticAttenuation() { return quadratic; }

	public float getSpotAngle() { return angle; }

	public float getSpotExponent() { return exponent; }

	public void setAttenuation(float c, float l, float q)
	{
		constant = c;
		linear = l;
		quadratic = q;
	}

	public void setColor(int RGB) { color = RGB; }

	public void setIntensity(float value) { intensity = value; }

	public void setMode(int value) { mode = value; }

	public void setSpotAngle(float theta) { angle = theta; }

	public void setSpotExponent(float exp) { exponent = exp; }

}
