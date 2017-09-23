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

public class SkinnedMesh extends Mesh
{

	private Group skeleton;


	public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton) {  }

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance, Group skeleton) {  }


	public void addTransform(Node bone, int weight, int firstVertex, int numVertices) {  }

	public void getBoneTransform(Node bone, Transform transform) {  }

	public int getBoneVertices(Node bone, int[] indices, float[] weights) { return 0; }

	public Group getSkeleton() { return skeleton; }

}
