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

import java.net.URL;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class MetadataAPITest {

    public MetadataAPITest() {
    }
    private MetadataAPI api;

    @Before
    public void setUp() {
	try {
	    api = createAPI();
	} catch (Exception ex) {
	    throw new RuntimeException("Exception while setting up MetadataAPITest", ex);
	}
    }

    @After
    public void tearDown() {
	api = null;
    }

    protected MetadataAPI getAPI() {
	return api;
    }

    protected abstract MetadataAPI createAPI() throws Exception;

    protected abstract MetadataDocumentType createDocumentType() throws Exception;

    protected abstract MetadataDocument createDocument() throws Exception;

    protected abstract MetadataDocument createInvalidDocument() throws Exception;

    protected abstract MetadataContainer createEmptyParentElement(MetadataDocument document) throws Exception;

    protected abstract MetadataElementType createAddableType() throws Exception;

    protected abstract URL getDocumentURL() throws Exception;

    /**
     * Test of getMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testGetMetadataDocument() throws Exception {
	URL url = getDocumentURL();
	MetadataDocument expResult = createDocument();
	MetadataDocument result = api.getMetadataDocument(url);
	assertEquals(expResult.getType(), result.getType());
	assertEquals(expResult.getChildren().size(), result.getChildren().size());
    }

    /**
     * Test of createMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testCreateMetadataDocument() throws Exception {
	final MetadataDocumentType documentType = createDocumentType();
	MetadataDocument expResult = createDocument();
	MetadataDocument result = api.createMetadataDocument(documentType);
	assertEquals(expResult.getType(), result.getType());
	assertEquals(expResult.getChildren().size(), result.getChildren().size());
    }

    /**
     * Test of validateMetadataDocument method, of class MetadataAPI.
     */
    @Test
    public void testValidateMetadataDocument() throws Exception {
	MetadataDocument validDocument = createDocument();
	MetadataDocument invalidDocument = createInvalidDocument();
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
    @Ignore
    public void testCreateMetadataElement() throws Exception {
	MetadataContainer parentElement = createEmptyParentElement(createDocument());
	MetadataElementType type = createAddableType();
	MetadataElement result = api.createMetadataElement(parentElement, type);
	assertNotNull(result);
    }

    /**
     * Test of removeElement method, of class MetadataAPI.
     */
    @Test
    @Ignore
    public void testRemoveElement() throws Exception {
	MetadataContainer parentElement = createEmptyParentElement(createDocument());
	MetadataElementType type = createAddableType();
	// Add an element
	MetadataElement element = api.createMetadataElement(parentElement, type);
	assertEquals(1, parentElement.getChildren().size());

	// Try to remove
	boolean result = api.removeElement(element);
	assertTrue(result);
	assertEquals(0, parentElement.getChildren().size());

	// cannot remove twice
	result = api.removeElement(element);
	assertFalse(result);
    }
}
