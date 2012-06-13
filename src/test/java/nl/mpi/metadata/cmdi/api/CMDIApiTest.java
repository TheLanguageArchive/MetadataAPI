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
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentWriter;
import nl.mpi.metadata.cmdi.api.dom.MockCMDIDocumentReader;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.MockCMDIProfileReader;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import nl.mpi.metadata.cmdi.api.validation.MockCMDIValidator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Use JMock to mock readers, profile container, maybe element factory,
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApiTest extends CMDIAPITestCase {

    private CMDIApi api;
    private MockCMDIDocumentReader documentReader;
    private MockCMDIProfileReader profileReader;
    private CMDIDocumentWriter documentWriter;
    private CMDIDocument testDocument;
    private CMDIProfile profile;

    @Before
    public void setUp() throws Exception {
	testDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY);
	profile = getNewTestProfileAndRead();
	documentReader = new MockCMDIDocumentReader(testDocument);
	profileReader = new MockCMDIProfileReader(profile);
	api = new CMDIApi(documentReader, documentWriter, profileReader, new DefaultCMDIValidator(), CMDI_API_TEST_ENTITY_RESOLVER);
    }

    /**
     * Test of getDocumentReader method, of class CMDIApi.
     */
    @Test
    public void testGetDocumentReader() {
	assertEquals(documentReader, api.getDocumentReader());
    }

    @Test
    public void testGetProfileReader() {
	assertEquals(profileReader, api.getProfileReader());
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
	MockCMDIValidator mockCMDIValidator = new MockCMDIValidator(warnings, errors, fatalErrors);
	api = new CMDIApi(documentReader, documentWriter, profileReader, mockCMDIValidator);
	// Validate using this mock handler (against simple handler)
	api.validateMetadataDocument(testDocument = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY), errorHandler);
	// Check if numbers match
	assertEquals(warnings, errorHandler.getWarnings().size());
	assertEquals(errors, errorHandler.getErrors().size());
	assertEquals(fatalErrors, errorHandler.getFatalErrors().size());
    }
    //TODO Test constructors
}
