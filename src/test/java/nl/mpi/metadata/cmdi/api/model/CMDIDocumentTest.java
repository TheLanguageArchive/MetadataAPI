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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentTest extends CMDIMetadataElementTest {

    public CMDIDocumentTest() {
    }
    private CMDIDocument document;
    private CMDIProfile profile;

    @Before
    public void setUp() throws Exception {
	profile = getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
	document = new CMDIDocument(profile, testSchemaTextCorpus.toURI());
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
     * Test of getHeaderInformation method, of class CMDIDocument.
     */
    @Test
    public void testGetHeaderInformation() {
	assertEquals(0, document.getHeaderInformation().size());
	assertNull(document.getHeaderInformation("Key"));
	assertNull(document.getHeaderInformation("OtherKey"));
	document.putHeaderInformation(new HeaderInfo("Key", "Value"));
	document.getHeaderInformation();
	assertEquals(1, document.getHeaderInformation().size());
	assertNotNull(document.getHeaderInformation("Key"));
	assertSame(document.getHeaderInformation("Key"), document.getHeaderInformation().iterator().next());
	assertEquals("Value", document.getHeaderInformation("Key").getValue());
	assertNull(document.getHeaderInformation("OtherKey"));
	document.removeHeaderInformation("OtherKey");
	assertEquals(1, document.getHeaderInformation().size());
	document.removeHeaderInformation("Key");
	assertEquals(0, document.getHeaderInformation().size());
	assertNull(document.getHeaderInformation("Key"));
    }

    @Test
    public void testGetDocumentResourceProxy() throws URISyntaxException {
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
    public void testAddDocumentResourceProxy() throws URISyntaxException {
	assertEquals(0, document.getDocumentReferences().size());
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(1, document.getDocumentReferences().size());
	assertEquals(resourceProxy, document.getDocumentReferences().iterator().next());
    }

    @Test
    public void testRemoveDocumentResourceProxy() throws URISyntaxException {
	DataResourceProxy resourceProxy = new DataResourceProxy("rpId", new URI("http://resource"), "test/mime-type");
	document.addDocumentResourceProxy(resourceProxy);
	assertEquals(1, document.getDocumentReferences().size());
	document.removeDocumentResourceProxy("rpId");
	assertEquals(0, document.getDocumentReferences().size());
    }

    @Test
    public void testCreateDocumentResourceProxy() throws URISyntaxException {
	assertEquals(0, document.getDocumentReferences().size());
	DataResourceProxy resourceProxy = document.createDocumentResourceReference(new URI("http://resource"), "test/mime-type");
	assertEquals(1, document.getDocumentReferences().size());
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
    }

    @Test
    public void testCreateDocumentMetadataResourceProxy() throws URISyntaxException {
	assertEquals(0, document.getDocumentReferences().size());
	MetadataResourceProxy resourceProxy = document.createDocumentMetadataReference(new URI("http://resource"), "test/mime-type");
	assertEquals(1, document.getDocumentReferences().size());
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
    }

    @Test
    public void testRemoveDocumentReference() throws URISyntaxException {
	DataResourceProxy resourceProxy = document.createDocumentResourceReference(new URI("http://resource"), "test/mime-type");
	assertTrue(document.getDocumentReferences().contains(resourceProxy));
	document.removeDocumentReference(resourceProxy);
	assertFalse(document.getDocumentReferences().contains(resourceProxy));
    }

    @Test
    public void testGetHandle() {
	// Handle is read from MdSelfLink header element
	document.putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, "test:test-handle"));
	assertEquals("test:test-handle", document.getHandle());
	document.putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, null));
	assertEquals(null, document.getHandle());
    }

    @Test
    public void testSetHandle() {
	document.setHandle("test:test-handle");
	// Handle is stored in MdSelfLink header element
	assertEquals("test:test-handle", document.getHeaderInformation(CMDIConstants.CMD_HEADER_MD_SELF_LINK).getValue());
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

    @Override
    CMDIMetadataElement getInstance() {
	return document;
    }

    @Override
    CMDIDocument getDocument() {
	return document;
    }
}
