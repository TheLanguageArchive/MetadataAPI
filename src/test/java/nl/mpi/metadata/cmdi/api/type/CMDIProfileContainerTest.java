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
package nl.mpi.metadata.cmdi.api.type;

import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import java.net.URI;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainerTest extends CMDIAPITestCase {

    @Test
    public void testGetProfile() throws Exception {
	
	//TODO: Mock profile container
	
	URI testUri = testSchemaTextCorpus.toURI();
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
	assertSame(profile, profile2);
    }
}
