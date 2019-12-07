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

import java.util.ArrayList;

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

	private ArrayList<Integer> sequence = new ArrayList<Integer>();

	public Image sprite;

	private int frame=0;
	private int frameWidth;
	private int frameHeight;
	private int frameCount=1;
	private int imgWidth;
	private int imgHeight;

	public int rowCount=1;
	public int colCount=1;

	public Sprite()
	{
		System.out.println("Sprite A");
	}

	public Sprite(Image img)
	{
		//System.out.println("Sprite B"); // Found in use, Pacman Championship Edition
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		frameWidth = imgWidth; 
		frameHeight = imgHeight;
		colCount = 1;
		rowCount = 1;
		frameCount = 1;
		setImage(img, imgWidth, imgHeight);
	}

	public Sprite(Image img, int frameW, int frameH)
	{
		//System.out.println("Sprite C"); // Found in use, Pacman Championship Edition
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();
		frameWidth = frameW;
		frameHeight = frameH;
		colCount = (int)(imgWidth/frameWidth);
		rowCount = (int)(imgHeight/frameHeight);
		frameCount = rowCount * colCount;
		setImage(img, frameWidth, frameHeight);
	}

	public Sprite(Sprite s)
	{
		System.out.println("Sprite D");
		imgWidth = s.sprite.getWidth();
		imgHeight = s.sprite.getHeight();
		frameWidth = imgWidth; 
		frameHeight = imgHeight;
		colCount = 1;
		rowCount = 1;
		frameCount = 1;
		setImage(s.sprite, imgWidth, imgHeight);
	}

	public boolean collidesWith(Image image, int x, int y, boolean pixelLevel)
	{
		return false;
	}

	public boolean collidesWith(Sprite s, boolean pixelLevel)
	{
		int Ax = (x-refX) + hitX;
		int Ay = (y-refY) + hitY;
		int Aw = hitWidth;
		int Ah = hitHeight;

		int Bx = (s.getX()-s.getRefPixelX()) + s.getHitX();
		int By = (s.getY()-s.getRefPixelY()) + s.getHitY();
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
			int r = frameHeight * (int)(f / colCount);
			int c = frameWidth * (int)(f % colCount);
			g.drawRegion(sprite, c, r, frameWidth, frameHeight, transform, x-refX, y-refY, 0);
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

	public void setFrame(int sequenceIndex)
	{
		if(sequenceIndex<0 || sequenceIndex >= sequence.size())
		{
			throw new IndexOutOfBoundsException();
		}
		frame = sequenceIndex;
	}

	public void setFrameSequence(int[] fsequence)
	{
		//System.out.println("Set Frame Sequence");
		try
		{
			frame = 0;
			sequence.clear();
			for(int i=0; i<fsequence.length; i++)
			{
				sequence.add(i, fsequence[i]);
			}
		}
		catch (Exception e)
		{
			System.out.println("Problem with Sprite.setFrameSequence");
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
			sequence.add(i, i);
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
