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

import javax.microedition.io.Connection;


public class LocalDevice {

	private LocalDevice() throws BluetoothStateException {
	}

	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		throw new BluetoothStateException("This is not JSR-82 implementation");
	}

	public DiscoveryAgent getDiscoveryAgent() {
		return null;
	}

	public String getFriendlyName() {
	    return null;
	}

	public DeviceClass getDeviceClass() {
	    return null;
	}

	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		if ((mode != DiscoveryAgent.GIAC) && (mode != DiscoveryAgent.LIAC) && (mode != DiscoveryAgent.NOT_DISCOVERABLE) 
				&& (mode < 0x9E8B00 || mode > 0x9E8B3F)) {
			throw new IllegalArgumentException("Invalid discoverable mode");
		}
		return false;
	}

	public static boolean isPowerOn() {
		return false;
	}

	public int getDiscoverable() {
		return DiscoveryAgent.NOT_DISCOVERABLE;
	}

	public static String getProperty(String property) {
		return null;
	}

	public String getBluetoothAddress() {
	    return null;
	}

	public ServiceRecord getRecord(Connection notifier) {
		if (notifier == null) {
			throw new NullPointerException("notifier is null");
		}

		return null;
	}

	public void updateRecord(ServiceRecord srvRecord) throws ServiceRegistrationException {
		if (srvRecord == null) {
			throw new NullPointerException("Service Record is null");
		}
	}
}
