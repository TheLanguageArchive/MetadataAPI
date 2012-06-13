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
package nl.mpi.metadata.cmdi.api.validation;

import nl.mpi.metadata.api.SimpleErrorHandler;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DefaultCMDIValidatorTest extends CMDIAPITestCase {

    private DefaultCMDIValidator validator;

    @Before
    public void setUp() {
	validator = new DefaultCMDIValidator();
    }

    /**
     * Test of validateMetadataDocument method, of class CMDIApi.
     */
    @Test
    public void testValidateValidMetadataDocument() throws Exception {
	CMDIDocument document = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY, testSchemaSmall.toURI(), "/cmdi/SmallTestProfile-instance-valid.cmdi", SMALL_PROFILE_ROOT_NODE_PATH);
	SimpleErrorHandler errorHandler = new SimpleErrorHandler();
	validator.validateMetadataDocument(document, errorHandler);
	assertEquals(0, errorHandler.getWarnings().size());
	assertEquals(0, errorHandler.getFatalErrors().size());
	assertEquals(0, errorHandler.getErrors().size());
    }

    /**
     * Test of validateMetadataDocument method, of class CMDIApi.
     */
    @Test
    public void testValidateInvalidMetadataDocument() throws Exception {
	CMDIDocument document = getNewTestDocument(CMDI_METADATA_ELEMENT_FACTORY, testSchemaSmall.toURI(), "/cmdi/SmallTestProfile-instance-invalid.cmdi", SMALL_PROFILE_ROOT_NODE_PATH);
	SimpleErrorHandler errorHandler = new SimpleErrorHandler();
	validator.validateMetadataDocument(document, errorHandler);
	assertEquals(0, errorHandler.getWarnings().size());
	assertEquals(0, errorHandler.getFatalErrors().size());
	assertEquals(2, errorHandler.getErrors().size());
    }
}
