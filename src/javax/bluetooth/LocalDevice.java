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

import javax.microedition.io.Connection;


public abstract class LocalDevice 
{
    public abstract String getBluetoothAddress();

    public abstract DeviceClass getDeviceClass();

    public abstract int getDiscoverable();

    public abstract DiscoveryAgent getDiscoveryAgent();

    public abstract String getFriendlyName();

    public abstract LocalDevice getLocalDevice();

    public abstract String getProperty(String property);

    public abstract ServiceRecord getRecord(Connection notifier);

    public static boolean isPowerOn() { return false; /* We won't have functional Bluetooth yet */ }

    public abstract boolean setDiscoverable(int mode);    

    public abstract void updateRecord(ServiceRecord srvRecord);
}
