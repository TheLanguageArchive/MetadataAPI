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

import javax.xml.parsers.DocumentBuilderFactory;
import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.jmock.integration.junit4.JUnit4Mockery;
import java.util.Properties;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentWriterTest extends CMDIAPITestCase {

    private JUnit4Mockery context = new JUnit4Mockery();
    private MetadataDOMBuilder<CMDIDocument> domBuilder;
    private CMDIDocumentWriter documentWriter;

    public CMDIDocumentWriterTest() {
    }

    @Before
    public void setUp() {
	domBuilder = (MetadataDOMBuilder<CMDIDocument>) context.mock(MetadataDOMBuilder.class, "CMDIDocument");
	documentWriter = new CMDIDocumentWriter(domBuilder);
    }

    @After
    public void tearDown() {
	domBuilder = null;
	documentWriter = null;
    }

    /**
     * Test of write method, of class CMDIDocumentWriter.
     */
    @Test
    public void testWrite() throws Exception {
	final CMDIDocument document = new CMDIDocument(getNewTestProfileAndRead());

	// Create a mock DOM result
	DocumentBuilderFactory documentBuilder = DocumentBuilderFactory.newInstance();
	final Document mockDom = documentBuilder.newDocumentBuilder().newDocument();
	mockDom.appendChild(mockDom.createElement("CMD")).appendChild(mockDom.createElement("Components")).appendChild(mockDom.createElement("Test"));

	// Configure mock DomBuilder
	context.checking(new Expectations() {

	    {
		// mock builder should be called with document
		oneOf(domBuilder).buildDomForDocument(document);
		// and return the mock DOM
		will(returnValue(mockDom));
	    }
	});

	// Write document 
	StringWriter resultStringWriter = new StringWriter();
	Result result = new StreamResult(resultStringWriter);
	documentWriter.write(document, result);

	// Compare result to mock DOM
	Document testDocument = XMLUnit.buildTestDocument(resultStringWriter.getBuffer().toString());
	Diff compareXML = XMLUnit.compareXML(mockDom, testDocument);
	assertTrue(compareXML.toString(), compareXML.identical());
    }

    /**
     * Test of getOutputProperties method, of class CMDIDocumentWriter.
     */
    @Test
    public void testGetOutputProperties() {
	assertNull(documentWriter.getOutputProperties());
	Properties properties = new Properties();
	properties.setProperty("some-property", "some-value");
	documentWriter.setOutputProperties(properties);
	// Should not be altered
	assertSame(properties, documentWriter.getOutputProperties());
    }

    /**
     * Test of setOutputProperties method, of class CMDIDocumentWriter.
     */
    @Test
    public void testSetOutputProperties() throws Exception {
	Properties properties = new Properties();
	properties.setProperty(OutputKeys.MEDIA_TYPE, "application/x-blabla");
	documentWriter.setOutputProperties(properties);
	Transformer transformer = documentWriter.getNewTransformer();
	Properties transformerOutputProperties = transformer.getOutputProperties();
	assertEquals("application/x-blabla", transformerOutputProperties.getProperty(OutputKeys.MEDIA_TYPE));

    }
}
