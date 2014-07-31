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
package nl.mpi.metadata.cmdi.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Default entity resolver for CMDI that resolves w3org's xml.xsd to a copy stored as an internal resource
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIEntityResolver implements EntityResolver {

    private static Logger logger = LoggerFactory.getLogger(CMDIEntityResolver.class);
    private static final String W3ORG_XML_XSD_URI = "http://www.w3.org/2001/xml.xsd";

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	if (systemId.equalsIgnoreCase(W3ORG_XML_XSD_URI)) {
	    return new InputSource(CMDIEntityResolver.class.getResourceAsStream("/xsd/xml.xsd"));
	} else {
	    return new InputSource(systemId);
	}
    }

    /**
     * Convenience method. Calls {@link #getInputStreamForURI(org.xml.sax.EntityResolver, java.lang.String) } with {@code location.toString()}
     * @see #getInputStreamForURI(org.xml.sax.EntityResolver, java.lang.String) 
     */
    public static InputStream getInputStreamForURI(final EntityResolver entityResolver, final URI location) throws IOException, MalformedURLException {
	return getInputStreamForURI(entityResolver, location.toString());
    }

    /**
     * Creates an input stream for a remote location. If entityResolver is null, the original location is used.
     * @param entityResolver EntityResolver to use. Can be null.
     * @param location location to open InputStream for
     * @return a new input stream to the specified location
     * @throws IOException can occur while resolving location or opening stream on resolved location
     * @throws MalformedURLException If the string specifies an unknown protocol. See {@link URL#URL(java.lang.String) }.
     */
    public static InputStream getInputStreamForURI(final EntityResolver entityResolver, final String location) throws IOException, MalformedURLException {
	if (entityResolver != null) {
	    try {
		final InputSource resolvedEntity = entityResolver.resolveEntity(null, location);
		final InputStream byteStream = resolvedEntity.getByteStream();
		if (byteStream != null) {
		    return byteStream;
		} else if (resolvedEntity.getSystemId() != null) {
		    return new URL(resolvedEntity.getSystemId()).openStream();
		}
	    } catch (SAXException sEx) {
		logger.warn("SAXException while resolving schema location. Proceeding with unresolved location: " + location, sEx);
	    }
	}
	return new URL(location).openStream();
    }
}
