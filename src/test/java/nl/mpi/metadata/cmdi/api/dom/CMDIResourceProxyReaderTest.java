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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import org.apache.xpath.CachedXPathAPI;
import org.custommonkey.xmlunit.XMLUnit;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIResourceProxyReaderTest extends CMDIAPITestCase {

    CMDIResourceProxyReader instance;

    @Before
    public void setUp() {
	instance = new CMDIResourceProxyReader();
    }

    /**
     * Test of readResourceProxies method, of class CMDIResourceProxyReader.
     */
    @Test
    public void testReadResourceProxies() throws Exception {
	CMDIDocument cmdiDocument = new CMDIDocument(getNewTestProfileAndRead());
	Document domDocument = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
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

    @Test
    public void testCreateResourceProxy() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType mimetype=\"text/plain\">Resource</ResourceType>"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	ResourceProxy resourceProxy = instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	assertEquals("resource1", resourceProxy.getId());
	assertEquals("text/plain", resourceProxy.getMimetype());
	assertEquals(new URI("http://resources/1"), resourceProxy.getURI());
    }

    @Test
    public void testCreateResourceProxyNoResourceType() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	    fail("Should fail because no resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("Encountered resource proxy without ResourceType"));
	}
    }

    @Test
    public void testCreateResourceProxyInvalidType() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType>InvalidType</ResourceType>"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	    fail("Should fail because of invalid resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("Unknown ResourceType"));
	    assertTrue(mEx.getMessage().contains("InvalidType"));
	}
    }

    @Test
    public void testCreateResourceProxyNoId() throws Exception {
	String xml = "<ResourceProxy>"
		+ "<ResourceType>Resource</ResourceType>"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	    fail("Should fail because no id");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("Encountered resource proxy without id"));
	}
    }

    @Test
    public void testCreateResourceProxyNoResourceRef() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType>Resource</ResourceType>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	    fail("Should fail because no resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("Encountered resource proxy without ResourceRef"));
	}
    }

    @Test
    public void testCreateResourceProxyInvalidURI() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType>Resource</ResourceType>"
		+ "<ResourceRef>http:/\\</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, new CachedXPathAPI());
	    fail("Should fail because no resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("URI syntax exception"));
	    assertEquals(URISyntaxException.class, mEx.getCause().getClass());
	}
    }

    private Node getResourceProxyNode(CharSequence resourceProxyXML) throws SAXException, IOException, TransformerException {
	StringBuilder xmlBuilder = new StringBuilder();
	xmlBuilder.append("<CMD xmlns=\"http://www.clarin.eu/cmd/\"");
	xmlBuilder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
	xmlBuilder.append(" CMDVersion=\"1.1\"");
	xmlBuilder.append(" xsi:schemaLocation=\"http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438164/xsd \">");
	xmlBuilder.append(" <Resources>");
	xmlBuilder.append("	<ResourceProxyList>");
	xmlBuilder.append(resourceProxyXML);
	xmlBuilder.append("	</ResourceProxyList>");
	xmlBuilder.append(" </Resources>");
	xmlBuilder.append("</CMD>");
	Document document = XMLUnit.buildTestDocument(xmlBuilder.toString());
	return new CachedXPathAPI().selectSingleNode(document, "/:CMD/:Resources/:ResourceProxyList/*");
    }
}
