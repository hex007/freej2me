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

public abstract class DiscoveryAgent
{
    
    public static final int CACHED = 0;

    public static final int GIAC = 10390323;

    public static final int LIAC = 10390272;

    public static final int NOT_DISCOVERABLE = 0;
    
    public static final int PREKNOWN = 1;

    public abstract boolean cancelInquiry(DiscoveryListener listener);

    public abstract boolean cancelServiceSearch(int transID);

    public abstract RemoteDevice[] retrieveDevices(int option);

    public abstract int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener discListener);

    public abstract String selectService(UUID uuid, int security, boolean master);

    public abstract boolean startInquiry(int accessCode, DiscoveryListener listener);
}
