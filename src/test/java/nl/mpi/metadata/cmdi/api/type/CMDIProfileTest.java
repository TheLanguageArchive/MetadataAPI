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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileTest extends CMDIAPITest {

    @Test
    public void testLoadSchema() throws Exception {
	CMDIProfile profile = new CMDIProfile(testSchemaSession.toURI());
	profile.readSchema();

	assertEquals(profile.getName(), "TextCorpusProfile");
	// Session has 7 children (descriptions, MDGroup, ...)
	assertEquals(profile.getContainableTypes().size(), 3);
	// Has 2 attributes (ref, componentId)
	assertEquals(profile.getAttributes().size(), 1);

	ComponentType corpusType = (ComponentType) profile.getType("Corpus");
	assertNotNull(corpusType);
	ElementType topicType = (ElementType) corpusType.getType("Topic");
	assertNotNull(topicType);

	// Test containability
	assertFalse(profile.canContainType(topicType));
	assertTrue(corpusType.canContainType(topicType));

	//Test cardinality	
	assertEquals(1, corpusType.getMinOccurences(profile));
	assertEquals(1, corpusType.getMaxOccurences(profile));
	assertEquals(0, topicType.getMinOccurences(corpusType));
	assertEquals(-1, topicType.getMaxOccurences(corpusType));
    }

    @Test
    public void testEquals() throws Exception {
	CMDIProfile profile1 = new CMDIProfile(testSchemaSession.toURI());
	CMDIProfile profile2 = new CMDIProfile(testSchemaSession.toURI());
	assertTrue("Expected equality of profiles", profile1.equals(profile2));
	assertTrue("Expected equality of profiles", profile2.equals(profile1));
	CMDIProfile profile3 = new CMDIProfile(testSchemaWebservice.toURI());
	assertFalse("Expected non-equality of profiles", profile1.equals(profile3));
	assertFalse("Expected non-equality of profiles", profile3.equals(profile1));
    }
}
