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

public class VertexBuffer extends Object3D
{

	public VertexBuffer() {  }


	public VertexArray getColors() { return new VertexArray(0,0,0); }

	public int getDefaultColor() { return 0; }

	public VertexArray getNormals() { return new VertexArray(0,0,0); }

	public VertexArray getPositions(float[] scaleBias) { return new VertexArray(0,0,0); }

	public VertexArray getTexCoords(int index, float[] scaleBias) { return new VertexArray(0,0,0); }

	public int getVertexCount() { return 0; }

	public void setColors(VertexArray colors) {  }

	public void setDefaultColor(int ARGB) {  }

	public void setNormals(VertexArray normals) {  }

	public void setPositions(VertexArray positions, float scale, float[] bias) {  }

	public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias) {  }

}
