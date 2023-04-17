/*
 *
 * Copyright  1990-2008 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 *
 */



package javax.microedition.io;

import java.io.IOException;

/**
 * A parsed HTTP (or subclass of) URL. Based on RFC 2396.
 * <p>
 * Handles IPv6 hosts, check host[0] for a "[".
 * Can be used for relative URL's that do not have authorities.
 * Can be used for FTP URL's that do not have the username and passwords.
 * <p>
 * Any elements not specified are represented by null, except a
 * non-specified port, which is represented by a -1.
 */
public class HttpUrl {
    /** Scheme of the URL or null. */
    public String scheme;
    /** Authority (host [port]) of the URL. */
    public String authority;
    /** Path of the URL or null. */
    public String path;
    /** Query of the URL or null. */
    public String query;
    /** Fragment of the URL or null. */
    public String fragment;
    /** hHst of the authority or null. */
    public String host;
    /** Port of the authority or -1 for not specified. */
    public int port = -1;
    /** Machine of the host or null. */
    public String machine;
    /** Domain of the host or null. */
    public String domain;

    /**
     * Construct a HttpUrl.
     *
     * @param url HTTP URL to parse
     *
     * @exception IllegalArgumentException if there is a space in the URL or
     *             the port is not numeric
     */
    public HttpUrl(String url) {
        int afterScheme = 0;
        int length;
        int endOfScheme;

        if (url == null) {
            return;
        }

        length = url.length();
        if (length == 0) {
            return;
        }

        // ":" can mark a the scheme in a absolute URL which has a "//".
        endOfScheme = url.indexOf(':');
        if (endOfScheme != -1) {
            if (endOfScheme == length - 1) {
                // just a scheme
                scheme = url.substring(0, endOfScheme);
                return;
            }

            if (endOfScheme < length - 2 &&
                    url.charAt(endOfScheme + 1) == '/' &&
                    url.charAt(endOfScheme + 2) == '/') {
                // found "://", get the scheme
                scheme = url.substring(0, endOfScheme);
                afterScheme = endOfScheme + 1;
            }
        }

        parseAfterScheme(url, afterScheme, length);
    }

    /**
     * Construct a HttpUrl from a scheme and partial HTTP URL.
     *
     * @param theScheme  the protocol component of an HTTP URL
     * @param partialUrl HTTP URL to parse
     *
     * @exception IllegalArgumentException if there is a space in the URL or
     *             the port is not numeric
     */
    public HttpUrl(String theScheme, String partialUrl) {
        int length;

        scheme = theScheme;

        if (partialUrl == null) {
            return;
        }

        length = partialUrl.length();
        if (length == 0) {
            return;
        }

        parseAfterScheme(partialUrl, 0, length);
    }

