/**
 *  Java docs licensed under the Apache License, Version 2.0
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *   (c) Copyright 2001, 2002 Motorola, Inc.  ALL RIGHTS RESERVED.
 *
 *
 *  @version $Id$
*/ 

/*
    Apache 2.0 can be embedded into GPLv3 according to Apache:
    https://www.apache.org/licenses/GPL-compatibility.html

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


public interface DiscoveryListener {

	public static final int INQUIRY_COMPLETED = 0x00;

	public static final int INQUIRY_TERMINATED = 0x05;

	public static final int INQUIRY_ERROR = 0x07;

	public static final int SERVICE_SEARCH_COMPLETED = 0x01;

	public static final int SERVICE_SEARCH_TERMINATED = 0x02;

	public static final int SERVICE_SEARCH_ERROR = 0x03;

	public static final int SERVICE_SEARCH_NO_RECORDS = 0x04;

	public static final int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 0x06;

	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

	public void servicesDiscovered(int transID, ServiceRecord[] servRecord);

	public void serviceSearchCompleted(int transID, int respCode);

	public void inquiryCompleted(int discType);
}
