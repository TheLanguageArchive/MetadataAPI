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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URISyntaxException;
import java.util.Collection;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentTest extends CMDIAPITestCase {

    public CMDIDocumentTest() {
    }
    private CMDIDocument document;
    private Document domDocument;
    private CMDIProfile profile;
    private Node documentRootNode;

    @Before
    public void setUp() throws Exception {
	profile = getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
	domDocument = getDomDocumentForResource("/cmdi/TextCorpusProfile-instance.cmdi");
	documentRootNode = XPathAPI.selectSingleNode(domDocument, "/CMD/Components/TextCorpusProfile");
	document = new CMDIDocument(documentRootNode, profile, testSchemaTextCorpus.toURI());
    }

    /**
     * Test of getType method, of class CMDIDocument.
     */
    @Test
    public void testGetType() {
	assertEquals(profile, document.getType());
    }

    /**
     * Test of getFileLocation method, of class CMDIDocument.
     */
    @Test
    public void testGetFileLocation() throws URISyntaxException {
	assertEquals(testSchemaTextCorpus.toURI(), document.getFileLocation());
    }

    /**
     * Test of getMetadataDocument method, of class CMDIDocument.
     */
    @Test
    public void testGetMetadataDocument() {
	assertEquals(document, document.getMetadataDocument());
    }

    /**
     * Test of getDomNode method, of class CMDIDocument.
     */
    @Test
    public void testGetDomNode() {
	assertEquals(documentRootNode, document.getDomNode());
    }

    @Test
    public void testAddChildElement() throws TransformerException {
	// Document should have no children right after creation
	assertEquals(0, document.getChildren().size());
	// Get child node from DOM
	Node collectionNode = getCollectionNode();
	// Create component
	Component component = createComponent(collectionNode, "Collection");
	// Add as a child
	document.addChildElement(component);
	// It should be in the appear as a child (+1)
	assertEquals(1, document.getChildren().size());
	// And it should be the same component
	assertEquals(component, document.getChildren().get(0));
    }

    @Test
    public void testRemoveChildElement() throws TransformerException {
	int childCount = document.getChildren().size();
	// Get child node from DOM
	Node collectionNode = getCollectionNode();
	// Create component
	Component component = createComponent(collectionNode, "Collection");
	// Add it as a child
	document.addChildElement(component);
	// There should be one more (+1)
	assertEquals(childCount + 1, document.getChildren().size());
	document.removeChildElement(component);
	// There should be one less (-1)
	assertEquals(childCount, document.getChildren().size());
    }

    /**
     * Test of addElementToMap method, of class CMDIDocument.
     */
    @Test
    public void testAddElementToMap() throws TransformerException {
	// Get child node from DOM
	Node collectionNode = getCollectionNode();
	// Create component
	Component component = createComponent(collectionNode, "Collection");
	// Add to map
	document.addElementToMap(component);
	// It should be in the element map
	assertEquals(component, document.getElementFromMap(collectionNode));
    }

    /**
     * Test of removeElementFromMap method, of class CMDIDocument.
     */
    @Test
    public void testRemoveElementFromMap() throws TransformerException {
	// Get child node from DOM
	Node collectionNode = getCollectionNode();
	// Create component
	Component component = createComponent(collectionNode, "Collection");
	// Add to map
	document.addElementToMap(component);
	// It should be in the element map and appear as a child
	assertEquals(component, document.getElementFromMap(collectionNode));
	// Remove child element
	document.removeChildElement(component);
	// Should not be there anymore
	assertEquals(null, document.getElementFromMap(collectionNode));
    }

    /**
     * Test of getHeaderInformation method, of class CMDIDocument.
     */
    @Test
    @Ignore
    public void testGetHeaderInformation() {
	System.out.println("getHeaderInformation");
	CMDIDocument instance = null;
	Collection expResult = null;
	Collection result = instance.getHeaderInformation();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of addMetadataDocumentListener method, of class CMDIDocument.
     */
    @Test
    @Ignore
    public void testAddMetadataDocumentListener() {
	System.out.println("addMetadataDocumentListener");
	MetadataDocumentListener listener = null;
	CMDIDocument instance = null;
	instance.addMetadataDocumentListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of removeMetadataDocumentListener method, of class CMDIDocument.
     */
    @Test
    @Ignore
    public void testRemoveMetadataDocumentListener() {
	System.out.println("removeMetadataDocumentListener");
	MetadataDocumentListener listener = null;
	CMDIDocument instance = null;
	instance.removeMetadataDocumentListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of getMetadataDocumentListeners method, of class CMDIDocument.
     */
    @Test
    @Ignore
    public void testGetMetadataDocumentListeners() {
	System.out.println("getMetadataDocumentListeners");
	CMDIDocument instance = null;
	Collection expResult = null;
	Collection result = instance.getMetadataDocumentListeners();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    private Node getCollectionNode() throws TransformerException {
	// Get child node from DOM
	Node childNode = XPathAPI.selectSingleNode(documentRootNode, "/Collection");
	return childNode;
    }

    private Component createComponent(Node componentNode, String componentName) throws TransformerException {
	// Should not be in elements map yet
	assertNull(document.getElementFromMap(componentNode));
	// Create a component of CollectionType and add it to the document
	ComponentType collectionComponentType = (ComponentType) profile.getType(componentName);
	return new Component(componentNode, collectionComponentType, document);
    }
}
