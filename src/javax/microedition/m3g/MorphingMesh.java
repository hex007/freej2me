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

public class MorphingMesh extends Mesh
{

	private VertexBuffer morphtarget;
	private float[] weights;


	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] submeshes, Appearance[] appearances) {  }

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer submesh, Appearance appearance) {  }


	public VertexBuffer getMorphTarget(int index) { return morphtarget; }

	public int getMorphTargetCount() { return 0; }

	public void getWeights(float[] store)
	{
		for (int i=0; i<weights.length; i++)
		{
			store[i]=weights[i];
		}
	}

	public void setWeights(float[] values) { weights = values; }

}
