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

import java.util.Hashtable;

import javax.obex.SessionNotifier;

public class LocalDevice extends Object
{

    private static LocalDevice locDevc;

    private static DiscoveryAgent disAgnt;

    private static DeviceClass devcCls;

    private static ServiceRecord srvcRcd;

    private static Hashtable<String, String> properties = new Hashtable<String, String>();

    static {
		properties.put("bluetooth.api.version", "0.0");
		properties.put("bluetooth.master.switch", "false");
		properties.put("bluetooth.sd.attr.retrievable.max", "0");
		properties.put("bluetooth.connected.devices.max", "0");
		properties.put("bluetooth.l2cap.receiveMTU.max", "0");
		properties.put("bluetooth.sd.trans.max", "0");
		properties.put("bluetooth.connected.inquiry.scan", "false");
		properties.put("bluetooth.connected.page.scan", "false");
		properties.put("bluetooth.connected.inquiry", "false");
		properties.put("bluetooth.connected.page", "false");
	}

    public static String getBluetoothAddress() throws BluetoothStateException { return "000000000000"; }

    public static DeviceClass getDeviceClass() { return devcCls; }

    public static int getDiscoverable() { return DiscoveryAgent.NOT_DISCOVERABLE; }

    public static DiscoveryAgent getDiscoveryAgent() { return disAgnt; }

    public static String getFriendlyName() { return "noDevice"; }

    public static LocalDevice getLocalDevice() throws BluetoothStateException { return locDevc; }

    public static String getProperty(String property) { return properties.get(property); }

    public static ServiceRecord getRecord(Connection notifier) throws IllegalArgumentException, 
    NullPointerException { return srvcRcd; }

    public static boolean isPowerOn() { return false; } /* We won't have functional Bluetooth yet */

    public static boolean setDiscoverable(int mode) throws IllegalArgumentException, 
    BluetoothStateException { return false; }    

    public static void updateRecord(ServiceRecord srvRecord) throws NullPointerException, 
    IllegalArgumentException, ServiceRegistrationException { }

}
