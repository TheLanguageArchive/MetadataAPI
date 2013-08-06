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
import java.util.Collection;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.CMDIAPITestCase;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import org.apache.xpath.XPathAPI;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static nl.mpi.metadata.cmdi.api.CMDIAPITestCase.TEXT_CORPUS_INSTANCE_LOCATION;
import static nl.mpi.metadata.cmdi.api.CMDIConstants.*;
import static org.junit.Assert.*;

/**
 * At the moment tests both {@link CMDIDocumentReader} and {@link CMDIComponentReader}
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentReaderTest extends CMDIAPITestCase {

    private CMDIDocumentReader reader;
    private CMDIProfileContainer profileContainer;

    @Before
    public void setUp() {
	profileContainer = new CMDIProfileContainer(getProfileReader());
	reader = new CMDIDocumentReader(profileContainer, CMDI_METADATA_ELEMENT_FACTORY);
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
    public void testReadHeader() throws Exception {
	CMDIDocument cmdi = readTestDocument(TEXT_CORPUS_INSTANCE_LOCATION);
	// After read header state should be clean
	assertFalse(cmdi.getHeaderDirtyState().isDirty());

	// Profile should be loaded from specified schemaLocation
	assertEquals("http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438164/xsd", cmdi.getType().getSchemaLocation().toString());

	// Header information should match
	assertEquals("Joe Unit", cmdi.getHeaderInformation(CMD_HEADER_MD_CREATOR).getValue());
	assertEquals("2009-11-18", cmdi.getHeaderInformation(CMD_HEADER_MD_CREATION_DATE).getValue());
	assertEquals("clarin.eu:cr1:p_1271859438164", cmdi.getHeaderInformation(CMD_HEADER_MD_PROFILE).getValue());
	assertEquals("Metadata API test instances", cmdi.getHeaderInformation(CMD_HEADER_MD_COLLECTION_DISPLAY_NAME).getValue());

    }

    /**
     * Test of read method, of class CMDIDocumentReader.
     *
     * Component structure of tested document:
     *
     * <TextCorpusProfile>
     * <Collection>
     * <GeneralInfo>
     * <Name>TextCorpus test</Name>
     * </GeneralInfo>
     * <OriginLocation>
     * <Location><Country><Code>NL</Code>..</Location>
     * <Location><Country><Code>BE</Code>..</Location>
     * </OriginLocation>
     * ...
     * </Collection>
     * <Corpus>...</Corpus>
     * <TextCorpus>...</TextCorpus>
     * </TextCorpusProfile>
     */
    @Test
    public void testReadComponents() throws Exception {
	CMDIDocument cmdi = readTestDocument(TEXT_CORPUS_INSTANCE_LOCATION);
	// After read document should be a clean metadata element
	assertFalse(cmdi.isDirty());
	assertEquals(3, cmdi.getChildren().size());

	final Component collection = (Component) cmdi.getChildElement("Collection");
	assertNotNull(collection);
	// Component should be clean, fresh read
	assertFalse(collection.isDirty());

	// Get Collection/GeneralInfo/Name element
	final Component generalInfo = (Component) cmdi.getChildElement("Collection/GeneralInfo");
	assertNotNull(generalInfo);
	assertEquals(0, generalInfo.getAttributes().size());
	// Component should be clean, fresh read
	assertFalse(generalInfo.isDirty());

	// Get Collection/GeneralInfo/Name element
	final Element name = (Element) cmdi.getChildElement("Collection/GeneralInfo/Name");
	assertNotNull(name);
	// Name should also be retrievable from Collection component
	assertEquals(name, collection.getChildElement("GeneralInfo/Name"));
	// Value should match document
	assertEquals("TextCorpus test", name.getValue());
	// Element should be clean, fresh read
	assertFalse(name.isDirty());

	// Check component with multiple occurences
	final Component originLocation = (Component) cmdi.getChildElement("Collection/OriginLocation");
	assertEquals(2, originLocation.getChildren().size());
	Element location1code = (Element) originLocation.getChildElement("Location[1]/Country/Code");
	assertEquals("NL", location1code.getValue());
	Element location2code = (Element) originLocation.getChildElement("Location[2]/Country/Code");
	assertEquals("BE", location2code.getValue());

	// Check resource proxies. Read non-metadata resource proxies
	Collection<Reference> references = generalInfo.getReferences();
	assertNotNull(references);
	assertEquals(2, references.size());
	Iterator<Reference> iterator = references.iterator();
	ResourceProxy reference = (ResourceProxy) iterator.next();
	assertEquals("resource1", reference.getId());
	assertEquals(new URI("http://resources/1"), reference.getURI());
	assertTrue(reference instanceof DataResourceProxy);
	reference = (ResourceProxy) iterator.next();
	assertEquals("resource2", reference.getId());
	assertEquals(new URI("http://resources/2"), reference.getURI());
	assertTrue(reference instanceof DataResourceProxy);

	// Read metadata resource proxy
	references = originLocation.getReferences();
	assertNotNull(references);
	assertEquals(1, references.size());
	reference = (ResourceProxy) references.iterator().next();
	assertEquals("metadata1", reference.getId());
	assertEquals(new URI("http://metadata/1"), reference.getURI());
	assertTrue(reference instanceof MetadataResourceProxy);

    }

    /**
     * Test of read method, of class CMDIDocumentReader.
     */
    @Test
    public void testReadAttributesAndLanguages() throws Exception {
	CMDIDocument cmdi = readTestDocument(TEXT_CORPUS_INSTANCE_LOCATION);

	// Get Collection/GeneralInfo/Description/Description
	final Element description = (Element) cmdi.getChildElement("Collection/GeneralInfo/Description/Description");
	// Check attributes
	Collection<Attribute> attributes = description.getAttributes();
	assertEquals(1, attributes.size());
	// Check attribute values
	final Attribute attribute = attributes.iterator().next();
	assertEquals("nl", attribute.getValue());
	assertEquals("LanguageID", attribute.getType().getName());
	assertEquals("", attribute.getType().getNamespaceURI()); // default namespace

	// Get Collection/GeneralInfo/Name element
	final MultilingualElement name = (MultilingualElement) cmdi.getChildElement("Collection/GeneralInfo/Name");
	// Check attributes
	attributes = name.getAttributes();
	assertEquals(0, attributes.size());
	assertEquals("en", name.getLanguage());
    }

    private CMDIDocument readTestDocument(String resource) throws SAXException, DOMException, MetadataException, ParserConfigurationException, IOException {
	final Document dom = getDomDocumentForResource(resource);
	final CMDIDocument cmdi = reader.read(dom, null);
	assertNotNull(cmdi);
	return cmdi;
    }

    /**
     * Tries to read a CMDI file that uses "cmd:" namespace prefix for all elements in the CMDI namespace
     *
     * @throws Exception
     */
    @Test
    public void testReadNamespacePrefixDocument() throws Exception {
	readTestDocument("/cmdi/Soundbites-instance-namespace-prefixes.cmdi");
    }

    /**
     * Test of read method, of class CMDIDocumentReader.
     */
    @Test(expected = MetadataException.class)
    public void testReadProfileUriMissing() throws Exception {
	Document dom = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
	// Remove schema location info
	org.w3c.dom.Element cmdElement = (org.w3c.dom.Element) XPathAPI.selectSingleNode(dom, "/:CMD");
	cmdElement.removeAttribute("xsi:schemaLocation");
	// Read from DOM. Should fail because of missing URI
	reader.read(dom, null);
    }

    /**
     * Test of read method, of class CMDIDocumentReader.
     */
    @Test
    public void testReadProfileUriSyntax() throws Exception {
	Document dom = getDomDocumentForResource(TEXT_CORPUS_INSTANCE_LOCATION);
	// Replace schema location info by illegal URI
	org.w3c.dom.Element cmdElement = (org.w3c.dom.Element) XPathAPI.selectSingleNode(dom, "/:CMD");
	cmdElement.setAttribute("xsi:schemaLocation", CMDIConstants.CMD_NAMESPACE + " http://\"illegal\"");
	// Read from DOM. Should fail because of syntax error
	try {
	    reader.read(dom, null);
	    fail("Expected URISyntaxException nested in MetadataException");
	} catch (MetadataException mdEx) {
	    assertEquals(URISyntaxException.class, mdEx.getCause().getClass());
	}
    }

    @Test
    public void testGetProfileURI() throws Exception {
	final XPathFactory xpf = XPathFactory.newInstance();
	final XPath xPath = xpf.newXPath();
	xPath.setNamespaceContext(new CMDINamespaceContext());
	// Test with standard CMD namespace
	Document document = XMLUnit.buildTestDocument(
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<CMD xmlns=\"http://www.clarin.eu/cmd/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
		+ "CMDVersion=\"1.1\"\n"
		+ "xsi:schemaLocation=\"http://www.clarin.eu/cmd/ http://schemalocation\">\n"
		+ "</CMD>\n");
	URI profileURI = reader.getProfileURI(document, xPath);
	assertEquals(new URI("http://schemalocation"), profileURI);

	// Test with CMD namespace prefix
	document = XMLUnit.buildTestDocument(
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<cmd:CMD xmlns:cmd=\"http://www.clarin.eu/cmd/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
		+ "CMDVersion=\"1.1\"\n"
		+ "xsi:schemaLocation=\"http://www.clarin.eu/cmd/ http://schemalocation\">\n"
		+ "</cmd:CMD>\n");
	profileURI = reader.getProfileURI(document, xPath);
	assertEquals(new URI("http://schemalocation"), profileURI);

	// Test with non-matching namespaces
	document = XMLUnit.buildTestDocument(
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<CMD xmlns=\"http://www.clarin.eu/cmd/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
		+ "CMDVersion=\"1.1\"\n"
		+ "xsi:schemaLocation=\"http://www.mpi.nl/custom/ http://schemalocation\">\n"
		+ "</CMD>\n");
	profileURI = reader.getProfileURI(document, xPath);
	assertEquals(new URI("http://schemalocation"), profileURI);

	// Test with no specification at all
	document = XMLUnit.buildTestDocument(
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<CMD xmlns=\"http://www.clarin.eu/cmd/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
		+ "CMDVersion=\"1.1\">\n"
		+ "</CMD>\n");
	profileURI = reader.getProfileURI(document, xPath);
	assertNull(profileURI);
    }
}
