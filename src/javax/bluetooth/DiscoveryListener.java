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

public interface DiscoveryListener
{

    static final int INQUIRY_COMPLETED = 0x00;

    static final int INQUIRY_ERROR = 0x07;

    static final int INQUIRY_TERMINATED = 0x05;

    static final int SERVICE_SEARCH_COMPLETED = 0x01;

    static final int SERVICE_SEARCH_DEVICE_NOT_REACHABLE  = 0x06;

    static final int SERVICE_SEARCH_ERROR = 0x03;

    static final int SERVICE_SEARCH_NO_RECORDS = 0x04;

    static final int SERVICE_SEARCH_TERMINATED = 0x02;

    void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

    void inquiryCompleted(int discType);

    void servicesDiscovered(int transID, ServiceRecord[] servRecord);

    void serviceSearchCompleted(int transID, int respCode);
    
}
