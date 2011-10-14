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
package nl.mpi.metadata.cmdi.api.type;

import java.io.IOException;
import java.net.URI;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.junit.Test;

import org.xml.sax.EntityResolver;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainerTest extends CMDIAPITest {

    @Test
    public void testGetProfile() throws Exception {
	URI testUri = testSchema.toURI();
	CMDIProfileContainer container = new CMDIProfileContainer();
	// Is empty
	assertFalse(container.containsProfile(testUri));
	// Load profile
	CMDIProfile profile = container.getProfile(testUri);
	assertNotNull(profile);
	// Contains this profile
	assertTrue(container.containsProfile(testUri));
	assertTrue(container.containsProfile(profile));

	// Request again, should be the same object
	CMDIProfile profile2 = container.getProfile(testUri);
	assertTrue(profile == profile2);
    }

    @Test
    public void testCustomEntityResolver() throws Exception {
	// Custom resolver
	class MyEntityResolver implements EntityResolver {

	    public boolean hit = false;
	    private EntityResolver cmdiResolver = new CMDIEntityResolver();

	    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		hit = true;
		return cmdiResolver.resolveEntity(publicId, systemId);
	    }
	}
	MyEntityResolver myER = new MyEntityResolver();

	CMDIProfileContainer container = new CMDIProfileContainer(myER);
	// Resolver should not have been hit
	assertFalse(myER.hit);
	// Request new schema from container, so it has to be created
	container.getProfile(testSchema.toURI());
	// Should have been hit for xml.xsd
	assertTrue(myER.hit);
    }
}
