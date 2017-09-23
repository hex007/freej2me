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
package javax.microedition.io;


public interface HttpConnection extends ContentConnection
{

	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final int HTTP_ACCEPTED = 202;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_BAD_METHOD = 405;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_CLIENT_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_ENTITY_TOO_LARGE = 413;
	public static final int HTTP_EXPECT_FAILED = 417;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_GONE = 410;
	public static final int HTTP_INTERNAL_ERROR = 500;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_MOVED_TEMP = 302;
	public static final int HTTP_MULT_CHOICE = 300;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_NOT_ACCEPTABLE = 406;
	public static final int HTTP_NOT_AUTHORITATIVE = 203;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int HTTP_NOT_IMPLEMENTED = 501;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_OK = 200;
	public static final int HTTP_PARTIAL = 206;
	public static final int HTTP_PAYMENT_REQUIRED = 402;
	public static final int HTTP_PRECON_FAILED = 412;
	public static final int HTTP_PROXY_AUTH = 407;
	public static final int HTTP_REQ_TOO_LONG = 414;
	public static final int HTTP_RESET = 205;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_TEMP_REDIRECT = 307;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_UNAVAILABLE = 503;
	public static final int HTTP_UNSUPPORTED_RANGE = 416;
	public static final int HTTP_UNSUPPORTED_TYPE = 415;
	public static final int HTTP_USE_PROXY = 305;
	public static final int HTTP_VERSION = 505;
	public static final String POST = "POST";


	long getDate();

	public long getExpiration();

	public String getFile();

	public String getHeaderField(int n);

	public String getHeaderField(String name);

	public long getHeaderFieldDate(String name, long def);

	public int getHeaderFieldInt(String name, int def);

	public String getHeaderFieldKey(int n);

	public String getHost();

	public long getLastModified();

	public int getPort();

	public String getProtocol();

	public String getQuery();

	public String getRef();

	public String getRequestMethod();

	public String getRequestProperty(String key);

	public int getResponseCode();

	public String getResponseMessage();

	public String getURL();

	public void setRequestMethod(String method);

	public void setRequestProperty(String key, String value);

}
