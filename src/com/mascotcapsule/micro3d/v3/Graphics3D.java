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
	public static final int COMMAND_AFFINE_INDEX = 0x87000000; // -2030043136
	public static final int COMMAND_AMBIENT_LIGHT = 0xA0000000; // -1610612736
	public static final int COMMAND_ATTRIBUTE = 0x83000000; // -2097152000
	public static final int COMMAND_CENTER = 0x85000000; // -2063597568
	public static final int COMMAND_CLIP = 0x84000000; // -2080374784
	public static final int COMMAND_DIRECTION_LIGHT = 0xA1000000; // -1593835520
	public static final int COMMAND_END = 0x80000000; // -2147483648;
	public static final int COMMAND_FLUSH = 0x82000000; // -2113929216
	public static final int COMMAND_LIST_VERSION_1_0 = 0x1FFFFFF; //-33554431
	public static final int COMMAND_NOP = 0x81000000; // -2130706432
	public static final int COMMAND_PARALLEL_SCALE = 0x90000000; // -1879048192
	public static final int COMMAND_PARALLEL_SIZE = 0x91000000; // -1862270976
	public static final int COMMAND_PERSPECTIVE_FOV = 0x92000000; // -1845493760
	public static final int COMMAND_PERSPECTIVE_WH = 0x93000000; // -1828716544
	public static final int COMMAND_TEXTURE_INDEX = 0x86000000; // -2046820352
	public static final int COMMAND_THRESHOLD = 0xAF000000; // -1358954496
	public static final int ENV_ATTR_LIGHTING = 1;
	public static final int ENV_ATTR_SEMI_TRANSPARENT = 8;
	public static final int ENV_ATTR_SPHERE_MAP = 2;
	public static final int ENV_ATTR_TOON_SHADING = 4;
	public static final int PATTR_BLEND_ADD = 64;
	public static final int PATTR_BLEND_HALF = 32;
	public static final int PATTR_BLEND_NORMAL = 0;
	public static final int PATTR_BLEND_SUB = 96;
	public static final int PATTR_COLORKEY = 16;
	public static final int PATTR_LIGHTING = 1;
	public static final int PATTR_SPHERE_MAP = 2;
	public static final int PDATA_COLOR_NONE = 0;
	public static final int PDATA_COLOR_PER_COMMAND = 0x400; // 1024
	public static final int PDATA_COLOR_PER_FACE = 0x800; // 2048
	public static final int PDATA_NORMAL_NONE = 0;
	public static final int PDATA_NORMAL_PER_FACE = 0x200; // 512
	public static final int PDATA_NORMAL_PER_VERTEX = 0x300; // 768
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_CMD = 0x1000; // 4096
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_FACE = 0x2000; // 8192
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_VERTEX = 0x3000; // 12288
	public static final int PDATA_TEXURE_COORD = 0x3000; // 12288
	public static final int PDATA_TEXURE_COORD_NONE = 0;
	public static final int POINT_SPRITE_LOCAL_SIZE = 0;
	public static final int POINT_SPRITE_NO_PERS = 2;
	public static final int POINT_SPRITE_PERSPECTIVE = 0;
	public static final int POINT_SPRITE_PIXEL_SIZE = 1;
	public static final int PRIMITVE_POINTS = 0x1000000; // 16777216
	public static final int PRIMITVE_LINES = 0x2000000; // 33554432;
	public static final int PRIMITVE_TRIANGLES = 0x3000000; // 50331648;
	public static final int PRIMITVE_QUADS = 0x4000000; // 67108864;
	public static final int PRIMITVE_POINT_SPRITES = 0x5000000; // 83886080


	protected Graphics gc;


	public Graphics3D()
	{
		gc = null;
	}


	public void bind(Graphics g) { gc = g; }

	public void release(Graphics g) { gc = null; }

	public void dispose() { }

	public void flush() { }

	public void renderPrimitives(Texture texture, int x, int y, FigureLayout layout, Effect3D effect, int command, int numPrimitives, int[] vertexCoords, int[] normals, int[] textureCoords, int[] colors)
	{

	}

	public void renderFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect)
	{
	
	}

	public void drawCommandList(Texture[] textures, int x, int y, FigureLayout layout, Effect3D effect, int[] commandList)
	{

	}

	public void drawCommandList(Texture texture, int x, int y, FigureLayout layout, Effect3D effect, int[] commandList)
	{

	}

	public void drawFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect)
	{

	}


}
