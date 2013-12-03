/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.metadata.api.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HandleUtil {

    public final static Pattern HANDLE_PATTERN = Pattern.compile("^[^/]+/[^/]+$");
    public final static String HANDLE_PROXY_URL = "http://hdl.handle.net/";

    public boolean isHandleUri(URI handleUri) {
	return "hdl".equalsIgnoreCase(handleUri.getScheme())
		&& isHandle(handleUri.getSchemeSpecificPart());
    }

    public boolean isHandle(String handleString) {
	return HANDLE_PATTERN.matcher(handleString).matches();
    }

    /**
     *
     * @param handleUri handle string to create URI from
     * <ul>
     * <li>Valid handle URIs with the 'hdl' scheme will be returned unmodified as an URI object
     * <li>Valid URLs based on the {@link #HANDLE_PROXY_URL handle proxy URL} will be converted into a handle URI with scheme 'hdl', e.g.
     * http://hdl.handle.net/1234/5678 -> hdl:1234/5678
     * <li>Valid handles (without any prefix or proxy) will be wrapped into a URI with scheme 'hdl', e.g. hdl:1234/5678
     * <li>Null is accepted (will return null)</li>
     * </ul>
     * @return the handle URI that was created based on the parameter; if it could not be created, will return null. Client needs to check
     * for null value.
     */
    public URI createHandleUri(String handleUri) {
	if (handleUri != null) {
	    final String handleUriLowerCase = handleUri.toLowerCase();

	    // check valid handle URI
	    if (handleUriLowerCase.startsWith("hdl:")) {
		try {
		    URI uri = new URI(handleUri);
		    if (isHandleUri(uri)) {
			return uri;
		    }
		} catch (URISyntaxException ex) {
		    // Handle is not a valid URI
		    return null;
		}
	    }

	    // check handle proxy case	    
	    if (handleUriLowerCase.startsWith(HANDLE_PROXY_URL)) {
		// split off handle part and create handle URI for it
		return createHandleUri(handleUri.substring(HANDLE_PROXY_URL.length()));
	    }

	    // check plain handle case	    
	    if (isHandle(handleUri)) {
		try {
		    return new URI("hdl:" + handleUri);
		} catch (URISyntaxException ex) {
		    // Something in handle part violates URI syntax
		    return null;
		}
	    }
	}
	// no matching case, returns null
	return null;
    }
}
