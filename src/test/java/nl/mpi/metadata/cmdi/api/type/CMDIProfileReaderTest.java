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

import java.net.URI;
import java.net.URL;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileReaderTest extends CMDIAPITestCase {

    @Test
    public void testLoadSchema() throws Exception {
	CMDIProfileReader reader = new CMDIProfileReader(CMDI_API_TEST_ENTITY_RESOLVER);
	CMDIProfile profile = reader.read(testSchemaTextCorpus.toURI());

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

	//Test display priority
	ComponentType generalInfoType = (ComponentType) ((ComponentType) profile.getType("Collection")).getContainableTypeByName("GeneralInfo");
	ElementType nameType = (ElementType) generalInfoType.getContainableTypeByName("Name");
	assertEquals(0, nameType.getDisplayPriority());
	ElementType titleType = (ElementType) generalInfoType.getContainableTypeByName("Title");
	assertEquals(1, titleType.getDisplayPriority());

	//Test data category
	DataCategory dc2544 = new DataCategory(new URI("http://www.isocat.org/datcat/DC-2544"));
	assertEquals(dc2544, nameType.getDataCategory());
	DataCategory dc2503 = new DataCategory(new URI("http://www.isocat.org/datcat/DC-2503"));
	assertEquals(dc2503, topicType.getDataCategory());
    }

    @Test
    public void testCustomEntityResolver() throws Exception {
	final URL remoteURL = new URL(REMOTE_TEXT_CORPUS_SCHEMA_URL);

	TestEntityResolver testResolver = new TestEntityResolver(remoteURL, testSchemaTextCorpus);
	assertEquals(0, testResolver.byteStreamRequested);

	// Read schema
	CMDIProfileReader reader = new CMDIProfileReader(testResolver);
	CMDIProfile profile = reader.read(remoteURL.toURI());
	// This should cause the entity resolver to be triggered twice (once for loading SchemaTypeSystem and once for reading schema DOM)
	assertEquals(2, testResolver.byteStreamRequested);
	// Should still match REMOTE schema location
	assertEquals(new URI(REMOTE_TEXT_CORPUS_SCHEMA_URL), profile.getSchemaLocation());
    }

    @Test
    public void testNoEntityResolver() throws Exception {
	// Small schema has no xml:lang import
	CMDIProfileReader reader = new CMDIProfileReader(null);
	CMDIProfile profile = reader.read(testSchemaSmall.toURI());
	// Schema location should match
	assertEquals(testSchemaSmall.toURI(), profile.getSchemaLocation());
    }
}
