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

public abstract class Transformable extends Object3D
{

	public void getCompositeTransform(Transform transform) {  }

	public void getOrientation(float[] angleAxis) {  }

	public void getScale(float[] xyz) {  }

	public void getTransform(Transform transform) {  }

	public void getTranslation(float[] xyz) {  }

	public void postRotate(float angle, float ax, float ay, float az) {  }

	public void preRotate(float angle, float ax, float ay, float az) {  }

	public void scale(float sx, float sy, float sz) {  }

	public void setOrientation(float angle, float ax, float ay, float az) {  }

	public void setScale(float sx, float sy, float sz) {  }

	public void setTransform(Transform transform) {  }

	public void setTranslation(float tx, float ty, float tz) {  }

	public void translate(float tx, float ty, float tz) {  }

}