    /**
     * Parse the part of the HTTP URL after the scheme.
     *
     * @param url the part of the HTTP URL after the ":" of the scheme
     * @param afterScheme index of the first char after the scheme
     * @param length length of the url
     *
     * @exception IllegalArgumentException if there is a space in the URL or
     *             the port is not numeric
     */
    private void parseAfterScheme(String url, int afterScheme, int length) {
        int start;
        int startOfAuthority;
        int endOfUrl;
        int endOfAuthority;
        int endOfPath;
        int endOfQuery;
        int endOfHost;
        int startOfPort;
        int endOfPort;
        int startOfDomain;

        if (url.indexOf(' ') != -1) {
            throw new IllegalArgumentException("Space character in URL");
        }

        endOfUrl = length;
        endOfAuthority = endOfUrl;
        endOfPath = endOfUrl;
        endOfQuery = endOfUrl;

        if (url.startsWith("//", afterScheme)) {
            // do not include the "//"
            startOfAuthority = afterScheme + 2;
        } else {
            // no authority, the path starts at 0 and may not begin with a "/"
            startOfAuthority = afterScheme;
        }

        /*
         * Since all of the elements after the authority are optional
         * and they can contain the delimiter of the element before it.
         * Work backwards since we know the end of the last item and will
         * know the end of the next item when find the start of the current
         * item.
         */
        start = url.indexOf('#', startOfAuthority);
        if (start != -1) {
            endOfAuthority = start;
            endOfPath = start;
            endOfQuery = start;

            // do not include the "#"
            start++;

            // do not parse an empty fragment
            if (start < endOfUrl) {
                fragment = url.substring(start, endOfUrl);
            }
        }

        start = url.indexOf('?', startOfAuthority);
        if (start != -1 && start < endOfQuery) {
            endOfAuthority = start;
            endOfPath = start;

            // do not include the "?"
            start++;

            // do not parse an empty query
            if (start < endOfQuery) {
                query = url.substring(start, endOfQuery);
            }
        }

        if (startOfAuthority == afterScheme) {
            // no authority, the path starts after scheme
            start = afterScheme;
        } else {
            // this is not relative URL so the path must begin with "/"
            start = url.indexOf('/', startOfAuthority);
        }

        // do not parse an empty path
        if (start != -1 && start < endOfPath) {
            endOfAuthority = start;

            path = url.substring(start, endOfPath);
        }

        if (startOfAuthority >= endOfAuthority) {
            return;
        }

        authority = url.substring(startOfAuthority, endOfAuthority);
        endOfPort = authority.length();

        // get the port first, to find the end of the host
        // IPv6 address have brackets around them and can have ":"'s
        start = authority.indexOf(']');
        if (start == -1) {
            startOfPort = authority.indexOf(':');
        } else {
            startOfPort = authority.indexOf(':', start);
        }

        if (startOfPort != -1) {
            endOfHost = startOfPort;

            // do not include the ":"
            startOfPort++;

            // do not try parse an empty port
            if (startOfPort < endOfPort) {
                try {
                    port = Integer.parseInt(authority.substring(
                                            startOfPort,
                                            endOfPort));

                    if (port <= 0) {
                        throw new
                            IllegalArgumentException("invalid port format");
                    }

                    if (port == 0 || port > 0xFFFF) {
                        throw new IllegalArgumentException(
                            "port out of legal range");
                    }
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("invalid port format");
                }
            }
        } else {
            endOfHost = endOfPort;
        }

        // there could be a port but no host
        if (endOfHost < 1) {
            return;
        }

        // get the host
        host = authority.substring(0, endOfHost);
        // find the machine and domain, if not host is not an IP address
        if (Character.isDigit(host.charAt(0)) || host.charAt(0) == '[') {
            return;
        }

        startOfDomain = host.indexOf('.');
        if (startOfDomain != -1) {
            // do not include the "."
            domain = host.substring(startOfDomain + 1, host.length());
            machine = host.substring(0, startOfDomain);
        } else {
            machine = host;
        }
    }

    /**
     * Adds a base URL to this URL if this URL is a relative one.
     * Afterwards this URL will be an absolute URL.
     *
     * @param baseUrl an absolute URL
     *
     * @exception IllegalArgumentException if there is a space in the URL or
     *             the port is not numeric
     * @exception IOException if an I/O error occurs processing the URL
     */
    public void addBaseUrl(String baseUrl) throws IOException {
        addBaseUrl(new HttpUrl(baseUrl));
    }

    /**
     * Adds a base URL to this URL if this URL is a relative one.
     * Afterwards this URL will be an absolute URL.
     *
     * @param baseUrl a parsed absolute URL
     */
    public void addBaseUrl(HttpUrl baseUrl) {
        String basePath;

        if (authority != null) {
            return;
        }

        scheme = baseUrl.scheme;
        authority = baseUrl.authority;

        if (path == null) {
            path = baseUrl.path;
            return;
        }

        if (path.charAt(0) == '/' || baseUrl.path == null ||
               baseUrl.path.charAt(0) != '/') {
            return;
        }

        // find the base path
        basePath = baseUrl.path.substring(0, baseUrl.path.lastIndexOf('/'));

        path = basePath + '/' + path;
    }

    /**
     * Converts this URL into a string.
     *
     * @return string representation of this URL
     */
    public String toString() {
        StringBuffer url = new StringBuffer();

        if (scheme != null) {
            url.append(scheme);
            url.append(':');
        }

        if (authority != null) {
            url.append('/');
            url.append('/');
            url.append(authority);
        }

        if (path != null) {
            url.append(path);
        }

        if (query != null) {
            url.append('?');
            url.append(query);
        }

        if (fragment != null) {
            url.append('#');
            url.append(fragment);
        }

        return url.toString();
    }
}
