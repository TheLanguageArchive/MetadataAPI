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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.cmdi.api.dom.CMDIApiDOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentReader;
import nl.mpi.metadata.cmdi.api.dom.CMDINamespaceContext;
import nl.mpi.metadata.cmdi.api.dom.DOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIMetadataElementFactoryImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import nl.mpi.metadata.cmdi.api.type.impl.CMDIProfileContainerImpl;
import nl.mpi.metadata.cmdi.api.type.impl.CMDIProfileReader;
import nl.mpi.metadata.cmdi.api.type.impl.CMDIProfileReaderTest;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIAPITestCase {

    private static Logger LOG = LoggerFactory.getLogger(CMDIAPITestCase.class);
    /**
     * Test schema 1 (TextCorpusProfile
     * http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1271859438164)
     */
    public final static URL testSchemaTextCorpus = CMDIProfileReaderTest.class.getResource("/xsd/TextCorpusProfile.xsd");
    public final static String TEXT_CORPUS_PROFILE_ROOT_NODE_PATH = "/:CMD/:Components/:TextCorpusProfile";
    public final static String REMOTE_TEXT_CORPUS_SCHEMA_URL = "http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438164/xsd";
    /**
     * Test schema 1 instance location
     */
    public static final String TEXT_CORPUS_INSTANCE_LOCATION = "/cmdi/TextCorpusProfile-instance.cmdi";
    /**
     * Location of test schema with 'localURI' attribute extension
     */
    public static final String TEXT_CORPUS_LOCAL_URI_INSTANCE_LOCATION = "/cmdi/TextCorpusProfile-instance-localURI.cmdi";
    /**
     * Test schema 2 (CLARINWebservice
     * http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1311927752335)
     */
    public final static URL testSchemaWebservice = CMDIProfileReaderTest.class.getResource("/xsd/clarin-webservice.xsd");
    /**
     * Small test schema with no xml.xsd import (should process quickly even
     * without entity resolver)
     */
    public final static URL testSchemaSmall = CMDIProfileReaderTest.class.getResource("/xsd/SmallTestProfile.xsd");
    public final static String SMALL_PROFILE_ROOT_NODE_PATH = "/:CMD/:Components/:SmallTestProfile";
    public final static CMDIMetadataElementFactory CMDI_METADATA_ELEMENT_FACTORY = new CMDIMetadataElementFactoryImpl();
    /**
     * Entity resolver that resolves remote schema locations for the CMDI
     * instances in the test package resources
     */
    public final static CMDIEntityResolver CMDI_API_TEST_ENTITY_RESOLVER = new CMDIEntityResolver() {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            LOG.debug("CMDI_API_TEST_ENTITY_RESOLVER: publicId={} systemId={}", publicId, systemId);
            if (REMOTE_TEXT_CORPUS_SCHEMA_URL.equals(systemId)) {
                return new InputSource(testSchemaTextCorpus.openStream());
            }
            return super.resolveEntity(publicId, systemId);
        }
    };
    public final static DOMBuilderFactory CMDI_API_TEST_DOM_BUILDER_FACTORY = new CMDIApiDOMBuilderFactory(CMDI_API_TEST_ENTITY_RESOLVER);

    public static CMDIProfile getNewTestProfileAndRead() throws IOException, CMDITypeException, URISyntaxException {
        return getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
    }

    public static CMDIProfile getNewTestProfileAndRead(URI uri) throws IOException, CMDITypeException {
        CMDIProfile profile = new CMDIProfileReader(CMDI_API_TEST_ENTITY_RESOLVER).read(uri);
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

    protected CMDIDocument getNewTestDocument(CMDIMetadataElementFactory elementFactory) throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException, MetadataException {
        return getNewTestDocument(elementFactory, testSchemaTextCorpus.toURI(), TEXT_CORPUS_INSTANCE_LOCATION, TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
    }

    protected CMDIDocument getNewTestDocument(CMDIMetadataElementFactory elementFactory, final URI schemaURI, final String documentResourceLocation, final String rootNodePath) throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException, MetadataException {
        Document domDocument = getDomDocumentForResource(documentResourceLocation);
        return getDocumentReader(elementFactory).read(domDocument, getClass().getResource(documentResourceLocation).toURI());
    }

    protected CMDIDocumentReader getDocumentReader(CMDIMetadataElementFactory elementFactory) {
        return new CMDIDocumentReader(getProfileContainer(), elementFactory);
    }

    protected CMDIProfileContainerImpl getProfileContainer() {
        return new CMDIProfileContainerImpl(getProfileReader());
    }

    protected CMDIProfileReader getProfileReader() {
        return new CMDIProfileReader(getEntityResolver());
    }

    protected EntityResolver getEntityResolver() {
        return CMDI_API_TEST_ENTITY_RESOLVER;
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

    protected static String domToString(Document originalDocument) throws TransformerFactoryConfigurationError, TransformerException, TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(originalDocument);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        return writer.toString();
    }

    private final static XPathFactory xPathFactory = XPathFactory.newInstance();
    private final static CMDINamespaceContext namespaceContext = new CMDINamespaceContext();

    protected Node selectSingleNode(Node node, String path) throws XPathExpressionException {
        final XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(namespaceContext);
        return (Node) xPath.evaluate(path, node, XPathConstants.NODE);
    }
}
