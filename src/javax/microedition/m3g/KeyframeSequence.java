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

public class KeyframeSequence extends Object3D
{

	public static final int CONSTANT = 192;
	public static final int LINEAR = 176;
	public static final int LOOP = 193;
	public static final int SLERP = 177;
	public static final int SPLINE = 178;
	public static final int SQUAD = 179;
	public static final int STEP = 180;


	private int duration;
	private int intType;
	private int keyframe;
	private int keyframes;
	private int repeat;
	private int rangeFirst;
	private int rangeLast;


	public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {  }


	public int getComponentCount() { return 0; }

	public int getDuration() { return duration; }

	public int getInterpolationType() { return intType; }

	public int getKeyframe(int index, float[] value) { return keyframe; }

	public int getKeyframeCount() { return keyframes; }

	public int getRepeatMode() { return repeat; }

	public int getValidRangeFirst() { return rangeFirst; }

	public int getValidRangeLast() { return rangeLast; }

	public void setDuration(int value) { duration = value; }

	public void setKeyframe(int index, int time, float[] value) { keyframe=index; }

	public void setRepeatMode(int mode) { repeat=mode; }

	public void setValidRange(int first, int last) { rangeFirst=first; rangeLast=last; }

}
