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

import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileTest extends CMDIAPITest {

    @Test
    public void testLoadSchema() throws Exception {
	CMDIProfile profile = new CMDIProfile(testSchema.toURI());

	assertEquals(profile.getName(), "Session");
	// Session has 7 children (descriptions, MDGroup, ...)
	assertEquals(profile.getContainableTypes().size(), 7);
	// Has 2 attributes (ref, componentId)
	assertEquals(profile.getAttributes().size(), 2);

	ComponentType descriptionsType = (ComponentType) profile.getType("descriptions");
	ElementType descriptionType = (ElementType) descriptionsType.getType("Description");

	// Test containability
	assertFalse(profile.canContainType(descriptionType));
	assertTrue(descriptionsType.canContainType(descriptionType));

	//Test cardinality	
	assertEquals(descriptionsType.getMinOccurences(profile), 0);
	assertEquals(descriptionsType.getMaxOccurences(profile), 1);
	assertEquals(descriptionType.getMinOccurences(descriptionsType), 0);
	assertEquals(descriptionType.getMaxOccurences(descriptionsType), -1);
    }
}
