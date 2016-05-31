/*
 * Copyright (C) 2016 Max Planck Institute for Psycholinguistics
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

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class LatSessionTest extends CMDIAPITestCase {
    
    private final URL DOC_URL = getClass().getResource("/cmdi/lat-session-instance.cmdi");
    private CMDIApi api;
    
    @Before
    public void setUp() {
        api = new CMDIApi();
    }
    
    @Test
    public void testRemoveWrittenResource() throws Exception {
        CMDIDocument metadataDocument = api.getMetadataDocument(DOC_URL);

        //case to test: removing one of Resources/WrittenResource should not alter order
        assertThat(metadataDocument.getChildElement("Resources"), instanceOf(MetadataContainer.class));
        MetadataContainer resources = (MetadataContainer) metadataDocument.getChildElement("Resources");
        
        assertThat(resources.getChildElement("WrittenResource[1]"), instanceOf(ContainedMetadataElement.class));
        ContainedMetadataElement writtenResource = (ContainedMetadataElement) resources.getChildElement("WrittenResource[1]");
        //test order of children
        
        testChildOrder(metadataDocument);

        //remove one of the children of 'Resources'
        assertEquals("9 resources expected from original file", 9, resources.getChildrenCount());
        writtenResource.getParent().removeChildElement(writtenResource);
        assertEquals("Removal should reduce child count", 8, resources.getChildrenCount());
        
        //order of children at root level should not have changed
        testChildOrder(metadataDocument);
        
        //write document to file
        File outFile = File.createTempFile("lat-session", ".cmdi");
        StreamResult result = new StreamResult(outFile);
        api.writeMetadataDocument(metadataDocument, result);
        
        //reload
        metadataDocument = api.getMetadataDocument(outFile.toURI().toURL());
        
        //order of children at root level should not have changed
        testChildOrder(metadataDocument);
    }
    
    protected void testChildOrder(CMDIDocument metadataDocument) {
        List<MetadataElement> docChildren = metadataDocument.getChildren();
        assertEquals("Resources", docChildren.get(10).getName());
        assertEquals("References", docChildren.get(11).getName());
    }
    
}
