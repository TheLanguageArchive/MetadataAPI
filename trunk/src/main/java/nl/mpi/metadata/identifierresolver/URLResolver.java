/*
 * Copyright (C) 2011 Max Planck Institute for Psycholinguistics
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
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class URLResolver implements IdentifierResolver {

    @Override
    public boolean canResolve(MetadataDocument document, URI identifier) {
	final String scheme = identifier.getScheme();
	if (identifier.isAbsolute()) {
	    return scheme.equalsIgnoreCase("http")
		    || scheme.equalsIgnoreCase("https")
		    || scheme.equalsIgnoreCase("file");
	} else {
	    if (document != null && document.getFileLocation().isAbsolute()) {
		try {
		    document.getFileLocation().toURL();
		    return true;
		} catch (MalformedURLException ex) {
		    return false;
		}
	    } else {
		return false;
	    }
	}
    }

    /**
     *
     * @param document a document with an absolute file location
     * @param identifier identifier to resolve on this document
     * @return the resolved location
     * @see MetadataDocument#getFileLocation()
     * @throws IdentifierResolutionException if the document's file location was not absolute
     */
    @Override
    public URL resolveIdentifier(MetadataDocument document, URI identifier) {
	try {
	    return document.getFileLocation().resolve(identifier).toURL();
	} catch (MalformedURLException ex) {
	    throw new IdentifierResolutionException(ex);
	}
    }
}
