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

import javax.microedition.lcdui.Graphics;

public class Graphics3D
{
	public static final int COMMAND_LIST_VERSION_1_0 = 254; //-2 ?
	public static final int COMMAND_END = Integer.MIN_VALUE;
	public static final int COMMAND_NOP = 129;
	public static final int COMMAND_FLUSH = 130;
	public static final int COMMAND_ATTRIBUTE = 131;
	public static final int COMMAND_CLIP = 132;
	public static final int COMMAND_CENTER = 133;
	public static final int COMMAND_TEXTURE_INDEX = 134;
	public static final int COMMAND_AFFINE_INDEX = 135;
	public static final int COMMAND_PARALLEL_SCALE = 144;
	public static final int COMMAND_PARALLEL_SIZE = 145;
	public static final int COMMAND_PERSPECTIVE_FOV = 146;
	public static final int COMMAND_PERSPECTIVE_WH = 147;
	public static final int COMMAND_AMBIENT_LIGHT = 160;
	public static final int COMMAND_DIRECTION_LIGHT = 161;
	public static final int COMMAND_THRESHOLD = 175;
	public static final int PRIMITVE_POINTS = 16;
	public static final int PRIMITVE_LINES = 32;
	public static final int PRIMITVE_TRIANGLES = 48;
	public static final int PRIMITVE_QUADS = 64;
	public static final int PRIMITVE_POINT_SPRITES = 80;
	public static final int POINT_SPRITE_LOCAL_SIZE = 0;
	public static final int POINT_SPRITE_PIXEL_SIZE = 1;
	public static final int POINT_SPRITE_PERSPECTIVE = 0;
	public static final int POINT_SPRITE_NO_PERS = 2;
	public static final int ENV_ATTR_LIGHTING = 1;
	public static final int ENV_ATTR_SPHERE_MAP = 2;
	public static final int ENV_ATTR_TOON_SHADING = 4;
	public static final int ENV_ATTR_SEMI_TRANSPARENT = 8;
	public static final int PATTR_LIGHTING = 1;
	public static final int PATTR_SPHERE_MAP = 2;
	public static final int PATTR_COLORKEY = 16;
	public static final int PATTR_BLEND_NORMAL = 0;
	public static final int PATTR_BLEND_HALF = 32;
	public static final int PATTR_BLEND_ADD = 64;
	public static final int PATTR_BLEND_SUB = 96;
	public static final int PDATA_NORMAL_NONE = 0;
	public static final int PDATA_NORMAL_PER_FACE = 2;
	public static final int PDATA_NORMAL_PER_VERTEX = 3;
	public static final int PDATA_COLOR_NONE = 0;
	public static final int PDATA_COLOR_PER_COMMAND = 4;
	public static final int PDATA_COLOR_PER_FACE = 8;
	public static final int PDATA_TEXURE_COORD_NONE = 0;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_CMD = 16;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_FACE = 32;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_VERTEX = 48;
	public static final int PDATA_TEXURE_COORD = 48;


	public void renderPrimitives(Texture tex, int p1, int p2, FigureLayout figLayout, Effect3D e3d, int p3, int p4, int[] p5, int[] p6, int[] p7, int[] p8) {  }

	public void renderPrimitives(Figure fig, int p1, int p2, FigureLayout figLayout, Effect3D paramEffect3D, int p3, int p4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4) { }

	public void drawCommandList(Figure fig, int p1, int p2, FigureLayout figLayout, Effect3D e3d, int[] p3) { }

	public void renderFigure(Figure fig, int p1, int p2, FigureLayout figLayout, Effect3D e3d) { }

	public void flush() { }

	public void drawFigure(Figure fig, int p1, int p2, FigureLayout figLayout, Effect3D e3d) { }

	public void dispose() { }

	public void bind(Graphics g) { }

	public void release(Graphics g) { }
}
