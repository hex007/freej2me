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

public abstract interface DiscoveryListener
{

    public static int INQUIRY_COMPLETED = 0;

    public static int INQUIRY_ERROR = 7;

    public static int INQUIRY_TERMINATED = 5;

    public static int SERVICE_SEARCH_COMPLETED = 1;

    public static int SERVICE_SEARCH_DEVICE_NOT_REACHABLE  = 6;

    public static int SERVICE_SEARCH_ERROR = 3;

    public static int SERVICE_SEARCH_NO_RECORDS = 4;

    public static int SERVICE_SEARCH_TERMINATED = 2;

    public abstract void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

    public abstract void inquiryCompleted(int discType);

    public abstract void servicesDiscovered(int transID, ServiceRecord[] servRecord);

    public abstract void serviceSearchCompleted(int transID, int respCode);
}
