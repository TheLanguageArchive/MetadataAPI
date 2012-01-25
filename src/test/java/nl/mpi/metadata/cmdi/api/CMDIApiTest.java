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

import java.net.URL;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.junit.After;
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
    }

    @After
    public void tearDown() {
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
	CMDIDocument document = api.getMetadataDocument(new URL("http://google.com"));
	// Mock reader will simply return the document passed in the constructor
	assertEquals(documentReader.getDocument(), document);
	// It will also store the input stream
	assertNotNull(documentReader.getInputStream());
    }

    /**
     * Test of createMetadataDocument method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testCreateMetadataDocument() {
    }

    /**
     * Test of createMetadataElement method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testCreateMetadataElement() {
    }

    /**
     * Test of validateMetadataDocument method, of class CMDIApi.
     */
    @Test
    @Ignore
    public void testValidateMetadataDocument() {
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
