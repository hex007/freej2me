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


public class AnimationController extends Object3D
{

	private float weight;
	private float speed;
	private int world;
	private float sequence;
	private int intStart;
	private int intEnd;


	public AnimationController() {  }


	public int getActiveIntervalEnd() { return intEnd; }

	public int getActiveIntervalStart()  { return intStart; }

	public float getPosition(int worldTime)  { return sequence; }

	public int getRefWorldTime()  { return world; }

	public float getSpeed()  { return speed; }

	public float getWeight()  { return weight; }

	public void setActiveInterval(int start, int end)
	{
		intStart = start;
		intEnd = end;
	}

	public void setPosition(float sequenceTime, int worldTime)
	{
		sequence = sequenceTime;
		world = worldTime;
	}

	public void setSpeed(float value, int worldTime)
	{
		speed = value;
		world = worldTime;
	}

	public void setWeight(float value)  { weight = value; }

}
