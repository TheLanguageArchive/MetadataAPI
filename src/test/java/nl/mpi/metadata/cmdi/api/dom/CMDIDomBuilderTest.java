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

import java.net.URI;
import java.util.Collections;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.dom.DomBuildingMode;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;

import static nl.mpi.metadata.cmdi.api.CMDIConstants.*;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.model.impl.AttributeImpl;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIDocumentImpl;
import nl.mpi.metadata.cmdi.api.model.impl.ComponentImpl;
import nl.mpi.metadata.cmdi.api.model.impl.ElementImpl;
import nl.mpi.metadata.cmdi.api.model.impl.MultilingualElementImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.impl.ElementTypeImpl;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	Document document = instance.createDomFromSchema(new URI(REMOTE_TEXT_CORPUS_SCHEMA_URL), DomBuildingMode.MANDATORY);

	// Check DOM
	Node rootNode = document.getFirstChild();
	assertEquals("CMD", rootNode.getLocalName());
	Node componentsNode = rootNode.getLastChild();
	assertEquals("Components", componentsNode.getLocalName());
	assertEquals("TextCorpusProfile", componentsNode.getFirstChild().getLocalName());

	// Mandatory attributes should be added (LanguageID is mandatory, xml:lang is optional)
	Node descriptionNode = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description/:Description");
	assertEquals(1, descriptionNode.getAttributes().getLength());
	assertEquals("LanguageID", descriptionNode.getAttributes().item(0).getLocalName());
    }

    /**
     * Test of createDomFromSchema method, of class CMDIDomBuilder.
     */
    @Test
    public void testCreateEmptyDomFromSchema() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	Document document = instance.createDomFromSchema(new URI(REMOTE_TEXT_CORPUS_SCHEMA_URL), DomBuildingMode.EMPTY);

	// Check DOM
	Node rootNode = document.getFirstChild();
	assertEquals("CMD", rootNode.getLocalName());
	Node componentsNode = rootNode.getLastChild();
	assertEquals("Components", componentsNode.getLocalName());
	final Node profileRootNode = componentsNode.getFirstChild();
	assertEquals("TextCorpusProfile", profileRootNode.getLocalName());
	assertEquals(0, profileRootNode.getChildNodes().getLength());
    }

    @Test
    public void testBuildDomForDocumentHeaders() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);

	// Modify header info
	metadataDocument.putHeaderInformation(new HeaderInfo(CMD_HEADER_MD_CREATION_DATE,
		"1999-99-99",
		Collections.singletonMap("testAttribute", "value")));
	metadataDocument.removeHeaderInformation(CMD_HEADER_MD_PROFILE);

	// Build DOM
	Document document = instance.buildDomForDocument(metadataDocument);
	assertNotNull(document);

	//MdCreator (unchanged header)
	Node node = selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_CREATOR);
	assertNotNull("MdCreator header", node);
	assertEquals("MdCreator header content unchanged", "Joe Unit", node.getTextContent());
	//MdCreationDate (modified header)
	node = selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_CREATION_DATE);
	assertNotNull("MdCreationDate header", node);
	assertEquals("MdCreationDate header content changed", "1999-99-99", node.getTextContent());
	//MdCreationDate (set attribute)
	assertEquals("MdCreationDate attribute", 1, node.getAttributes().getLength());
	Node attributeNode = node.getAttributes().item(0);
	assertEquals("MdCreationDate attribute name", "testAttribute", attributeNode.getNodeName());
	assertEquals("MdCreationDate attribute value", "value", attributeNode.getNodeValue());

	//MdProfile (removed header)
	node = selectSingleNode(document, "/:CMD/:Header/:" + CMD_HEADER_MD_PROFILE);
	assertNull("MdProfile header removed", node);
    }

    @Test
    public void testBuildDomForDocumentProxies() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);

	// Modify resource proxies
	metadataDocument.removeDocumentResourceProxy("resource1");
	metadataDocument.addDocumentResourceProxy(new DataResourceProxy("resource3", new URI("http://resources/3"), URI.create("http://resources/files/3.txt"), "MyResourceType", "test/test-resource"));

	// Build DOM
	Document document = instance.buildDomForDocument(metadataDocument);
	assertNotNull(document);

	Node proxiesNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList");
	NodeList proxyListchildNodes = proxiesNode.getChildNodes();
	assertEquals(6, proxyListchildNodes.getLength());

	// Check existing resource proxy items read from file
        // Resource 1 has been removed
	assertEquals("resource2", proxyListchildNodes.item(0).getAttributes().getNamedItem("id").getNodeValue());

	assertEquals("metadata1", proxyListchildNodes.item(1).getAttributes().getNamedItem("id").getNodeValue());
	Node resourceTypeNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[2]/:ResourceType");
	assertNotNull(resourceTypeNode);
	assertEquals("Metadata", resourceTypeNode.getTextContent());
	Node resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[2]/:ResourceRef");
	assertNotNull(resourceRefNode);
	assertEquals("http://metadata/1", resourceRefNode.getTextContent());

	assertEquals("landingPage", proxyListchildNodes.item(2).getAttributes().getNamedItem("id").getNodeValue());
	resourceTypeNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[3]/:ResourceType");
	assertNotNull(resourceTypeNode);
	assertEquals("LandingPage", resourceTypeNode.getTextContent());
	resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[3]/:ResourceRef");
	assertNotNull(resourceRefNode);
	assertEquals("hdl:1839/00-0000-0000-0001-2345-6@view", resourceRefNode.getTextContent());
        
	assertEquals("searchPage1", proxyListchildNodes.item(3).getAttributes().getNamedItem("id").getNodeValue());
	resourceTypeNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[4]/:ResourceType");
	assertNotNull(resourceTypeNode);
	assertEquals("SearchPage", resourceTypeNode.getTextContent());
	resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[4]/:ResourceRef");
	assertNotNull(resourceRefNode);
	assertEquals("http://www.google.com", resourceRefNode.getTextContent());

	assertEquals("searchService1", proxyListchildNodes.item(4).getAttributes().getNamedItem("id").getNodeValue());
	resourceTypeNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[5]/:ResourceType");
	assertNotNull(resourceTypeNode);
	assertEquals("SearchService", resourceTypeNode.getTextContent());
	resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[5]/:ResourceRef");
	assertNotNull(resourceRefNode);
	assertEquals("http://cqlservlet.mpi.nl", resourceRefNode.getTextContent());

	// This resource was added after reading from file

	assertEquals("resource3", proxyListchildNodes.item(5).getAttributes().getNamedItem("id").getNodeValue());
	resourceTypeNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[6]/:ResourceType");
	assertNotNull(resourceTypeNode);
	assertEquals("MyResourceType", resourceTypeNode.getTextContent());
	resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[6]/:ResourceRef");
	assertNotNull(resourceRefNode);
	assertEquals("http://resources/3", resourceRefNode.getTextContent());
        
        final Node locationAttribute = resourceRefNode.getAttributes().getNamedItemNS(CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAMESPACE, CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAME);
        assertNotNull(locationAttribute);
        assertEquals("http://resources/files/3.txt", locationAttribute.getNodeValue());
    }

    @Test
    public void testBuildDomForDocumentProxiesWithModifiedContent() throws Exception {
	final CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	final CMDIDocument metadataDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);

	// Modify the first of the resource proxies
	metadataDocument.getDocumentResourceProxy("resource1").setURI(new URI("http://resources/1/changed"));

	// Build DOM
	final Document document = instance.buildDomForDocument(metadataDocument);
	// Resource ref was modified, see if the change has been written to the DOM
	final Node resourceRefNode = selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/:ResourceProxy[1]/:ResourceRef");
	assertEquals("http://resources/1/changed", resourceRefNode.getTextContent());
    }

    @Test
    public void testBuildDomForDocumentComponents() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	// Build DOM
	Document document = instance.buildDomForDocument(metadataDocument);
	assertNotNull(document);
	// Existence of root component node
	Node rootComponentNode = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile");
	assertNotNull("Root component node", rootComponentNode);
	// Existence of child node
	Node collectionNode = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection");
	assertNotNull("Root child component node", collectionNode);
	// Content of element node
	Node nameNode = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	assertNotNull(nameNode);
	assertEquals("Content of MimeType element", "TextCorpus test", nameNode.getTextContent());
	// Attribute on element
	assertEquals(1, nameNode.getAttributes().getLength());
	Node xmlLangAttributeNode = nameNode.getAttributes().item(0);
	assertEquals("en", xmlLangAttributeNode.getNodeValue());
	assertEquals("lang", xmlLangAttributeNode.getLocalName());
	assertEquals("http://www.w3.org/XML/1998/namespace", xmlLangAttributeNode.getNamespaceURI());
	Node languageIdAttributeNode = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description/:Description/@LanguageID");
	assertEquals("LanguageID", languageIdAttributeNode.getLocalName());
	assertNull("Default namespace for CMD specified attributes", languageIdAttributeNode.getNamespaceURI());
	// Resource proxy reference on element
	Node generalInfoRefAttribute = selectSingleNode(document, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/@ref");
	assertNotNull(generalInfoRefAttribute);
	assertEquals("resource1 resource2", generalInfoRefAttribute.getNodeValue());
    }

    @Test
    public void testBuildDomForDocumentNewDocument() throws Exception {
	//TODO: Use more mocking...

	// Create a new document on basis of a profile
	CMDIProfile profile = getNewTestProfileAndRead();
	CMDIDocument document = new CMDIDocumentImpl(profile);

	// Add some child elements
	ComponentType collectionType = (ComponentType) profile.getType("Collection");
	ComponentType originLocationType = (ComponentType) collectionType.getType("OriginLocation");
	ComponentType generalInfoType = (ComponentType) collectionType.getType("GeneralInfo");
	ComponentType descriptionComponentType = (ComponentType) generalInfoType.getType("Description");
	ElementTypeImpl nameType = (ElementTypeImpl) generalInfoType.getType("Name");
	ElementTypeImpl descriptionType = (ElementTypeImpl) descriptionComponentType.getType("Description");

	Component collection = new ComponentImpl(collectionType, document);
	document.addChildElement(collection);

	Component originLocation = new ComponentImpl(originLocationType, collection);
	collection.addChildElement(originLocation);

	Component generalInfo = new ComponentImpl(generalInfoType, collection);
	collection.addChildElement(generalInfo);

	// Add multilingual name element with language set
	MultilingualElement name = new MultilingualElementImpl(nameType, generalInfo, "test element");
	generalInfo.addChildElement(name);
	name.setLanguage("nl");

	Component descriptionComponent = new ComponentImpl(descriptionComponentType, generalInfo);
	generalInfo.addChildElement(descriptionComponent);

	// Add element with attribute LanguageID set
	Element description = new ElementImpl(descriptionType, descriptionComponent, "description element");
	descriptionComponent.addChildElement(description);

	Attribute langAttr = new AttributeImpl(descriptionType.getAttributeTypeByName(null, "LanguageID"), description);
	langAttr.setValue("en");
	description.addAttribute(langAttr);

	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	Document dom = instance.buildDomForDocument(document);

	XPath xPath = XPathFactory.newInstance().newXPath();
	xPath.setNamespaceContext(new CMDINamespaceContext());
	Node collectionNode = (Node) xPath.evaluate(collection.getPathString(), dom, XPathConstants.NODE);
	NodeList collectionNodeChildren = collectionNode.getChildNodes();

	assertEquals(2, collectionNodeChildren.getLength());
	// Notice that GeneralInfo should appear first, even though it was added second (this the profile order)
	assertEquals("GeneralInfo", collectionNodeChildren.item(0).getLocalName());
	assertEquals("OriginLocation", collectionNodeChildren.item(1).getLocalName());

	Node nameNode = (Node) xPath.evaluate(name.getPathString(), dom, XPathConstants.NODE);
	assertEquals("test element", nameNode.getTextContent());
	assertEquals(1, nameNode.getAttributes().getLength());
	Node langAttrNode = nameNode.getAttributes().getNamedItem("xml:lang");
	assertNotNull(langAttrNode);
	assertEquals("xml:lang", langAttrNode.getNodeName());
	assertEquals("nl", langAttrNode.getNodeValue());

	Node descriptionNode = (Node) xPath.evaluate(description.getPathString(), dom, XPathConstants.NODE);
	assertEquals("description element", descriptionNode.getTextContent());
	assertEquals(1, descriptionNode.getAttributes().getLength());
	langAttrNode = descriptionNode.getAttributes().getNamedItem("LanguageID");
	assertNotNull(langAttrNode);
	assertEquals("LanguageID", langAttrNode.getNodeName());
	assertEquals("en", langAttrNode.getNodeValue());
    }

    @Test
    public void testGetBaseDocument() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	// Load document from disk
	CMDIDocument document = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	Document baseDocument = instance.getBaseDocument(document);

	// Select an element
	Node node = selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	// Should be there
	assertNotNull(node);
	// With content
	assertEquals("TextCorpus test", node.getTextContent());
	// Select optional element
	node = selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Description");
	// Should be there as well
	assertNotNull(node);
    }

    @Test
    public void testBuildDomForDocumentResult() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	CMDIDocument metadataDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	// Build DOM
	Document builtDocument = instance.buildDomForDocument(metadataDocument);
	// Compare to reading of original DOM
	Document originalDocument = XMLUnit.buildControlDocument(new InputSource(getClass().getResourceAsStream(TEXT_CORPUS_INSTANCE_LOCATION)));

	XMLUnit.setIgnoreWhitespace(false);
	XMLUnit.setIgnoreComments(false);
	XMLUnit.setIgnoreAttributeOrder(false);

	Diff compareXML = XMLUnit.compareXML(originalDocument, builtDocument);
	assertTrue(compareXML.toString(), compareXML.identical());
    }

    @Test
    public void testGetBaseDocumentUnsaved() throws Exception {
	CMDIDomBuilder instance = new CMDIDomBuilder(CMDI_API_TEST_ENTITY_RESOLVER, CMDI_API_TEST_DOM_BUILDER_FACTORY);
	// New document, not from disk
	CMDIDocument document = new CMDIDocumentImpl(getNewTestProfileAndRead());
	Document baseDocument = instance.getBaseDocument(document);

	// Select mandatory element 
	Node node = selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile");
	// Should be there
	assertNotNull(node);
	// Select mandatory element
	node = selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:Name");
	// Should not be there
	assertNull(node);
	// Select optional element
	node = selectSingleNode(baseDocument, "/:CMD/:Components/:TextCorpusProfile/:Collection/:GeneralInfo/:OriginLocation");
	// Should not be there
	assertNull(node);
    }
}
