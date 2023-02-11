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
package javax.microedition.pim;

public class PIMException extends Exception 
{

	public static final int FEATURE_NOT_SUPPORTED = 0;
	public static final int GENERAL_ERROR = 1;
	public static final int LIST_CLOSED = 2;
	public static final int LIST_NOT_ACCESSIBLE = 3;
	public static final int MAX_CATEGORIES_EXCEEDED = 4;
	public static final int UNSUPPORTED_VERSION = 5;
	public static final int UPDATE_ERROR = 6;
	private final int reason;

	public PIMException() { reason = GENERAL_ERROR; }

	public PIMException(String detailMessage) 
	{
		super(detailMessage);
		reason = GENERAL_ERROR;
	}

	public PIMException(String detailMessage, int reason) 
	{
		super(detailMessage);
		this.reason = reason;
	}

	public int getReason() { return reason; }
}
