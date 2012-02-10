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
package nl.mpi.metadata.cmdi.api;

import nl.mpi.metadata.api.SimpleErrorHandler;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.validation.MockCMDIValidator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApiTest extends CMDIAPITestCase {

    private CMDIApi api;
    private MockCMDIDocumentReader documentReader;

    @Before
    public void setUp() throws Exception {
	CMDIDocument testDocument = getNewTestDocument();
	documentReader = new MockCMDIDocumentReader(testDocument);
	api = new CMDIApi(documentReader);
	api.setEntityResolver(CMDI_API_TEST_ENTITY_RESOLVER);
    }

    /**
     * Test of getDocumentReader method, of class CMDIApi.
     */
    @Test
    public void testGetDocumentReader() {
	assertEquals(documentReader, api.getDocumentReader());
    }

    /**
     * Test of getMetadataDocument method, of class CMDIApi.
     */
    @Test
    public void testGetMetadataDocument() throws Exception {
	CMDIDocument document = api.getMetadataDocument(testSchemaTextCorpus);
	// Mock reader will simply return the document passed in the constructor
	assertEquals(documentReader.getCmdiDocument(), document);
	// It will also store the input document
	assertNotNull(documentReader.getDocument());
    }

    /**
     * Test of validateMetadataDocument method, of class CMDIApi.
     */
    @Test
    public void testValidateValidMetadataDocument() throws Exception {
	// Check several combinations of produced/found warnings, errors, fatal errors
	testValidateValidMetadataDocument(0, 0, 0);
	testValidateValidMetadataDocument(0, 0, 1);
	testValidateValidMetadataDocument(0, 1, 1);
	testValidateValidMetadataDocument(1, 0, 0);
	testValidateValidMetadataDocument(1, 0, 1);
	testValidateValidMetadataDocument(1, 1, 1);
    }

    private void testValidateValidMetadataDocument(int warnings, int errors, int fatalErrors) throws Exception {
	// Simple error handler that just collects errors
	SimpleErrorHandler errorHandler = new SimpleErrorHandler();
	// Set a mock validator that produces the number of errors as specified
	api.setCmdiValidator(new MockCMDIValidator(warnings, errors, fatalErrors));
	// Validate using this mock handler (against simple handler)
	api.validateMetadataDocument(getNewTestDocument(), errorHandler);
	// Check if numbers match
	assertEquals(warnings, errorHandler.getWarnings().size());
	assertEquals(errors, errorHandler.getErrors().size());
	assertEquals(fatalErrors, errorHandler.getFatalErrors().size());
    }

    /**
     * Test of createMetadataDocument method, of class CMDIApi.
     */
    @Test
    public void testCreateMetadataDocument() throws Exception {
	CMDIProfile profile = getNewTestProfileAndRead(testSchemaSmall.toURI());
	CMDIDocument document = api.createMetadataDocument(profile);
	assertNotNull(document);
    }

    /**
     * Test of createMetadataElement method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testCreateMetadataElement() {
    }

    /**
     * Test of insertElement method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testInsertElement() throws Exception {
    }

    /**
     * Test of removeElement method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testRemoveElement() throws Exception {
    }
}
