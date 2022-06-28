 
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
package javax.bluetooth;

public class UUID
{
	
	private final long baseUUID1 = 0x0000_0000L;
	
	private final long baseUUID2 = 0x0000_1000L; 
	
	private final long baseUUID3 = 0x8000_0080L; 
	
	private final long baseUUID4 = 0x5F9B_34FBL;

	private final long UUID1, UUID2, UUID3, UUID4;

	public UUID(long uuidValue) throws IllegalArgumentException
	{
		if (uuidValue < 0L || uuidValue > 0xffffffffL) { throw new IllegalArgumentException("Invalid UUID Value, must be between 0 an (2^32)-1"); }
		else
		{
			UUID1 = baseUUID1;
			UUID2 = baseUUID2;
			UUID3 = baseUUID3;
			UUID4 = uuidValue;
		}
	}

	public UUID(String uuidValue, boolean shortUUID) throws IllegalArgumentException, NullPointerException, NumberFormatException
	{ 
		if (uuidValue == null) { throw new NullPointerException("Received a NULL UUID string."); }

		if (uuidValue.length() == 0) { throw new IllegalArgumentException("Received an empty UUID string."); }
		
		if (shortUUID)
		{
			if(uuidValue.length() > 8) { throw new IllegalArgumentException("UUID string is too long for a shortUUID"); }
			else
			{
				String formattedUuidValue = String.format("%08d", uuidValue);
				UUID1 = baseUUID1;
				UUID2 = baseUUID2;
				UUID3 = baseUUID3;
				UUID4 = Long.parseUnsignedLong(formattedUuidValue, 16);
			}
		}
		else
		{
			if(uuidValue.length() > 32) { throw new IllegalArgumentException("UUID value is too long"); }
			else
			{
				String formattedUuidValue = String.format("%032d", uuidValue);
				UUID1 = Long.parseUnsignedLong(formattedUuidValue.substring(0, 8), 16);
				UUID2 = Long.parseUnsignedLong(formattedUuidValue.substring(8, 16), 16);
				UUID3 = Long.parseUnsignedLong(formattedUuidValue.substring(16, 24), 16);
				UUID4 = Long.parseUnsignedLong(formattedUuidValue.substring(24, 32), 16);
			}
		}
	}

	@Override
	public boolean equals(Object value) 
	{
		if (value == null || (value instanceof UUID) == false) { return false; }
		
		return this.equals(value);
	}

	@Override
	public int hashCode() { return this.hashCode(); }

	@Override
	public String toString()
	{
		String uuidString = String.format("%08X", UUID1) + String.format("%08X", UUID2) + String.format("%08X", UUID3) + String.format("%08X", UUID4);
		return uuidString.replaceFirst("^0+", "");
	}

}
