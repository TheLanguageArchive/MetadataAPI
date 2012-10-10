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
package nl.mpi.metadata.cmdi.api.model.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentImplTest extends CMDIMetadataElementImplTest {

    public CMDIDocumentImplTest() {
    }
    private CMDIDocument document;
    private CMDIProfile profile;
    private Mockery mockContext = new JUnit4Mockery();

    @Before
    public void setUp() throws Exception {
	profile = getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
	document = new CMDIDocumentImpl(profile, testSchemaTextCorpus.toURI());
    }

    /**
     * Test of getType method, of class CMDIDocumentImpl.
     */
    @Test
    public void testGetType() {
	assertEquals(profile, document.getType());
    }

    /**
     * Test of getFileLocation method, of class CMDIDocumentImpl.
     */
    @Test
    public void testGetFileLocation() throws URISyntaxException {
	assertEquals(testSchemaTextCorpus.toURI(), document.getFileLocation());
    }

    /**
     * Test of getFileLocation method, of class CMDIDocumentImpl.
     */
    @Test
    public void testSetFileLocation() throws URISyntaxException {
	document.setFileLocation(new URI("http://mynewuri"));
	assertEquals(new URI("http://mynewuri"), document.getFileLocation());
    }

    /**
     * Test of getMetadataDocument method, of class CMDIDocumentImpl.
     */
    @Test
    public void testGetMetadataDocument() {
	assertEquals(document, document.getMetadataDocument());
    }

    /**
     * Test of getHeaderInformation method, of class CMDIDocumentImpl.
     */
    @Test
    public void testGetHeaderInformation() throws MetadataException {
	assertEquals(0, document.getHeaderInformation().size());

	assertNull(document.getHeaderInformation("MdProfile"));
	document.putHeaderInformation(new HeaderInfo("MdProfile", "Value"));
	assertEquals(1, document.getHeaderInformation().size());
	assertNotNull(document.getHeaderInformation("MdProfile"));
	assertSame(document.getHeaderInformation("MdProfile"), document.getHeaderInformation().iterator().next());
	assertEquals("Value", document.getHeaderInformation("MdProfile").getValue());

	assertNull(document.getHeaderInformation("MdCreationDate"));
	document.removeHeaderInformation("MdCreationDate");
	assertEquals(1, document.getHeaderInformation().size());
	document.removeHeaderInformation("MdProfile");
	assertEquals(0, document.getHeaderInformation().size());
	assertNull(document.getHeaderInformation("MdProfile"));
    }

    @Test
    public void testPutHeaderInformation() throws MetadataException {
	document.putHeaderInformation(new HeaderInfo("MdProfile", "Value"));
	assertEquals("MdProfile", document.getHeaderInformation().get(0).getName());
	// Add header info that should be inserted before the previous one
	document.putHeaderInformation(new HeaderInfo("MdCreator", "Value"));
	// Order should match profile order
	assertEquals("MdCreator should have been inserted before MdProfile.", "MdCreator", document.getHeaderInformation().get(0).getName());
	assertEquals("MdProfile should be second in the list after MdCreator.", "MdProfile", document.getHeaderInformation().get(1).getName());
	document.putHeaderInformation(new HeaderInfo("MdCollectionDisplayName", "Value"));
	assertEquals("MdCollectionDisplayName should be second in the list after MdCreator.", "MdCollectionDisplayName", document.getHeaderInformation().get(2).getName());

	// Replace value
	assertEquals("Value", document.getHeaderInformation().get(1).getValue());
	document.putHeaderInformation(new HeaderInfo("MdProfile", "NewValue"));
	assertEquals("NewValue", document.getHeaderInformation().get(1).getValue());
    }

    /**
     * Test of getHeaderInformation method, of class CMDIDocumentImpl.
     */
    @Test(expected = MetadataException.class)
    public void testPutHeaderInformationIllegal() throws MetadataException {
	document.putHeaderInformation(new HeaderInfo("MyIllegalHeader", "Value"));
    }

    @Test
    public void testGetDocumentResourceProxyById() throws URISyntaxException {
	assertNull(document.getDocumentResourceProxy("rpId"));
	// add a proxy
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(resourceProxy, document.getDocumentResourceProxy("rpId"));
	// replace by proxy with same id
	DataResourceProxy resourceProxy2 = new DataResourceProxy("rpId", new URI("http://resource2"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy2);
	assertEquals(resourceProxy2, document.getDocumentResourceProxy("rpId"));
    }

    @Test
    public void testGetDocumentReferenceByURI() throws URISyntaxException {
	assertNull(document.getDocumentReferenceByURI(new URI("http://resource")));
	// add a proxy
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(resourceProxy, document.getDocumentReferenceByURI(new URI("http://resource")));
	// replace by proxy with same id
	DataResourceProxy resourceProxy2 = new DataResourceProxy("rpId", new URI("http://resource2"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy2);
	assertNull(document.getDocumentReferenceByURI(new URI("http://resource")));
	assertEquals(resourceProxy2, document.getDocumentReferenceByURI(new URI("http://resource2")));
    }

    @Test
    public void testAddDocumentResourceProxy() throws URISyntaxException {
	assertEquals(0, document.getDocumentReferencesCount());
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(1, document.getDocumentReferencesCount());
	assertEquals(resourceProxy, document.getDocumentReferences().iterator().next());
    }

    @Test
    public void testRemoveDocumentResourceProxy() throws URISyntaxException {
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(1, document.getDocumentReferencesCount());
	document.removeDocumentResourceProxy("rpId");
	assertEquals(0, document.getDocumentReferencesCount());
    }

    @Test
    public void testCreateDocumentResourceProxy() throws Exception {
	assertEquals(0, document.getDocumentReferencesCount());
	DataResourceProxy resourceProxy = document.createDocumentResourceReference(new URI("http://resource"), "test/mime-type");
	assertEquals(1, document.getDocumentReferencesCount());
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
	// Create again, should return same object
	DataResourceProxy resourceProxy2 = document.createDocumentResourceReference(new URI("http://resource"), "test/new-mime-type");
	assertSame(resourceProxy2, resourceProxy);
	// Nothing should have been added
	assertEquals(1, document.getDocumentReferencesCount());
	// Create again, should have ignored mime type	
	assertEquals("test/mime-type", resourceProxy2.getMimetype());

	try {
	    // Cause conflict: add md proxy
	    document.addDocumentResourceProxy(new MetadataResourceProxy("mdrp", new URI("http://resource2"), "test/mime-type"));
	    // Try to create resource proxy with same URI
	    document.createDocumentResourceReference(new URI("http://resource2"), "test/mime-type");
	    // Exception gets thrown, shouldn't get this far
	    fail("Collision should throw MetadataException");
	} catch (MetadataException mdEx) {
	    // Should occur
	}
    }

    @Test
    public void testCreateDocumentMetadataResourceProxy() throws Exception {
	assertEquals(0, document.getDocumentReferencesCount());
	MetadataResourceProxy resourceProxy = document.createDocumentMetadataReference(new URI("http://resource"), "test/mime-type");
	assertEquals(1, document.getDocumentReferencesCount());
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
	// Create again, should return same object
	MetadataResourceProxy resourceProxy2 = document.createDocumentMetadataReference(new URI("http://resource"), "test/new-mime-type");
	assertSame(resourceProxy2, resourceProxy);
	// Nothing should have been added
	assertEquals(1, document.getDocumentReferencesCount());
	// Create again, should have ignored mime type	
	assertEquals("test/mime-type", resourceProxy2.getMimetype());


	try {
	    // Cause conflict: add resource proxy
	    document.addDocumentResourceProxy(new DataResourceProxy("mdrp", new URI("http://resource2"), "test/mime-type"));
	    // Try to create metadata proxy with same URI
	    document.createDocumentMetadataReference(new URI("http://resource2"), "test/mime-type");
	    // Exception gets thrown, shouldn't get this far
	    fail("Collision should throw MetadataException");
	} catch (MetadataException mdEx) {
	    // Should occur
	}
    }

    @Test
    public void testRegisterResourceProxyReference() throws Exception {
	DataResourceProxy proxy = new DataResourceProxy(UUID.randomUUID().toString(), new URI("http://resource"), "test/mime-type");
	// No references yet
	assertEquals(0, document.getResourceProxyReferences(proxy).size());

	// Add a reference to the proxy
	CMDIMetadataElement element = mockContext.mock(CMDIMetadataElement.class);
	document.registerResourceProxyReference(proxy, element);
	assertEquals(1, document.getResourceProxyReferences(proxy).size());
	assertSame(element, document.getResourceProxyReferences(proxy).iterator().next());

	// Add reference again, should not make a difference
	document.registerResourceProxyReference(proxy, element);
	assertEquals(1, document.getResourceProxyReferences(proxy).size());
    }

    @Test
    public void testUnregisterResourceProxyReference() throws Exception {
	DataResourceProxy proxy = new DataResourceProxy(UUID.randomUUID().toString(), new URI("http://resource"), "test/mime-type");

	// Add a reference to the proxy
	CMDIMetadataElement element = mockContext.mock(CMDIMetadataElement.class);
	document.registerResourceProxyReference(proxy, element);
	assertEquals(1, document.getResourceProxyReferences(proxy).size());

	// Remove it
	boolean result = document.unregisterResourceProxyReference(proxy, element);
	assertTrue(result);
	assertEquals(0, document.getResourceProxyReferences(proxy).size());

	// Remove again
	result = document.unregisterResourceProxyReference(proxy, element);
	assertFalse(result);

	// Remove element that has never been added
	result = document.unregisterResourceProxyReference(proxy, mockContext.mock(CMDIMetadataElement.class, "anotherElement"));
	assertFalse(result);
    }

    @Test
    public void testRemoveDocumentReference() throws Exception {
	DataResourceProxy resourceProxy = document.createDocumentResourceReference(new URI("http://resource"), "test/mime-type");
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
	document.removeDocumentReference(resourceProxy);
	assertFalse(document.getDocumentReferences().contains(resourceProxy));
    }

    @Test
    public void testGetHandle() throws MetadataException {
	// Handle is read from MdSelfLink header element
	document.putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, "test:test-handle"));
	assertEquals("test:test-handle", document.getHandle());
	document.putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, null));
	assertEquals(null, document.getHandle());
    }

    @Test
    public void testSetHandle() throws MetadataException {
	document.setHandle("test:test-handle");
	// Handle is stored in MdSelfLink header element
	assertEquals("test:test-handle", document.getHeaderInformation(CMDIConstants.CMD_HEADER_MD_SELF_LINK).getValue());
    }

    @Test
    public void testGetPathString() {
	assertEquals("/:CMD/:Components/:TextCorpusProfile", document.getPathString());
    }

    /**
     * Test of addMetadataDocumentListener method, of class CMDIDocumentImpl.
     */
    @Test
    @Ignore
    public void testAddMetadataDocumentListener() {
	System.out.println("addMetadataDocumentListener");
	MetadataDocumentListener listener = null;
	CMDIDocumentImpl instance = null;
	instance.addMetadataDocumentListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of removeMetadataDocumentListener method, of class CMDIDocumentImpl.
     */
    @Test
    @Ignore
    public void testRemoveMetadataDocumentListener() {
	System.out.println("removeMetadataDocumentListener");
	MetadataDocumentListener listener = null;
	CMDIDocumentImpl instance = null;
	instance.removeMetadataDocumentListener(listener);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    /**
     * Test of getMetadataDocumentListeners method, of class CMDIDocumentImpl.
     */
    @Test
    @Ignore
    public void testGetMetadataDocumentListeners() {
	System.out.println("getMetadataDocumentListeners");
	CMDIDocumentImpl instance = null;
	Collection expResult = null;
	Collection result = instance.getMetadataDocumentListeners();
	assertEquals(expResult, result);
	// TODO review the generated test code and remove the default call to fail.
	fail("The test case is a prototype.");
    }

    @Override
    CMDIMetadataElement getInstance() {
	return document;
    }

    @Override
    CMDIDocument getDocument() {
	return document;
    }
}
