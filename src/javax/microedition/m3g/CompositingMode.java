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


public class CompositingMode extends Object3D
{

	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;


	private int blending;
	private float alphaThreshold;
	private boolean alphaWrite;
	private boolean depthWrite;
	private boolean depthTest;
	private float depthOffsetUnits;
	private float depthOffsetFactor;
	private boolean colorWrite;


	public CompositingMode() {  }


	public float getAlphaThreshold() { return alphaThreshold; }

	public int getBlending() { return blending; }

	public float getDepthOffsetFactor() { return depthOffsetFactor; }

	public float getDepthOffsetUnits() { return depthOffsetUnits; }

	public boolean isAlphaWriteEnabled() { return alphaWrite; }

	public boolean isColorWriteEnabled() { return colorWrite; }

	public boolean isDepthTestEnabled() { return depthTest; }

	public boolean isDepthWriteEnabled() { return depthWrite; }

	public void setAlphaThreshold(float threshold) { alphaThreshold = threshold; }

	public void setAlphaWriteEnable(boolean enable) { alphaWrite = enable; }

	public void setBlending(int mode) { blending = mode; }

	public void setColorWriteEnable(boolean enable) { colorWrite = enable; }

	public void setDepthOffset(float factor, float units)
	{
		depthOffsetFactor = factor;
		depthOffsetUnits = units;
	}

	public void setDepthTestEnable(boolean enable) { depthTest = enable; }

	public void setDepthWriteEnable(boolean enable) { depthWrite = enable; }

}
