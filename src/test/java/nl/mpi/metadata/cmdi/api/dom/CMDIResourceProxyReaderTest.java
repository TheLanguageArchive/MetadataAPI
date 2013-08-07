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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIDocumentImpl;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

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
	CMDIDocumentImpl cmdiDocument = new CMDIDocumentImpl(getNewTestProfileAndRead());
	Document domDocument = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
	instance.readResourceProxies(cmdiDocument, domDocument, newXPath());

	assertEquals(5, cmdiDocument.getDocumentReferences().size());
	{
	    ResourceProxy resource1 = cmdiDocument.getDocumentResourceProxy("resource1");
	    assertNotNull(resource1);
	    assertEquals(DataResourceProxy.class, resource1.getClass());
	    assertEquals("text/plain", resource1.getMimetype());
	    assertEquals(new URI("http://resources/1"), resource1.getURI());
	}
	{
	    ResourceProxy resource2 = cmdiDocument.getDocumentResourceProxy("resource2");
	    assertNotNull(resource2);
	    assertEquals(DataResourceProxy.class, resource2.getClass());
	    assertNull("Resource should have no mimetype", resource2.getMimetype());
	    assertEquals(new URI("http://resources/2"), resource2.getURI());
	}
	{
	    ResourceProxy metadata1 = cmdiDocument.getDocumentResourceProxy("metadata1");
	    assertNotNull(metadata1);
	    assertEquals(MetadataResourceProxy.class, metadata1.getClass());
	    assertEquals("application/xml", metadata1.getMimetype());
	    assertEquals(new URI("http://metadata/1"), metadata1.getURI());
	}
	{
	    ResourceProxy searchPage = cmdiDocument.getDocumentResourceProxy("searchPage1");
	    assertNotNull(searchPage);
	    assertEquals(DataResourceProxy.class, searchPage.getClass());
	    assertNull(searchPage.getMimetype());
	    assertEquals(new URI("http://www.google.com"), searchPage.getURI());
	}
	{
	    ResourceProxy searchService = cmdiDocument.getDocumentResourceProxy("searchService1");
	    assertNotNull(searchService);
	    assertEquals(DataResourceProxy.class, searchService.getClass());
	    assertNull(searchService.getMimetype());
	    assertEquals(new URI("http://cqlservlet.mpi.nl"), searchService.getURI());
	}
    }

    @Test
    public void testCreateResourceProxy() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType mimetype=\"text/plain\">Resource</ResourceType>"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	ResourceProxy resourceProxy = instance.createResourceProxy(resourceProxyNode, newXPath());
	assertTrue(resourceProxy instanceof ResourceReference);
	assertEquals("resource1", resourceProxy.getId());
	assertEquals("Resource", ((ResourceReference) resourceProxy).getType());
	assertEquals("text/plain", resourceProxy.getMimetype());
	assertEquals(new URI("http://resources/1"), resourceProxy.getURI());

	xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceType>SearchPage</ResourceType>"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	resourceProxyNode = getResourceProxyNode(xml);
	resourceProxy = instance.createResourceProxy(resourceProxyNode, newXPath());
	assertEquals("SearchPage", ((ResourceReference) resourceProxy).getType());
	assertNull(resourceProxy.getMimetype());
    }

    @Test
    public void testCreateResourceProxyNoResourceType() throws Exception {
	String xml = "<ResourceProxy id=\"resource1\">"
		+ "<ResourceRef>http://resources/1</ResourceRef>"
		+ "</ResourceProxy>";
	Node resourceProxyNode = getResourceProxyNode(xml);
	try {
	    instance.createResourceProxy(resourceProxyNode, newXPath());
	    fail("Should fail because no resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("Encountered resource proxy without ResourceType"));
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
	    instance.createResourceProxy(resourceProxyNode, newXPath());
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
	    instance.createResourceProxy(resourceProxyNode, newXPath());
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
	    instance.createResourceProxy(resourceProxyNode, newXPath());
	    fail("Should fail because no resource type");
	} catch (MetadataException mEx) {
	    // Should be thrown
	    assertTrue(mEx.getMessage().startsWith("URI syntax exception"));
	    assertEquals(URISyntaxException.class, mEx.getCause().getClass());
	}
    }

    private Node getResourceProxyNode(CharSequence resourceProxyXML) throws SAXException, IOException, XPathExpressionException {
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
	return (Node) newXPath().evaluate("/cmd:CMD/cmd:Resources/cmd:ResourceProxyList/*", document, XPathConstants.NODE);
    }
    
    private XPath newXPath(){
	XPathFactory xpf = XPathFactory.newInstance();
	XPath xPath = xpf.newXPath();
	xPath.setNamespaceContext(new CMDINamespaceContext());
	return xPath;
    }
}
