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
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import static org.jmock.Expectations.returnValue;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileContainerTest extends CMDIAPITestCase {

    private Mockery context = new JUnit4Mockery();

    @Test
    public void testGetProfile() throws Exception {

	final MetadataDocumentTypeReader<CMDIProfile> reader = context.mock(MetadataDocumentTypeReader.class, "CMDIProfile");
	final CMDIProfileContainer instance = new CMDIProfileContainer(reader);

	final URI testUri = URI.create("http://test/uri");
	final CMDIProfile testProfile = getNewTestProfileAndRead(); //TODO: Mock once an interface has been extracted from CMDIProfile 
	
	// Is empty
	assertFalse(instance.containsProfile(testUri));
	
	// Load profile
	context.checking(new Expectations() {
	    {
		oneOf(reader).read(testUri);
		will(returnValue(testProfile));
	    }
	});
	CMDIProfile profile = instance.getProfile(testUri);
	assertNotNull(profile);
	// Contains this profile
	assertTrue(instance.containsProfile(testUri));
	assertTrue(instance.containsProfile(profile));

	// Request again, should be the same object
	context.checking(new Expectations() {
	    {
		never(reader).read(testUri);
	    }
	});
	CMDIProfile profile2 = instance.getProfile(testUri);
	assertSame(profile, profile2);
    }
}
