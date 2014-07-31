/*
 * Copyright (C) 2012 Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.cmdi.api;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class TracingEntityResolver implements EntityResolver {

    Logger LOG = LoggerFactory.getLogger(TracingEntityResolver.class);
    protected boolean triggered;

    public boolean isTriggered() {
	return triggered;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	LOG.debug("TracingEntityResolver. Current state of triggered is: {}", triggered);
	triggered = true;
	return CMDIAPITestCase.CMDI_API_TEST_ENTITY_RESOLVER.resolveEntity(publicId, systemId);
    }
}
