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
package nl.mpi.metadata.api;

import java.net.URI;
import java.net.URL;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.MetadataReference;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class MetadataAPITest {

    private MetadataAPI api;

    @Before
    public void setUp() {
	try {
	    api = getProvider().createAPI();
	} catch (Exception ex) {
	    throw new RuntimeException("Exception while setting up MetadataAPITest", ex);
	}
    }

    @After
    public void tearDown() {
	api = null;
    }

    /**
     * Test of getMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testGetMetadataDocument() throws Exception {
	URL url = getProvider().getDocumentURL();
	MetadataDocument expResult = getProvider().createDocument(api);
	MetadataDocument result = api.getMetadataDocument(url);
	assertEquals(expResult.getType(), result.getType());
	assertEquals(expResult.getChildren().size(), result.getChildren().size());
    }

    /**
     * Test of createMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testCreateMetadataDocument() throws Exception {
	final MetadataDocumentType documentType = getProvider().createDocumentType(api);
	MetadataDocument expResult = getProvider().createDocument(api);
	MetadataDocument result = api.createMetadataDocument(documentType);
	assertEquals(expResult.getType(), result.getType());
	assertEquals(expResult.getChildren().size(), result.getChildren().size());
    }

    /**
     * Test of validateMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testValidateMetadataDocument() throws Exception {
	MetadataDocument validDocument = getProvider().createDocument(api);
	MetadataDocument invalidDocument = getProvider().createInvalidDocument(api);
	SimpleErrorHandler errorHandler = new SimpleErrorHandler();
	api.validateMetadataDocument(validDocument, errorHandler);
	assertEquals(0, errorHandler.getErrors().size());
	assertEquals(0, errorHandler.getFatalErrors().size());
	assertEquals(0, errorHandler.getWarnings().size());

	errorHandler = new SimpleErrorHandler();
	api.validateMetadataDocument(invalidDocument, errorHandler);
	final int totalReports = errorHandler.getErrors().size() + errorHandler.getFatalErrors().size() + errorHandler.getWarnings().size();
	assertNotSame(0, totalReports);
    }

    /**
     * Test of createMetadataElement method, of class MetadataAPI.
     */
    @Test
    public void testCreateMetadataElement() throws Exception {
	MetadataContainer parentElement = getProvider().createEmptyParentElement(api, getProvider().createDocument(api));
	MetadataElementType type = getProvider().createAddableType(api);
	MetadataElement result = api.createMetadataElement(parentElement, type);
	assertNotNull(result);
	assertTrue(parentElement.getChildren().contains(result));
	assertTrue(result instanceof ContainedMetadataElement);
	assertEquals(parentElement, ((ContainedMetadataElement) result).getParent());

	try {
	    type = getProvider().createUnaddableType(api);
	    api.createMetadataElement(parentElement, type);
	    // The above line should fail because type is not addable to the parentElement
	    fail("Expected MetadataDocumentException");
	} catch (MetadataDocumentException ex) {
	    // this should happen
	}
    }

    /**
     * Test of removeMetadataElement method, of class MetadataAPI.
     */
    @Test
    public void testRemoveElement() throws Exception {
	MetadataContainer parentElement = getProvider().createEmptyParentElement(api, getProvider().createDocument(api));
	MetadataElementType type = getProvider().createAddableType(api);
	// Add an element
	MetadataElement element = api.createMetadataElement(parentElement, type);
	assertEquals(1, parentElement.getChildren().size());

	// Try to remove
	boolean result = api.removeMetadataElement(element);
	assertTrue(result);
	assertEquals(0, parentElement.getChildren().size());

	// cannot remove twice
	result = api.removeMetadataElement(element);
	assertFalse(result);
    }

    @Test
    public void testAddResources() throws Exception {
	MetadataDocument document = getProvider().createDocument(api);
	ReferencingMetadataElement element = getProvider().getReferencingMetadataElement(api, document);
	
	ResourceReference resourceReference = element.createResourceReference(new URI("http://resource/1"), "test/test-resource");
	assertNotNull(resourceReference);
	assertTrue(element.getReferences().contains(resourceReference));
	assertEquals(new URI("http://resource/1"), resourceReference.getURI());
	assertEquals("test/test-resource", resourceReference.getMimetype());
	
	MetadataReference metadataReference = element.createMetadataReference(new URI("http://metadata/1"), "test/test-metadata");
	assertNotNull(metadataReference);
	assertTrue(element.getReferences().contains(metadataReference));
	assertEquals(new URI("http://metadata/1"), metadataReference.getURI());
	assertEquals("test/test-metadata", metadataReference.getMimetype());
	
    }

    protected abstract MetadataAPITestProvider getProvider();

    protected interface MetadataAPITestProvider<A extends MetadataAPI> {

	MetadataAPI createAPI() throws Exception;

	MetadataDocumentType createDocumentType(A api) throws Exception;

	MetadataDocument createDocument(A api) throws Exception;

	MetadataDocument createInvalidDocument(A api) throws Exception;

	MetadataContainer createEmptyParentElement(A api, MetadataDocument document) throws Exception;
	
	MetadataElementType createAddableType(A api) throws Exception;

	MetadataElementType createUnaddableType(A api) throws Exception;

	ReferencingMetadataElement getReferencingMetadataElement(A api, MetadataDocument document);

	URL getDocumentURL() throws Exception;
    }
}
