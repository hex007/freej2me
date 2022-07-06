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

import java.util.Enumeration;

public interface EventList extends PIMList 
{
	
	public static final int STARTING = 0;
	public static final int ENDING = 1;
	public static final int OCCURRING = 2;

	public Event createEvent();

	public Event importEvent(Event item) throws NullPointerException;

	public void removeEvent(Event item) throws NullPointerException, SecurityException, PIMException;

	public Enumeration items(int searchType, long startDate, long endDate, boolean initialEventOnly)
	    throws IllegalArgumentException, SecurityException, PIMException;

	public int[] getSupportedRepeatRuleFields(int frequency) throws IllegalArgumentException;
}
