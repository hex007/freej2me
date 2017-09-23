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
package javax.microedition.sensor;

public interface SensorInfo
{

	public static final int CONN_EMBEDDED = 1;
	public static final int CONN_REMOTE = 2;
	public static final int CONN_SHORT_RANGE_WIRELESS = 4;
	public static final int CONN_WIRED = 8;
	public static final String CONTEXT_TYPE_AMBIENT = "ambient";
	public static final String CONTEXT_TYPE_DEVICE = "device";
	public static final String CONTEXT_TYPE_USER = "user";
	public static final String PROP_LATITUDE = "latitude";
	public static final String PROP_LOCATION = "location";
	public static final String PROP_LONGITUDE = "longitude";
	public static final String PROP_MAX_RATE = "maxSamplingRate";
	public static final String PROP_VENDOR = "vendor";
	public static final String PROP_VERSION = "version";


	public ChannelInfo[] getChannelInfos();

	public int getConnectionType();

	public String getContextType();

	public String getDescription();

	public int getMaxBufferSize();

	public String getModel();

	public Object getProperty(String name);

	public String[] getPropertyNames();

	public String getQuantity();

	public String getUrl();

	public boolean isAvailabilityPushSupported();

	public boolean isAvailable();

	public boolean isConditionPushSupported();

}
