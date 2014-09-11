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
package nl.mpi.metadata.cmdi.api.type.impl;

import java.net.URI;
import java.net.URL;
import java.util.List;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.type.CMDIControlledVocabularyItem;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ControlledVocabularyElementType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileReaderTest extends CMDIAPITestCase {

    @Test
    public void testLoadSchema() throws Exception {
        CMDIProfileReader reader = new CMDIProfileReader(CMDI_API_TEST_ENTITY_RESOLVER);
        CMDIProfile profile = reader.read(testSchemaTextCorpus.toURI());

        ComponentType corpusType = (ComponentType) profile.getType("Corpus");
        ComponentType generalInfoType = (ComponentType) ((ComponentType) profile.getType("Collection")).getType("GeneralInfo");

        ComponentType languagesType = (ComponentType) ((ComponentType) profile.getType("Collection")).getType("DocumentationLanguages");
        ComponentType isoType = (ComponentType) ((ComponentType) languagesType.getType("Language")).getType("ISO639");
        ElementType isoCodeType = (ElementType) isoType.getType("iso-639-3-code"); // has a vocabulary

        // Test profile
        assertEquals(profile.getName(), "TextCorpusProfile");
        // Session has 7 children (descriptions, MDGroup, ...)
        assertEquals(profile.getContainableTypes().size(), 3);
        // Has 2 attributes (ref, componentId)
        assertEquals(profile.getAttributes().size(), 1);

        // Test components and elements
        ElementType topicType = (ElementType) corpusType.getType("Topic");
        ElementType nameType = (ElementType) generalInfoType.getType("Name");
        ElementType descriptionType = (ElementType) ((ComponentType) generalInfoType.getType("Description")).getType("Description");
        ElementType titleType = (ElementType) generalInfoType.getType("Title");
        ElementType idType = (ElementType) generalInfoType.getType("ID");
        assertNotNull(corpusType);
        assertNotNull(topicType);

        assertTrue(isoCodeType instanceof ControlledVocabularyElementType);
        final List<ControlledVocabularyItem> items = ((ControlledVocabularyElementType) isoCodeType).getItems();
        assertEquals(7679, items.size());

        // Test vocabulary
        final ControlledVocabularyItem firstItem = items.get(0);
        assertTrue(firstItem instanceof CMDIControlledVocabularyItem);
        assertEquals("aaa", firstItem.getValue());
//        assertEquals("Ghotuo", firstItem.getDescription());
//        final DataCategory vocabDC = ((CMDIControlledVocabularyItem) firstItem).getDataCategory();
//        assertNotNull(vocabDC);
//        assertEquals(URI.create("http://cdb.iso.org/lg/CDB-00132443-001"), vocabDC.getIdentifier());

        // Test containability
        assertFalse(profile.canContainType(topicType));
        assertTrue(corpusType.canContainType(topicType));

        //Test cardinality	
        assertEquals(1, corpusType.getMinOccurences());
        assertEquals(1, corpusType.getMaxOccurences());
        assertEquals(0, topicType.getMinOccurences());
        assertEquals(-1, topicType.getMaxOccurences());

        //Test display priority
        assertEquals(0, nameType.getDisplayPriority());
        assertEquals(1, titleType.getDisplayPriority());
        //If unspecified in XML, should default to 0
        assertEquals(0, idType.getDisplayPriority());

        //Test data category
        assertNull(corpusType.getDataCategory());
        DataCategory dc2544 = new DataCategory(new URI("http://www.isocat.org/datcat/DC-2544"));
        assertEquals(dc2544, nameType.getDataCategory());
        assertEquals(dc2544, generalInfoType.getDataCategory());
        DataCategory dc2503 = new DataCategory(new URI("http://www.isocat.org/datcat/DC-2503"));
        assertEquals(dc2503, topicType.getDataCategory());

        //todo: get originlocation/location/continent and check vocabulary including data categories
        //Test documentation
        assertEquals("Name of the collection", nameType.getDescription());
        assertEquals("General information about this collection", generalInfoType.getDescription());
        assertNull(titleType.getDescription());

        //Test attributes
        assertEquals(1, descriptionType.getAttributes().size()); //xml:lang attribute
        assertEquals(2, descriptionType.getAllAttributes().size()); //xml:lang attribute
        assertEquals(0, idType.getAttributes().size());
        // Test attribute path
        final MetadataElementAttributeType attribute = descriptionType.getAttributes().iterator().next();
        assertEquals("/cmd:CMD/cmd:Components/cmd:TextCorpusProfile/cmd:Collection/cmd:GeneralInfo/cmd:Description/cmd:Description/@LanguageID", attribute.getPathString());

        //Test multilingual
        assertTrue(nameType.isMultilingual());
        assertFalse(idType.isMultilingual());

        //Test headers
        assertArrayEquals(new Object[]{"MdCreator", "MdCreationDate", "MdSelfLink", "MdProfile", "MdCollectionDisplayName"}, profile.getHeaderNames().toArray());
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
