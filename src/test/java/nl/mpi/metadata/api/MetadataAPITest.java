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

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.MetadataElementAttribute;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
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
	assertEquals(Arrays.toString(errorHandler.getErrors().toArray()), 0, errorHandler.getErrors().size());
	assertEquals(Arrays.toString(errorHandler.getFatalErrors().toArray()), 0, errorHandler.getFatalErrors().size());
	assertEquals(Arrays.toString(errorHandler.getWarnings().toArray()), 0, errorHandler.getWarnings().size());

	errorHandler = new SimpleErrorHandler();
	api.validateMetadataDocument(invalidDocument, errorHandler);
	final int totalReports = errorHandler.getErrors().size() + errorHandler.getFatalErrors().size() + errorHandler.getWarnings().size();
	assertNotSame(0, totalReports);
    }

    @Test
    public void testGetDocumentType() throws Exception {
	URI schemaURI = getProvider().getDocumentTypeURI();
	MetadataDocumentType documentType = api.getMetadataDocumentType(schemaURI);
	assertNotNull(documentType);
	assertEquals(documentType, getProvider().createDocumentType(api));
    }

    @Test
    public void testWriteDocument() throws Exception {
	MetadataDocument validDocument = getProvider().createDocument(api);
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	StreamResult result = new StreamResult(outputStream);
	assertEquals(0, outputStream.toByteArray().length);
	api.writeMetadataDocument(validDocument, result);
	assertTrue(outputStream.toByteArray().length > 0);
    }

    @Test
    public void testInsertMetadataElement() throws Exception {
	MetadataDocument validDocument = getProvider().createDocument(api);
	MetadataContainer parentElement = getProvider().createEmptyParentElement(api, validDocument);
	MetadataElementType addableType = getProvider().createAddableType(api);
	MetadataElement insertedElement = api.insertMetadataElement(parentElement, addableType);
	assertNotNull(insertedElement);
	MetadataElementType unaddableType = getProvider().createUnaddableType(api);
	try {
	    api.insertMetadataElement(parentElement, unaddableType);
	    fail("insertion of unaddable type should lead to MetadataElementException");
	} catch (MetadataElementException mdeEx) {
	    // This should occur
	}
    }

    @Test
    public void testInsertAttribute() throws Exception {
	MetadataDocument validDocument = getProvider().createDocument(api);
	MetadataElementAttributeContainer parentElement = getProvider().createAttributeParent(api, validDocument);
	MetadataElementAttributeType addableType = getProvider().createAddableAttributeType(api);
	MetadataElementAttribute insertedElement = api.insertAttribute(parentElement, addableType);
	assertNotNull(insertedElement);
	MetadataElementAttributeType unaddableType = getProvider().createUnaddableAttributeType(api);
	try {
	    api.insertAttribute(parentElement, unaddableType);
	    fail("insertion of unaddable type should lead to MetadataElementException");
	} catch (MetadataElementException mdeEx) {
	    // This should occur
	}
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

	MetadataElementAttributeContainer createAttributeParent(A api, MetadataDocument document) throws Exception;

	MetadataElementAttributeType createAddableAttributeType(A api) throws Exception;

	MetadataElementAttributeType createUnaddableAttributeType(A api) throws Exception;

	ReferencingMetadataElement getReferencingMetadataElement(A api, MetadataDocument document);

	URL getDocumentURL() throws Exception;

	URI getDocumentTypeURI() throws Exception;
    }
}
