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

public class Effect3D
{
	public static final int NORMAL_SHADING = 0;
	public static final int TOON_SHADING = 1;


	private Light light;
	private int shading = 0;
	private int threshold = 0;
	private int toonthreshold = 0;
	private int thresholdhigh = 0;
	private int toonthresholdhigh = 0;
	private int thresholdlow = 0;
	private int toonthresholdlow = 0;
	private boolean trans = false;
	private boolean strans = false;
	private Texture sphereTex;
	private Texture sphereMap;

	public Effect3D() {  }

	public Effect3D(Light light, int shading, boolean isEnableTrans, Texture tex) {  }


	public final Light getLight() { return light; }

	public final void setLight(Light light1) { light = light1; }

	public final int getShading() { return shading; }

	public final int getShadingType() { return shading; }

	public final void setShading(int value) { shading = value; }

	public final void setShadingType(int value) { shading = value; }

	public final int getThreshold() { return threshold; }

	public final int getToonThreshold() { return toonthreshold; }

	public final int getThresholdHigh() { return thresholdhigh; }

	public final int getToonHigh() { return toonthresholdhigh; }

	public final int getThresholdLow() { return thresholdlow; }

	public final int getToonLow() { return toonthresholdlow; }

	public final void setThreshold(int i, int j, int k) // unknown order
	{
		threshold = i;
		thresholdlow = j;
		thresholdhigh = k;
	}

	public final void setToonParams(int i, int j, int k)
	{
		toonthreshold = i;
		toonthresholdlow = j;
		toonthresholdhigh = k;
	}

	public final boolean isSemiTransparentEnabled() { return strans; }

	public final boolean isTransparency() { return false; }

	public final void setSemiTransparentEnabled(boolean value) { strans = value; }

	public final void setTransparency(boolean value) { trans = value; }

	public final Texture getSphereMap() { return sphereMap; }

	public final Texture getSphereTexture() { return sphereTex; }

	public final void setSphereMap(Texture map) { sphereMap = map; }

	public final void setSphereTexture(Texture texture) { sphereTex = texture; }
}
