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
package javax.microedition.lcdui;

import org.recompile.mobile.Mobile;
import org.recompile.mobile.PlatformFont;

public final class Font
{
	public static final int FACE_MONOSPACE = 32;
	public static final int FACE_PROPORTIONAL = 64;
	public static final int FACE_SYSTEM = 0;

	public static final int FONT_INPUT_TEXT = 1;
	public static final int FONT_STATIC_TEXT = 0;

	public static final int SIZE_LARGE = 16;
	public static final int SIZE_MEDIUM = 0;
	public static final int SIZE_SMALL = 8;

	public static final int STYLE_BOLD = 1;
	public static final int STYLE_ITALIC = 2;
	public static final int STYLE_PLAIN = 0;
	public static final int STYLE_UNDERLINED = 4;


	private int face;
	private int style;
	private int size;

	private static Font defaultFont = new Font(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

	public PlatformFont platformFont;

	private Font(int fontFace, int fontStyle, int fontSize)
	{
		face = fontFace;
		style = fontStyle;
		size = fontSize;
		platformFont = new PlatformFont(this);
	}

	public int charsWidth(char[] ch, int offset, int length)
	{
		int len = 0;
		for(int i=offset; i<ch.length+length; i++)
		{
			if(i<ch.length) { len += charWidth(ch[i]); }
		}
		return len;
	}

	public int charWidth(char ch) { return stringWidth(String.valueOf(ch)); }

	public int getBaselinePosition() { return convertSize(size); }

	public static Font getDefaultFont() { return defaultFont; }

	public int getFace() { return face; }

	public static Font getFont(int fontSpecifier) { return defaultFont; }

	public static Font getFont(int face, int style, int size) { return new Font(face, style, size); }

	public int getHeight() { return convertSize(size); }

	public int getSize() { return size; }

	public int getPointSize() { return convertSize(size); }

	public int getStyle() { return style; }

	public boolean isBold() { return (style & STYLE_BOLD) == STYLE_BOLD; }

	public boolean isItalic() { return (style & STYLE_ITALIC) == STYLE_ITALIC; }

	public boolean isPlain() { return style == STYLE_PLAIN; }

	public boolean isUnderlined() { return (style & STYLE_UNDERLINED) == STYLE_UNDERLINED; }

	public int stringWidth(String str)
	{
		return platformFont.stringWidth(str);
	}

	public int substringWidth(String str, int offset, int len)
	{
		return stringWidth(str.substring(offset, offset+len));
	}

	private int convertSize(int size)
	{
		switch(size)
		{
			case SIZE_LARGE  : return 14;
			case SIZE_MEDIUM : return 12;
			case SIZE_SMALL  : return 10;
			default          : return 10;
		}
	}
}
