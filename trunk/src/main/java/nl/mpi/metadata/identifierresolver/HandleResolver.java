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
package nl.mpi.metadata.identifierresolver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import nl.mpi.metadata.api.model.MetadataDocument;

/**
 * Resolver for URI's with scheme 'hdl'; resolving method will rewrite the URI to a URL on the handle proxy server
 *
 * @see http://hdl.handle.net
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HandleResolver implements IdentifierResolver {

    public final static String HANDLE_PROXY_SERVER = "hdl.handle.net";

    /**
     *
     * @param document will be ignored
     * @param identifier identifier to check
     * @return true only if the scheme equals (case insensitive) 'hdl'
     */
    @Override
    public boolean canResolve(MetadataDocument document, URI identifier) {
	return identifier.isAbsolute() && identifier.getScheme().equalsIgnoreCase("hdl");
    }

    /**
     *
     * @param document will be ignored
     * @param identifier identifier to resolve
     * @return URI rewritten to URL on {@link http://hdl.handle.net handle proxy server}
     * @throws IdentifierResolutionException if the provided URI does not have scheme 'hdl'
     */
    @Override
    public URL resolveIdentifier(MetadataDocument document, URI identifier) throws IdentifierResolutionException {
	if ("hdl".equalsIgnoreCase(identifier.getScheme())) {
	    try {
		return new URL("http", HANDLE_PROXY_SERVER, "/" + identifier.getSchemeSpecificPart());
	    } catch (MalformedURLException ex) {
		throw new IdentifierResolutionException("Could not resolve handle", ex);
	    }
	} else {
	    throw new IdentifierResolutionException("Cannot resolve handle with scheme other than 'hdl': " + identifier.getScheme());
	}
    }
}
