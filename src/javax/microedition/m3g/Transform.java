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

public class Transform
{

	public Transform() {  }

	public Transform(Transform transform) {  }


	public void get(float[] matrix) {  }

	public void invert() {  }

	public void postMultiply(Transform transform) {  }

	public void postRotate(float angle, float ax, float ay, float az) {  }

	public void postRotateQuat(float qx, float qy, float qz, float qw) {  }

	public void postScale(float sx, float sy, float sz) {  }

	public void postTranslate(float tx, float ty, float tz) {  }

	public void set(float[] matrix) {  }

	public void set(Transform transform) {  }

	public void setIdentity() {  }

	public void transform(float[] vectors) {  }

	public void transform(VertexArray in, float[] out, boolean W) {  }

	public void transpose() {  }

}
