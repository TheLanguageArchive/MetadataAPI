/*
 * Copyright (C) 2011 Max Planck Institute for Psycholinguistics
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

import nl.mpi.metadata.cmdi.api.dom.DOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIApiDOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileTest;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIAPITestCase {

    /**
     * Test schema 1 (TextCorpusProfile http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1271859438164)
     */
    public final static URL testSchemaTextCorpus = CMDIProfileTest.class.getResource("/xsd/TextCorpusProfile.xsd");
    public final static String TEXT_CORPUS_PROFILE_ROOT_NODE_PATH = "/:CMD/:Components/:TextCorpusProfile";
    public final static String REMOTE_TEXT_CORPUS_SCHEMA_URL = "http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438164/xsd";
    /**
     * Test schema 1 instance location
     */
    public static final String TEXT_CORPUS_INSTANCE_LOCATION = "/cmdi/TextCorpusProfile-instance.cmdi";
    /**
     * Test schema 2 (CLARINWebservice http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1311927752335)
     */
    public final static URL testSchemaWebservice = CMDIProfileTest.class.getResource("/xsd/clarin-webservice.xsd");
    /**
     * Small test schema with no xml.xsd import (should process quickly even without entity resolver)
     */
    public final static URL testSchemaSmall = CMDIProfileTest.class.getResource("/xsd/SmallTestProfile.xsd");
    public final static String SMALL_PROFILE_ROOT_NODE_PATH = "/:CMD/:Components/:SmallTestProfile";
    /**
     * Entity resolver that resolves remote schema locations for the CMDI instances in the test package resources
     */
    public final static CMDIEntityResolver CMDI_API_TEST_ENTITY_RESOLVER = new CMDIEntityResolver() {

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	    System.out.printf("CMDI_API_TEST_ENTITY_RESOLVER: publicId=%1$s systemId=%2$s\n",publicId,systemId);
	    if (REMOTE_TEXT_CORPUS_SCHEMA_URL.equals(systemId)) {
		return new InputSource(testSchemaTextCorpus.openStream());
	    }
	    return super.resolveEntity(publicId, systemId);
	}
    };
    public final static DOMBuilderFactory CMDI_API_TEST_DOM_BUILDER_FACTORY = new CMDIApiDOMBuilderFactory() {

	@Override
	protected EntityResolver getEntityResolver() {
	    return CMDI_API_TEST_ENTITY_RESOLVER;
	}
    };

    public static CMDIProfile getNewTestProfileAndRead() throws IOException, CMDITypeException, URISyntaxException {
	return getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
    }

    public static CMDIProfile getNewTestProfileAndRead(URI uri) throws IOException, CMDITypeException {
	final CMDIProfile profile = new CMDIProfile(uri, new CMDIEntityResolver());
	return profile;
    }

    protected Document getDomDocumentForResource(final String documentResourceLocation) throws ParserConfigurationException, IOException, SAXException {
	final InputStream documentStream = getClass().getResourceAsStream(documentResourceLocation);
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	domFactory.setNamespaceAware(true);
	domFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
	DocumentBuilder builder = domFactory.newDocumentBuilder();
	return builder.parse(documentStream);
    }

    protected CMDIDocument getNewTestDocument() throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException, MetadataException {
	return getNewTestDocument(testSchemaTextCorpus.toURI(), TEXT_CORPUS_INSTANCE_LOCATION, TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
    }

    protected CMDIDocument getNewTestDocument(final URI schemaURI, final String documentResourceLocation, final String rootNodePath) throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException, MetadataException {
	Document domDocument = getDomDocumentForResource(documentResourceLocation);
	return getDocumentReader().read(domDocument, getClass().getResource(documentResourceLocation).toURI());
    }

    protected CMDIDocumentReader getDocumentReader() {
	return new CMDIDocumentReader(getProfileContainer());
    }

    protected CMDIProfileContainer getProfileContainer() {
	return new CMDIProfileContainer(getEntityResolver());
    }

    protected EntityResolver getEntityResolver() {
	return new CMDIEntityResolver();
    }

    /**
     * Entity resolver that 
     */
    protected static class TestEntityResolver extends CMDIEntityResolver {

	public int byteStreamRequested = 0;
	final URL sourceURL;
	final URL targetURL;

	public TestEntityResolver(URL source, URL target) {
	    this.sourceURL = source;
	    this.targetURL = target;
	}

	@Override
	public synchronized InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	    if (systemId.equals(sourceURL.toString())) {
		return new InputSource() {

		    @Override
		    public synchronized InputStream getByteStream() {
			byteStreamRequested++;
			try {
			    return targetURL.openStream();
			} catch (IOException ioEx) {
			    Assert.fail(ioEx.toString());
			    return null;
			}
		    }
		};
	    } else {
		return CMDI_API_TEST_ENTITY_RESOLVER.resolveEntity(publicId, systemId);
	    }
	}
    };
}
