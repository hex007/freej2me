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
	
	private final long[] baseUUID = { 0x0000_0000L, 0x0000_1000L, 0x8000_0080L, 0x5F9B_34FBL };

	private final long[] UUIDval  = { 0x0000_0000L, 0x0000_0000L, 0x0000_0000L, 0x0000_0000L };


	public UUID(long uuidValue) throws IllegalArgumentException
	{
		if (uuidValue < 0L || uuidValue > 0xffffffffL) { throw new IllegalArgumentException("Invalid UUID Value, must be between 0 an (2^32)-1"); }
		else
		{
			UUIDval[0] = uuidValue;
			UUIDval[1] = baseUUID[1];
			UUIDval[2] = baseUUID[2];
			UUIDval[3] = baseUUID[3];
		}
	}

	public UUID(String uuidValue, boolean shortUUID) throws IllegalArgumentException, NullPointerException, NumberFormatException
	{ 	
		int length = uuidValue.length();

		if(uuidValue == null || length==0 || length>32 || (shortUUID && length>8) )
		{
			throw new IllegalArgumentException("Received an invalid UUID String. Check its size and type.");
		}

		if (shortUUID)
		{
			String formattedUuidValue = String.format("%08X", uuidValue);
			UUIDval[0] = Long.parseUnsignedLong(formattedUuidValue, 16);
			UUIDval[1] = baseUUID[1];
			UUIDval[2] = baseUUID[2];
			UUIDval[3] = baseUUID[3];
		}
		else
		{
			String formattedUuidValue = String.format("%032X", uuidValue);
			UUIDval[0] = Long.parseUnsignedLong(formattedUuidValue.substring(0, 8), 16);
			UUIDval[1] = Long.parseUnsignedLong(formattedUuidValue.substring(8, 16), 16);
			UUIDval[2] = Long.parseUnsignedLong(formattedUuidValue.substring(16, 24), 16);
			UUIDval[3] = Long.parseUnsignedLong(formattedUuidValue.substring(24, 32), 16);
		}
	}

	@Override
	public boolean equals(Object value) 
	{
		if (value == null || (value instanceof UUID) == false) { return false; }

		return ((UUID)value).toString().equals(this.toString());
	}

	@Override
	public int hashCode() { return (int)(UUIDval[0] & 0xFFFFFFFF); }

	@Override
	public String toString()
	{
		String uuidString = String.format("%08X", UUIDval[0]) + String.format("%08X", UUIDval[1]) + String.format("%08X", UUIDval[2]) + String.format("%08X", UUIDval[3]);
		return uuidString.replaceFirst("^0+", "");
	}
}
