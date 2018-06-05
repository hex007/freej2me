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
package org.recompile.mobile;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import com.nokia.mid.ui.DirectGraphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;

public class PlatformGraphics extends javax.microedition.lcdui.Graphics implements DirectGraphics
{
	protected BufferedImage canvas;
	protected Graphics2D gc;

	protected Color awtColor;

	protected int strokeStyle = SOLID;

	protected Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

	public PlatformGraphics platformGraphics;
	public PlatformImage platformImage;

	public PlatformGraphics(PlatformImage image)
	{
		canvas = image.getCanvas();
		gc = canvas.createGraphics();
		platformImage = image;

		platformGraphics = this;

		clipX = 0;
		clipY = 0;
		clipWidth = canvas.getWidth();
		clipHeight = canvas.getHeight();

		setColor(0,0,0);
		gc.setBackground(new Color(0, 0, 0, 0));
		gc.setFont(font.platformFont.awtFont);
	}

	public Graphics2D getGraphics2D()
	{
		return gc;
	}

	public BufferedImage getCanvas()
	{
		return canvas;
	}

	public void clearRect(int x, int y, int width, int height)
	{
		gc.clearRect(x, y, width, height);
	}

	public void copyArea(int subx, int suby, int subw, int subh, int x, int y, int anchor)
	{
		x = AnchorX(x, subw, anchor);
		y = AnchorY(y, subh, anchor);

		BufferedImage sub = canvas.getSubimage(subx, suby, subw, subh);

		gc.drawImage(sub, x, y, null);
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		gc.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void drawChar(char character, int x, int y, int anchor)
	{
		drawString(Character.toString(character), x, y, anchor);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y, int anchor)
	{
		drawString(data.toString(), x, y, anchor);
	}

	public void drawImage(Image image, int x, int y, int anchor)
	{
		try
		{
			int imgWidth = image.getWidth();
			int imgHeight = image.getHeight();

			x = AnchorX(x, imgWidth, anchor);
			y = AnchorY(y, imgHeight, anchor);

			gc.drawImage(image.platformImage.getCanvas(), x, y, null);
		}
		catch (Exception e)
		{
			System.out.println("drawImage A:"+e.getMessage());
		}
	}

	public void drawImage(Image image, int x, int y)
	{
		try
		{
			gc.drawImage(image.platformImage.getCanvas(), x, y, null);	
		}
		catch (Exception e)
		{
			System.out.println("drawImage B:"+e.getMessage());
		}
	}

	public void drawImage(BufferedImage image, int x, int y)
	{
		// called by Platform Image
		try
		{
			gc.drawImage(image, x, y, null);
		}
		catch (Exception e)
		{
			System.out.println("drawImage C:"+e.getMessage());
		}
	}

	public void flushGraphics(Image image, int x, int y, int width, int height)
	{
		// called by MobilePlatform.flushGraphics/repaint
		try
		{
			BufferedImage sub = image.platformImage.getCanvas().getSubimage(x, y, width, height);
			gc.drawImage(sub, x, y, null);
			//gc.drawImage(image.platformImage.getCanvas(), 0, 0, null);
		}
		catch (Exception e)
		{
			//System.out.println("flushGraphics A:"+e.getMessage());
		}
	}

	public void drawRegion(Image image, int subx, int suby, int subw, int subh, int transform, int x, int y, int anchor)
	{
		try
		{
			if(transform == 0)
			{
				BufferedImage sub = image.platformImage.getCanvas().getSubimage(subx, suby, subw, subh);
				x = AnchorX(x, subw, anchor);
				y = AnchorY(y, subh, anchor);
				gc.drawImage(sub, x, y, null);
			}
			else
			{
				PlatformImage sub = new PlatformImage(image, subx, suby, subw, subh, transform);
				x = AnchorX(x, sub.width, anchor);
				y = AnchorY(y, sub.height, anchor);
				gc.drawImage(sub.getCanvas(), x, y, null);
			}
		}
		catch (Exception e)
		{
			//System.out.println("drawRegion A (x:"+x+" y:"+y+" w:"+subw+" h:"+subh+"):"+e.getMessage());
		}
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha)
	{
		if(width<1 || height<1) { return; }
		if(!processAlpha)
		{
			for (int i=offset; i<rgbData.length; i++) { rgbData[i] &= 0x00FFFFFF; rgbData[i] |= 0xFF000000; }
		}
		else
		{	// Fix Alpha //
			for (int i=offset; i<rgbData.length; i++) { rgbData[i] |= 0x00000000; rgbData[i] &= 0xFFFFFFFF; }
		}
		// Copy from new image.  This avoids some problems with games that don't
		// properly adapt to different display sizes.
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		temp.setRGB(0, 0, width, height, rgbData, offset, scanlength);
		gc.drawImage(temp, x, y, null);
	}


	public void drawLine(int x1, int y1, int x2, int y2)
	{
		gc.drawLine(x1, y1, x2, y2);
	}

	public void drawRect(int x, int y, int width, int height)
	{
		gc.drawRect(x, y, width, height);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		gc.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	public void drawString(String str, int x, int y, int anchor)
	{
		if(str!=null)
		{
			x = AnchorX(x, gc.getFontMetrics().stringWidth(str), anchor);
			y = y + gc.getFontMetrics().getAscent() - 1;
			y = AnchorY(y, gc.getFontMetrics().getHeight(), anchor);
			gc.drawString(str, x, y);
		}
	}

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor)
	{
		if (str.length() >= offset + len)
		{
			drawString(str.substring(offset, offset+len), x, y, anchor);
		}
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle)
	{
		gc.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillRect(int x, int y, int width, int height)
	{
		gc.fillRect(x, y, width, height);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight)
	{
		gc.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	//public int getBlueComponent() { }
	//public Font getFont() { return font; }
	//public int getColor() { return color; }
	//public int getGrayScale() { }
	//public int getGreenComponent() { }
	//public int getRedComponent() { }
	//public int getStrokeStyle() { return strokeStyle; }

	public void setColor(int rgb)
	{
		setColor((rgb>>16) & 0xFF, (rgb>>8) & 0xFF, rgb & 0xFF);
	}

	public void setColor(int r, int g, int b)
	{
		color = (r<<16) + (g<<8) + b;
		awtColor = new Color(r, g, b);
		gc.setColor(awtColor);
	}

	public void setFont(Font font)
	{
		super.setFont(font);
		gc.setFont(font.platformFont.awtFont);
	}
	//public void setGrayScale(int value)
	//public void setStrokeStyle(int style)

	public void setClip(int x, int y, int width, int height)
	{
		gc.setClip(x, y, width, height);
		clipX = (int)gc.getClipBounds().getX();
		clipY = (int)gc.getClipBounds().getY();
		clipWidth = (int)gc.getClipBounds().getWidth();
		clipHeight = (int)gc.getClipBounds().getHeight();
	}

	public void clipRect(int x, int y, int width, int height)
	{
		gc.clipRect(x, y, width, height);
		clipX = (int)gc.getClipBounds().getX();
		clipY = (int)gc.getClipBounds().getY();
		clipWidth = (int)gc.getClipBounds().getWidth();
		clipHeight = (int)gc.getClipBounds().getHeight();
	}

	//public int getTranslateX() { }
	//public int getTranslateY() { }

	public void translate(int x, int y)
	{
		translateX += x;
		translateY += y;
		gc.translate((double)x, (double)y);
		//translateX = (int)gc.getTransform().getTranslateX();
		//translateY = (int)gc.getTransform().getTranslateY();
	}

	private int AnchorX(int x, int width, int anchor)
	{
		int xout = x;
		if((anchor & HCENTER)>0) { xout = x-(width/2); }
		if((anchor & RIGHT)>0) { xout = x-width; }
		if((anchor & LEFT)>0) { xout = x; }
		return xout;
	}

	private int AnchorY(int y, int height, int anchor)
	{
		int yout = y;
		if((anchor & VCENTER)>0) { yout = y-(height/2); }
		if((anchor & TOP)>0) { yout = y; }
		if((anchor & BOTTOM)>0) { yout = y-height; }
		if((anchor & BASELINE)>0) { yout = y+height; }
		return yout;
	}

	public void setAlphaRGB(int ARGB)
	{
		gc.setColor(new Color(ARGB, true));
	}

	/*
		****************************
			Nokia Direct Graphics
		****************************
	*/
	// http://www.j2megame.org/j2meapi/Nokia_UI_API_1_1/com/nokia/mid/ui/DirectGraphics.html

	private int colorAlpha;

	public int getNativePixelFormat() { return DirectGraphics.TYPE_INT_8888_ARGB; }

	public int getAlphaComponent() { return colorAlpha; }

	public void setARGBColor(int argbColor)
	{
		colorAlpha = (argbColor>>>24) & 0xFF;
		setColor(argbColor);
	}

	public void drawImage(javax.microedition.lcdui.Image img, int x, int y, int anchor, int manipulation)
	{
		//System.out.println("Nokia drawImage");
		BufferedImage image = manipulateImage(img.platformImage.getCanvas(), manipulation);
		x = AnchorX(x, image.getWidth(), anchor);
		y = AnchorY(y, image.getHeight(), anchor);
		drawImage(image, x, y);
	}

	public void drawPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
	{
		//System.out.println("drawPixels A"); // Found In Use
		
		int[] Type1 = {0xFFFFFFFF, 0xFF000000, 0x00FFFFFF, 0x00000000};
		int c = 0;
		int[] data;
		BufferedImage temp;
		switch(format)
		{
			case -1: // TYPE_BYTE_1_GRAY_VERTICAL // used by Monkiki's Castles
				data = new int[pixels.length*8];
				int row = 0;
				int col = 0;
				for(int b = (offset/8); b<pixels.length; b++)
				{
					for(int j=0; j<8; j++)
					{
						c = ((pixels[b]>>j)&1);
						if(transparencyMask!=null) { c |= (((transparencyMask[b]>>j)&1)^1)<<1; }
						data[((row+j)*width)+col] = Type1[c];
					}
					col++;
					if(col==width) { col=0; row+=8; }
				}

				temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				temp.setRGB(0, 0, width, height, data, 0, scanlength);
				drawImage(manipulateImage(temp, manipulation), x, y);
			break;

			case 1: // TYPE_BYTE_1_GRAY // used by Monkiki's Castles
				data = new int[pixels.length*8];

				for(int i=(offset/8); i<pixels.length; i++)
				{
					for(int j=7; j>=0; j--)
					{
						c = ((pixels[i]>>j)&1);
						if(transparencyMask!=null) { c |= (((transparencyMask[i]>>j)&1)^1)<<1; }
						data[(i*8)+(7-j)] = Type1[c];
					}
				}
				temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				temp.setRGB(0, 0, width, height, data, 0, scanlength);
				drawImage(manipulateImage(temp, manipulation), x, y);
			break;

			default: System.out.println("drawPixels A : Format " + format + " Not Implemented");
		}
	}

	public void drawPixels(int[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
	{
		//System.out.println("drawPixels B"); // Found In Use
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		temp.setRGB(0, 0, width, height, pixels, offset, scanlength);
		BufferedImage temp2 = manipulateImage(temp, manipulation);
		drawImage(temp2, x, y);
	}

	public void drawPixels(short[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
	{
		//System.out.println("drawPixels C"+format); // Found In Use
		int[] data = new int[pixels.length];

		for(int i=0; i<pixels.length; i++)
		{
			data[i] = pixelToColor(pixels[i], format);
		}

		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		temp.setRGB(0, 0, width, height, data, offset, scanlength);
		drawImage(manipulateImage(temp, manipulation), x, y);
	}

	public void drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)
	{
		int temp = color;
		setColor(argbColor);
		for (int i=0; i<nPoints; i++)
		{
			xPoints[i] += xOffset;
			yPoints[i] += yOffset;
		}
		gc.drawPolygon(xPoints, yPoints, nPoints);
		setColor(temp);
	}

	public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)
	{
		//System.out.println("drawTriange");
		int temp = color;
		setColor(argbColor);
		gc.drawPolygon(new int[]{x1,x2,x3}, new int[]{y1,y2,y3}, 3);
		setColor(temp);
	}

	public void fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)
	{
		int temp = color;
		setColor(argbColor);
		for (int i=0; i<nPoints; i++)
		{
			xPoints[i] += xOffset;
			yPoints[i] += yOffset;
		}
		gc.fillPolygon(xPoints, yPoints, nPoints);
		setColor(temp);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)
	{
		//System.out.println("fillTriangle"); // Found In Use
		int temp = color;
		setColor(argbColor);
		gc.fillPolygon(new int[]{x1,x2,x3}, new int[]{y1,y2,y3}, 3);
		setColor(temp);
	}

	public void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int format)
	{
		System.out.println("getPixels A");
	}

	public void getPixels(int[] pixels, int offset, int scanlength, int x, int y, int width, int height, int format)
	{
		//System.out.println("getPixels B");
		canvas.getRGB(x, y, width, height, pixels, offset, scanlength);
	}

	public void getPixels(short[] pixels, int offset, int scanlength, int x, int y, int width, int height, int format)
	{
		//System.out.println("getPixels C"); // Found In Use
		int i = offset;
		for(int row=0; row<height; row++)
		{
			for (int col=0; col<width; col++)
			{
				pixels[i] = colorToShortPixel(canvas.getRGB(col+x, row+y), format);
				i++;
			}
		}
	}

	private int pixelToColor(short c, int format)
	{
		int a = 0xFF;
		int r = 0;
		int g = 0;
		int b = 0;
		switch(format)
		{
			case DirectGraphics.TYPE_USHORT_1555_ARGB:
				a = ((c>>15) & 0x01)*0xFF;
				r = (c>>10) & 0x1F; g = (c>>5) & 0x1F; b = c & 0x1F;
				r = (r<<3)|(r>>2); g = (g<<3)|(g>>2); b = (b<<3)|(b>>2);
				break;
			case DirectGraphics.TYPE_USHORT_444_RGB:
				r = (c>>8) & 0xF; g = (c>>4) & 0xF; b = c & 0xF;
				r = (r<<4)|r; g = (g<<4)|g; b = (b<<4)|b;
				break;
			case DirectGraphics.TYPE_USHORT_4444_ARGB:
				a = (c>>12) & 0xF; r = (c>>8) & 0xF; g = (c>>4) & 0xF; b = c & 0xF;
				a = (a<<4)|a; r = (r<<4)|r; g = (g<<4)|g; b = (b<<4)|b;
				break;
			case DirectGraphics.TYPE_USHORT_555_RGB:
				r = (c>>10) & 0x1F; g = (c>>5) & 0x1F; b = c & 0x1F;
				r = (r<<3)|(r>>2); g = (g<<3)|(g>>2); b = (b<<3)|(b>>2);
				break;
			case DirectGraphics.TYPE_USHORT_565_RGB:
				r = (c>>11) & 0x1F; g = (c>>5) & 0x3F; b = c & 0x1F;
				r = (r<<3)|(r>>2); g = (g<<2)|(g>>4); b = (b<<3)|(b>>2);
				break;
		}
		return (a<<24) | (r<<16) | (g<<8) | b;
	}

	private short colorToShortPixel(int c, int format)
	{
		int a = 0;
		int r = 0;
		int g = 0;
		int b = 0;
		int out = 0;
		switch(format)
		{
			case DirectGraphics.TYPE_USHORT_1555_ARGB:
				a=c>>>31; r=((c>>19)&0x1F); g=((c>>11)&0x1F); b=((c>>3)&0x1F);
				out=(a<<15)|(r<<10)|(g<<5)|b;
				break;
			case DirectGraphics.TYPE_USHORT_444_RGB:
				r=((c>>20)&0xF); g=((c>>12)&0xF); b=((c>>4)&0xF);
				out=(r<<8)|(g<<4)|b;
				break;
			case DirectGraphics.TYPE_USHORT_4444_ARGB:
				a=((c>>>28)&0xF); r=((c>>20)&0xF); g=((c>>12)&0xF); b=((c>>4)&0xF);
				out=(a<<12)|(r<<8)|(g<<4)|b;
				break;
			case DirectGraphics.TYPE_USHORT_555_RGB:
				r=((c>>19)&0x1F); g=((c>>11)&0x1F); b=((c>>3)&0x1F);
				out=(r<<10)|(g<<5)|b;
				break;
			case DirectGraphics.TYPE_USHORT_565_RGB:
				r=((c>>19)&0x1F); g=((c>>10)&0x3F); b=((c>>3)&0x1F);
				out=(r<<11)|(g<<5)|b;
				break;
		}
		return (short)out;
	}

	private BufferedImage manipulateImage(BufferedImage image, int manipulation)
	{
		final int HV = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.FLIP_VERTICAL;
		final int H90 = DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_90;
		switch(manipulation)
		{
			case DirectGraphics.FLIP_HORIZONTAL:
				return PlatformImage.transformImage(image, Sprite.TRANS_MIRROR);
			case DirectGraphics.FLIP_VERTICAL: 
				return PlatformImage.transformImage(image, Sprite.TRANS_MIRROR_ROT180);
			case DirectGraphics.ROTATE_90: 
				return PlatformImage.transformImage(image, Sprite.TRANS_ROT90);
			case DirectGraphics.ROTATE_180:
				return PlatformImage.transformImage(image, Sprite.TRANS_ROT180);
			case DirectGraphics.ROTATE_270:
				return PlatformImage.transformImage(image, Sprite.TRANS_ROT270);
			case HV: 
				return PlatformImage.transformImage(image, Sprite.TRANS_ROT180);
			case H90: 
				return PlatformImage.transformImage(PlatformImage.transformImage(image, Sprite.TRANS_MIRROR), Sprite.TRANS_ROT270);
		}
		return image;
	}

}
