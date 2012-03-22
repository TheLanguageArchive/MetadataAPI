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
package nl.mpi.metadata.cmdi.api.dom;

import java.net.URI;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import org.apache.xpath.CachedXPathAPI;
import static org.junit.Assert.*;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIResourceProxyReaderTest extends CMDIAPITestCase {

    /**
     * Test of readResourceProxies method, of class CMDIResourceProxyReader.
     */
    @Test
    public void testReadResourceProxies() throws Exception {
	CMDIDocument cmdiDocument = new CMDIDocument(getNewTestProfileAndRead());
	Document domDocument = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
	CMDIResourceProxyReader instance = new CMDIResourceProxyReader();
	instance.readResourceProxies(cmdiDocument, domDocument, new CachedXPathAPI());

	assertEquals(3, cmdiDocument.getDocumentResourceProxies().size());

	ResourceProxy resource1 = cmdiDocument.getDocumentResourceProxy("resource1");
	assertTrue("Resource should be data resource proxy", resource1 instanceof DataResourceProxy);
	assertEquals("text/plain", resource1.getMimetype());
	assertEquals(new URI("http://resources/1"), resource1.getURI());

	ResourceProxy resource2 = cmdiDocument.getDocumentResourceProxy("resource2");
	assertTrue("Resource should be data resource proxy", resource2 instanceof DataResourceProxy);
	assertNull("Resource should have no mimetype", resource2.getMimetype());
	assertEquals(new URI("http://resources/2"), resource2.getURI());

	ResourceProxy metadata1 = cmdiDocument.getDocumentResourceProxy("metadata1");
	assertTrue("Resource should be metadata resource proxy", metadata1 instanceof MetadataResourceProxy);
	assertEquals("application/xml", metadata1.getMimetype());
	assertEquals(new URI("http://metadata/1"), metadata1.getURI());
    }
}
