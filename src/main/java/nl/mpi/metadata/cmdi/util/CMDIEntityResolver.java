/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Default entity resolver for CMDI that resolves w3org's xml.xsd to a copy stored as an internal resource
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIEntityResolver implements EntityResolver {

    private static final String W3ORG_XML_XSD_URI = "http://www.w3.org/2001/xml.xsd";

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	if (systemId.equalsIgnoreCase(W3ORG_XML_XSD_URI)) {
	    return new InputSource(CMDIEntityResolver.class.getResourceAsStream("/xsd/xml.xsd"));
	} else {
	    return new InputSource(systemId);
	}
    }
}
