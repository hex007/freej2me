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

import java.util.Vector;

public class Group extends Node
{

	private Vector<Node> nodes;


	public Group() {  }


	public void addChild(Node child)
	{
		try
		{
			nodes.add(child);
		}
		catch (Exception e) { }
	}

	public Node getChild(int index) { return nodes.get(index); }

	public int getChildCount() { return nodes.size(); }

	public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri) { return false; }

	public boolean pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri) { return false; }

	public void removeChild(Node child) { nodes.remove(child); }

}
