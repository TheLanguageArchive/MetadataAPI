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
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class LatSessionTest extends CMDIAPITestCase {

    private final URL DOC_URL = getClass().getResource("/cmdi/lat-session-instance.cmdi");
    private CMDIApi api;
    private static DocumentBuilderFactory dbFactory;

    @BeforeClass
    public static void setupClass() {
        dbFactory = DocumentBuilderFactory.newInstance();
    }

    @Before
    public void setup() {
        api = new CMDIApi();
    }

    @Test
    public void testInsertResources() throws Exception {
        //case to test: correct element order after insertion of elements (MediaFile/WrittenResource) in arbitrary order

        CMDIDocument metadataDocument = api.getMetadataDocument(DOC_URL);

        //verify that original resource is fine
        testResourcesOrder("Original file", saveDoc(metadataDocument));

        assertThat(metadataDocument.getChildElement("Resources"), instanceOf(Component.class));
        Component resources = (Component) metadataDocument.getChildElement("Resources");

        CMDIProfileElement wrType = resources.getType().getType("WrittenResource");
        assertNotNull(wrType);
        assertTrue(resources.canAddInstanceOfType(wrType));

        CMDIProfileElement mfType = resources.getType().getType("MediaFile");
        assertNotNull(mfType);
        assertTrue(resources.canAddInstanceOfType(mfType));

        //insert media file
        Component mf1 = (Component) api.insertMetadataElement(resources, mfType);
        //insert written resource
        Component wr1 = (Component) api.insertMetadataElement(resources, wrType);
        //insert media file
        Component mf2 = (Component) api.insertMetadataElement(resources, mfType);
        //insert written resource
        Component wr2 = (Component) api.insertMetadataElement(resources, wrType);

        List<MetadataElement> resourceChildren = resources.getChildren();
        assertTrue(resourceChildren.contains(mf1)
                && resourceChildren.contains(mf2)
                && resourceChildren.contains(wr1)
                && resourceChildren.contains(wr2));

        //check dom
        File outFile = saveDoc(metadataDocument);
        testResourcesOrder("After insertion of resource children", outFile);
    }

    @Test
    public void testRemoveWrittenResource() throws Exception {
        //case to test: removing one of Resources/WrittenResource should not alter order

        CMDIDocument metadataDocument = api.getMetadataDocument(DOC_URL);
        //test order of children
        testRootChildOrder(metadataDocument);
        //also on serialised version
        testRootChildOrder(saveDoc(metadataDocument));

        //get and check children
        assertThat(metadataDocument.getChildElement("Resources"), instanceOf(MetadataContainer.class));
        MetadataContainer resources = (MetadataContainer) metadataDocument.getChildElement("Resources");
        assertThat(resources.getChildElement("WrittenResource[1]"), instanceOf(ContainedMetadataElement.class));
        ContainedMetadataElement writtenResource = (ContainedMetadataElement) resources.getChildElement("WrittenResource[1]");

        //now time for some manipulations...
        //remove one of the children of 'Resources'
        assertEquals("2 resources expected from original file", 2, resources.getChildrenCount());
        writtenResource.getParent().removeChildElement(writtenResource);
        assertEquals("Removal should reduce child count", 1, resources.getChildrenCount());

        //order of children at root level should not have changed
        testRootChildOrder(metadataDocument);
        //save and reload
        File outFile = saveDoc(metadataDocument);
        metadataDocument = api.getMetadataDocument(outFile.toURI().toURL());
        //order of children at root level should not have changed
        testRootChildOrder(metadataDocument);
        testRootChildOrder(outFile);

        //remove remaining resource
        assertEquals("1 resources expected from altered document", 1, resources.getChildrenCount());
        writtenResource = (ContainedMetadataElement) resources.getChildElement("WrittenResource[1]");
        writtenResource.getParent().removeChildElement(writtenResource);
        assertEquals("Removal should reduce child count", 0, resources.getChildrenCount());

        //order of children at root level should not have changed
        testRootChildOrder(metadataDocument);

        //save and reload
        outFile = saveDoc(metadataDocument);
        metadataDocument = api.getMetadataDocument(outFile.toURI().toURL());
        //order of children at root level should not have changed
        testRootChildOrder(metadataDocument);
        testRootChildOrder(outFile);
    }

    protected void testRootChildOrder(CMDIDocument metadataDocument) {
        final List<MetadataElement> docChildren = metadataDocument.getChildren();
        assertEquals("Resources", docChildren.get(10).getName());
        assertEquals("References", docChildren.get(11).getName());
    }

    private void testRootChildOrder(File outFile) throws Exception {
        final Document document = dbFactory.newDocumentBuilder().parse(outFile);
        final Element sessionElement = (Element) document.getElementsByTagName("lat-session").item(0);
        final Node resourcesNode = sessionElement.getElementsByTagName("Resources").item(0);
        final Node referencesNode = sessionElement.getElementsByTagName("References").item(0);
        assertEquals("<Resources> should appear before <References> in XML",
                Node.DOCUMENT_POSITION_PRECEDING, referencesNode.compareDocumentPosition(resourcesNode));
    }

    private void testResourcesOrder(String message, File outFile) throws Exception {
        final Document document = dbFactory.newDocumentBuilder().parse(outFile);
        final Element sessionElement = (Element) document.getElementsByTagName("lat-session").item(0);
        final Element resourcesElement = (Element) sessionElement.getElementsByTagName("Resources").item(0);
        final NodeList mediaFiles = resourcesElement.getElementsByTagName("MediaFile");
        final NodeList writtenResources = resourcesElement.getElementsByTagName("WrittenResource");
        //test: all media files should come before all written resource files
        for (int m = 0; m < mediaFiles.getLength(); m++) {
            for (int w = 0; w < writtenResources.getLength(); w++) {
                final Node mf = mediaFiles.item(m);
                final Node wr = writtenResources.item(w);
                if (Node.DOCUMENT_POSITION_PRECEDING != wr.compareDocumentPosition(mf)) {
                    fail(String.format("%s: node %s (#%d) should preceed node %s (#%d)", message, mf, m, wr, w));
                }
            }
        }
    }

    protected File saveDoc(CMDIDocument metadataDocument) throws IOException, MetadataException, TransformerException {
        final File outFile = File.createTempFile("lat-session", ".cmdi");
        outFile.deleteOnExit();
        final StreamResult result = new StreamResult(outFile);
        api.writeMetadataDocument(metadataDocument, result);
        return outFile;
    }
}
