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
package	javax.microedition.lcdui.game;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class Sprite	extends	Layer
{

	public static final	int	TRANS_MIRROR = 2;
	public static final	int	TRANS_MIRROR_ROT180	= 1;
	public static final	int	TRANS_MIRROR_ROT270	= 4;
	public static final	int	TRANS_MIRROR_ROT90 = 7;
	public static final	int	TRANS_NONE = 0;
	public static final	int	TRANS_ROT180 = 3;
	public static final	int	TRANS_ROT270 = 6;
	public static final	int	TRANS_ROT90	= 5;


	private int refX = 0;
	private int refY = 0;

	private int hitX;
	private int hitY;
	private int hitWidth;
	private int hitHeight;

	private int transform;

	private Vector<Integer> sequence = new Vector<Integer>();

	private int frame;
	private int frameWidth;
	private int frameHeight;
	private int frameCount;

	public Image sprite;
	public int rowCount;
	public int colCount;

	public Sprite(Image image)
	{
		setImage(image, image.width, image.height);
	}

	public Sprite(Image image, int frameW, int frameH)
	{
		setImage(image, frameW, frameH);
	}

	public Sprite(Sprite s)
	{
		System.out.println("Sprite sprite");
		sprite = s.sprite;
	}

	public boolean collidesWith(Image image, int x, int y, boolean pixelLevel)
	{
		return false;
	}

	public boolean collidesWith(Sprite s, boolean pixelLevel)
	{
		int Ax = (x+refX) + hitX;
		int Ay = (y+refY) + hitY;
		int Aw = hitWidth;
		int Ah = hitHeight;

		int Bx = (s.getX()+s.getRefPixelX()) + s.getHitX();
		int By = (s.getY()+s.getRefPixelY()) + s.getHitY();
		int Bw = s.getHitWidth();
		int Bh = s.getHitHeight();

		if( (Ax+Aw)>Bx && Ax<(Bx+Bw) && (Ay+Ah)>By && Ay<(By+Bh) )
		{
			return true;
		}

		return false;
	}

	public boolean collidesWith(TiledLayer t, boolean pixelLevel)
	{
		return false;
	}

	public void defineCollisionRectangle(int x, int y, int width, int height)
	{
		hitX = x;
		hitY = y;
		hitWidth = width;
		hitHeight = height;
	}

	public void defineReferencePixel(int x, int y)
	{
		refX = x;
		refY = y;
	}

	public int getFrame() { return frame; }

	public int getFrameSequenceLength() { return sequence.size(); }

	public int getRawFrameCount() { return frameCount; }

	public int getRefPixelX() { return refX; }

	public int getRefPixelY() { return refY; }

	public void nextFrame()
	{
		if(frame<sequence.size()-1)
		{
			frame++;
		}
		else
		{
			frame=0;
		}
	}

	public void paint(Graphics g)
	{
		try
		{
			int f = sequence.get(frame);
			int r = frameHeight * (f/colCount);
			int c = frameWidth * (f % colCount);
			g.drawRegion(sprite, c, r, frameWidth, frameHeight, transform, x, y, 0);
		}
		catch (Exception e)
		{
			System.out.println("Problem drawing sprite");
		}
	}

	public void prevFrame()
	{
		if(frame>0)
		{
			frame--;
		}
		else
		{
			frame=sequence.size()-1;
		}
	}

	public void setFrame(int sequenceIndex) { frame = sequenceIndex; }

	public void setFrameSequence(int[] fsequence)
	{
		//System.out.println("Set Frame Sequence");
		try
		{
			frame = 0;
			sequence.clear();
			for(int i=0; i<fsequence.length; i++)
			{
				sequence.add(fsequence[i]);
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem with Sequence");
		}
	}

	public void setImage(Image img, int frameW, int frameH)
	{
		sprite = img;
		frameWidth = frameW;
		frameHeight = frameH;

		hitX = 0;
		hitY = 0;
		hitWidth = frameWidth;
		hitHeight = frameHeight;

		double spriteW = sprite.platformImage.width;
		double spriteH = sprite.platformImage.height;

		colCount = (int)Math.floor(spriteW/(double)frameW);
		rowCount = (int)Math.floor(spriteH/(double)frameH);

		frameCount = colCount * rowCount;

		sequence.clear();

		for(int i=0; i<frameCount; i++)
		{
			sequence.add(i);
		}
	}

	public void setRefPixelPosition(int x, int y)
	{
		refX = x;
		refY = y;
	}

	public void setTransform(int value) { transform = value; }

	public int getHitX() { return hitX; }
	public int getHitY() { return hitY; }
	public int getHitWidth() { return hitWidth; }
	public int getHitHeight() { return hitHeight; }

}
