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
package nl.mpi.metadata.cmdi.api.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import java.net.URI;
import javax.xml.parsers.DocumentBuilderFactory;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDomBuilderTest extends CMDIAPITestCase {

    /**
     * Test of createDomFromSchema method, of class CMDIDomBuilder.
     */
    @Test
    public void testCreateDomFromSchema() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	Document document = instance.createDomFromSchema(new URI(REMOTE_TEXT_CORPUS_SCHEMA_URL), false);

	// Check DOM
	Node rootNode = document.getFirstChild();
	assertEquals("CMD", rootNode.getLocalName());
	Node componentsNode = rootNode.getLastChild();
	assertEquals("Components", componentsNode.getLocalName());
	assertEquals("TextCorpusProfile", componentsNode.getFirstChild().getLocalName());
    }

    /**
     * Test of getEntityResolver method, of class CMDIDomBuilder.
     */
    @Test
    public void testGetEntityResolver() {
	EntityResolver entityResolver = new CMDIEntityResolver();
	CMDIDomBuilder instance = new CMDIDomBuilder(entityResolver, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	assertSame(entityResolver, instance.getEntityResolver());
    }

    @Test
    public void testBuildDomForDocument() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument document = getNewTestDocument();
	Document result = instance.buildDomForDocument(document);
	assertNotNull(result);
    }

    @Test
    public void testGetBaseDocument() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	// Load document from disk
	CMDIDocument document = getNewTestDocument();
	Document baseDocument = instance.getBaseDocument(document);

	CachedXPathAPI xPathAPI = new CachedXPathAPI();
	// Select an element
	Node node = xPathAPI.selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	// Should be there
	assertNotNull(node);
	// With content
	assertEquals("TextCorpus test", node.getTextContent());
	// Select optional element
	node = xPathAPI.selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description");
	// Should be there as well
	assertNotNull(node);
    }

    @Test
    public void testGetBaseDocumentUnsaved() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	// New document, not from disk
	CMDIDocument document = new CMDIDocument(getNewTestProfileAndRead());
	Document baseDocument = instance.getBaseDocument(document);

	CachedXPathAPI xPathAPI = new CachedXPathAPI();
	// Select mandatory element 
	Node node = xPathAPI.selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	// Should be there
	assertNotNull(node);
	// But no content
	assertEquals("", node.getTextContent());
	// Select optional element
	node = xPathAPI.selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description");
	// Should not be there
	assertNull(node);
    }
}
