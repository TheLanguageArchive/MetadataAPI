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

import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import java.net.URI;
import java.util.Collections;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xpath.CachedXPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import static org.junit.Assert.*;
import static nl.mpi.metadata.cmdi.api.CMDIConstants.*;

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
    public void testBuildDomForDocumentHeaders() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument();

	// Modify header info
	metadataDocument.putHeaderInformation(new HeaderInfo(CMD_HEADER_MD_CREATION_DATE,
		"1999-99-99",
		Collections.singletonMap("testAttribute", "value")));
	metadataDocument.removeHeaderInformation(CMD_HEADER_MD_PROFILE);

	// Build DOM
	Document document = instance.buildDomForDocument(metadataDocument);
	assertNotNull(document);

	CachedXPathAPI xPathAPI = new CachedXPathAPI();
	//MdCreator (unchanged header)
	Node node = xPathAPI.selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_CREATOR);
	assertNotNull("MdCreator header", node);
	assertEquals("MdCreator header content unchanged", "Joe Unit", node.getTextContent());
	//MdCreationDate (modified header)
	node = xPathAPI.selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_CREATION_DATE);
	assertNotNull("MdCreationDate header", node);
	assertEquals("MdCreationDate header content changed", "1999-99-99", node.getTextContent());
	//MdCreationDate (set attribute)
	assertEquals("MdCreationDate attribute", 1, node.getAttributes().getLength());
	Node attributeNode = node.getAttributes().item(0);
	assertEquals("MdCreationDate attribute name", "testAttribute", attributeNode.getNodeName());
	assertEquals("MdCreationDate attribute value", "value", attributeNode.getNodeValue());

	//MdProfile (removed header)
	node = xPathAPI.selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_PROFILE);
	assertNull("MdProfile header removed", node);
    }

    @Test
    public void testBuildDomForDocumentComponents() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument();
	// Build DOM
	Document document = instance.buildDomForDocument(metadataDocument);
	assertNotNull(document);
	CachedXPathAPI xPathAPI = new CachedXPathAPI();
	// Existence of root component node
	Node rootComponentNode = xPathAPI.selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile");
	assertNotNull("Root component node", rootComponentNode);
	// Existence of child node
	Node collectionNode = xPathAPI.selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection");
	assertNotNull("Root child component node", collectionNode);
	// Content of element node
	Node nameNode = xPathAPI.selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	assertNotNull(nameNode);
	assertEquals("Content of MimeType element", "TextCorpus test", nameNode.getTextContent());
	// Attribute on element
	assertEquals(1, nameNode.getAttributes().getLength());
	Node xmlLangAttributeNode = nameNode.getAttributes().item(0);
	assertEquals("en", xmlLangAttributeNode.getNodeValue());
	assertEquals("lang", xmlLangAttributeNode.getLocalName());
	assertEquals("http://www.w3.org/XML/1998/namespace", xmlLangAttributeNode.getNamespaceURI());
	Node languageIdAttributeNode = xPathAPI.selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description/:Description/@LanguageID");
	assertEquals("LanguageID", languageIdAttributeNode.getLocalName());
	assertNull("Default namespace for CMD specified attributes", languageIdAttributeNode.getNamespaceURI());
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
