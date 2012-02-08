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

import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentReaderTest extends CMDIAPITestCase {

    private CMDIDocumentReader reader;
    private CMDIProfileContainer profileContainer;

    @Before
    public void setUp() {
	profileContainer = new CMDIProfileContainer();
	reader = new CMDIDocumentReader(profileContainer);
    }

    @After
    public void tearDown() {
	profileContainer = null;
	reader = null;
    }

    /**
     * Test of read method, of class CMDIDocumentReader.
     */
    @Test
    public void testRead() throws Exception {
	Document dom = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
	CMDIDocument cmdi = reader.read(dom);
	assertNotNull(cmdi);
	assertEquals("http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438164/xsd", cmdi.getType().getSchemaLocation().toString());
    }
}
