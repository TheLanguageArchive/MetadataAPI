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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class LatSessionTest extends CMDIAPITestCase {

    private final URL DOC_URL = getClass().getResource("/cmdi/lat-session-instance.cmdi");
    private CMDIApi api;
    private DocumentBuilderFactory dbFactory;

    @Before
    public void setUp() {
        api = new CMDIApi();
        dbFactory = DocumentBuilderFactory.newInstance();
    }

    @Test
    public void testRemoveWrittenResource() throws Exception {
        //case to test: removing one of Resources/WrittenResource should not alter order

        CMDIDocument metadataDocument = api.getMetadataDocument(DOC_URL);

        //get and check children
        assertThat(metadataDocument.getChildElement("Resources"), instanceOf(MetadataContainer.class));
        MetadataContainer resources = (MetadataContainer) metadataDocument.getChildElement("Resources");
        assertThat(resources.getChildElement("WrittenResource[1]"), instanceOf(ContainedMetadataElement.class));
        ContainedMetadataElement writtenResource = (ContainedMetadataElement) resources.getChildElement("WrittenResource[1]");

        //test order of children
        testChildOrder(metadataDocument);

        //remove one of the children of 'Resources'
        assertEquals("2 resources expected from original file", 2, resources.getChildrenCount());
        writtenResource.getParent().removeChildElement(writtenResource);
        assertEquals("Removal should reduce child count", 1, resources.getChildrenCount());

        //order of children at root level should not have changed
        testChildOrder(metadataDocument);
        //save and reload
        File outFile = saveDoc(metadataDocument);
        metadataDocument = api.getMetadataDocument(outFile.toURI().toURL());
        //order of children at root level should not have changed
        testChildOrder(metadataDocument);
        testChildOrder(outFile);

        //remove remaining resource
        assertEquals("1 resources expected from altered document", 1, resources.getChildrenCount());
        writtenResource = (ContainedMetadataElement) resources.getChildElement("WrittenResource[1]");
        writtenResource.getParent().removeChildElement(writtenResource);
        assertEquals("Removal should reduce child count", 0, resources.getChildrenCount());

        //order of children at root level should not have changed
        testChildOrder(metadataDocument);

        //save and reload
        outFile = saveDoc(metadataDocument);
        metadataDocument = api.getMetadataDocument(outFile.toURI().toURL());
        //order of children at root level should not have changed
        testChildOrder(metadataDocument);
        testChildOrder(outFile);
    }

    protected void testChildOrder(CMDIDocument metadataDocument) {
        final List<MetadataElement> docChildren = metadataDocument.getChildren();
        assertEquals("Resources", docChildren.get(10).getName());
        assertEquals("References", docChildren.get(11).getName());
    }

    private void testChildOrder(File outFile) throws ParserConfigurationException, SAXException, IOException {
        final Document document = dbFactory.newDocumentBuilder().parse(outFile);
        final Element sessionElement = (Element) document.getElementsByTagName("lat-session").item(0);
        final Node resourcesNode = sessionElement.getElementsByTagName("Resources").item(0);
        final Node referencesNode = sessionElement.getElementsByTagName("References").item(0);
        assertThat("<Resources> should appear before <References> in XML",
                Integer.valueOf(resourcesNode.compareDocumentPosition(referencesNode)), lessThan(0));
    }

    protected CMDIDocument saveAndReload(CMDIDocument metadataDocument) throws TransformerException, MetadataException, IOException {
        //save and reload
        final File outFile = saveDoc(metadataDocument);
        return api.getMetadataDocument(outFile.toURI().toURL());
    }

    protected File saveDoc(CMDIDocument metadataDocument) throws IOException, MetadataException, TransformerException {
        final File outFile = File.createTempFile("lat-session", ".cmdi");
        outFile.deleteOnExit();
        final StreamResult result = new StreamResult(outFile);
        api.writeMetadataDocument(metadataDocument, result);
        return outFile;
    }
}
