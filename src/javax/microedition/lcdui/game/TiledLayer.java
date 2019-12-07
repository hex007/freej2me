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
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.recompile.mobile.PlatformImage;
import org.recompile.mobile.PlatformGraphics;


public class TiledLayer extends Layer
{

	private Image image;
	private Image canvas;
	private PlatformGraphics gc;
	private int rows;
	private int cols;
	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;
	private int tilesWidth;
	private int tilesHeight;

	private int[] animatedTiles;
	private int animatedTileCount = 0;

	private int[][] tiles;


	public TiledLayer(int colsw, int rowsh, Image baseimage, int tilewidth, int tileheight)
	{
		System.out.println("Tiled Layer");
		setStaticTileSet(baseimage, tilewidth, tileheight);

		rows = rowsh;
		cols = colsw;
		tileWidth = tilewidth;
		tileHeight = tileheight;

		x = 0;
		y = 0;
		width = tileWidth*cols;
		height = tileHeight*rows;

		canvas = Image.createImage(width, height);
		gc = canvas.platformImage.getGraphics();

		gc.clearRect(0,0,width,height);

		animatedTiles = new int[255];

		tiles = new int[colsw][rowsh];
	}

	int createAnimatedTile(int staticTileIndex)
	{
		animatedTileCount++;
		animatedTiles[animatedTileCount] = staticTileIndex;
		return 0-animatedTileCount;
	}

	public void fillCells(int col, int row, int numCols, int numRows, int tileIndex)
	{
		for (int c=0; c<numCols; c++)
		{
			for (int r=0; r<numRows; r++)
			{
				tiles[col+c][row+r]=tileIndex;
				drawTile(tileIndex, (col+c)*tileWidth, (row+r)*tileHeight);
			}
		}
	}

	public int getAnimatedTile(int animatedTileIndex)
	{
		return animatedTiles[0-animatedTileIndex];	
	}

	public int getCell(int col, int row) { return tiles[col][row]; }

	public int getCellHeight() { return tileHeight; }

	public int getCellWidth() { return tileWidth; }

	public int getColumns() { return cols; }

	public int getRows() { return rows; }

	public void paint(Graphics g)
	{
		g.drawImage(canvas, x, y, 0);
	}

	private void drawTiles()
	{
		int tile;
		for (int c=0; c<cols; c++)
		{
			for (int r=0; r<rows; r++)
			{
				tile = tiles[c][r];
				if(tile<0) { tile = animatedTiles[0-tile]; }
				if(tile>0) { drawTile(tile, c*tileWidth, r*tileHeight); }
			}
		}
	}

	private void drawTile(int tile, int xdest, int ydest)
	{
		//tile--;
		int r = tileHeight * (tile / tilesWidth);
		int c = tileWidth * (tile % tilesWidth);
		gc.drawRegion(image, c, r, tileWidth, tileHeight, 0, xdest, ydest, 0);
	}

	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex)
	{
		int tile;
		animatedTiles[0-animatedTileIndex] = staticTileIndex;
		for (int c=0; c<cols; c++)
		{
			for (int r=0; r<rows; r++)
			{
				tile = tiles[c][r];
				if(tile==animatedTileIndex)
				{
					drawTile(animatedTiles[0-animatedTileIndex], c*tileWidth, r*tileHeight);
				}
			}
		}
	}

	public void setCell(int col, int row, int tileIndex)
	{
		tiles[col][row] = tileIndex;
		drawTile(tileIndex, col*tileWidth, row*tileHeight);
	}

	public void setStaticTileSet(Image baseimage, int tilewidth, int tileheight)
	{
		image = baseimage;
		tileWidth = tilewidth;
		tileHeight = tileheight;
		tilesWidth = (int)Math.floor(image.getWidth()/tilewidth);
		tilesHeight = (int)Math.floor(image.getHeight()/tileheight);
	}
}
