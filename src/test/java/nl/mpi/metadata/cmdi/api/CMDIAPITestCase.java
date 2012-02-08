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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileTest;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
    public final static String TEXT_CORPUS_PROFILE_ROOT_NODE_PATH = "/CMD/Components/TextCorpusProfile";
    /**
     * Test schema 1 instance location
     */
    public static final String TEXT_CORPUS_INSTANCE_LOCATION = "/cmdi/TextCorpusProfile-instance.cmdi";
    /**
     * Test schema 2 (CLARINWebservice http://catalog.clarin.eu/ds/ComponentRegistry?item=clarin.eu:cr1:p_1311927752335)
     */
    public final static URL testSchemaWebservice = CMDIProfileTest.class.getResource("/xsd/clarin-webservice.xsd");
    public final static URL testSchemaSmall = CMDIProfileTest.class.getResource("/xsd/SmallTestProfile.xsd");
    public final static String SMALL_PROFILE_ROOT_NODE_PATH = "/CMD/Components/SmallTestProfile";

    public CMDIProfile getNewTestProfileAndRead() throws IOException, CMDITypeException, URISyntaxException {
	return getNewTestProfileAndRead(testSchemaTextCorpus.toURI());
    }

    public CMDIProfile getNewTestProfileAndRead(URI uri) throws IOException, CMDITypeException {
	final CMDIProfile profile = new CMDIProfile(uri);
	profile.readSchema();
	return profile;
    }

    protected Document getDomDocumentForResource(final String documentResourceLocation) throws ParserConfigurationException, IOException, SAXException {
	final InputStream documentStream = getClass().getResourceAsStream(documentResourceLocation);
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = domFactory.newDocumentBuilder();
	return builder.parse(documentStream);
    }

    protected CMDIDocument getNewTestDocument() throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException {
	return getNewTestDocument(testSchemaTextCorpus.toURI(), TEXT_CORPUS_INSTANCE_LOCATION, TEXT_CORPUS_PROFILE_ROOT_NODE_PATH);
    }

    protected CMDIDocument getNewTestDocument(final URI schemaURI, final String documentResourceLocation, final String rootNodePath) throws IOException, CMDITypeException, ParserConfigurationException, SAXException, TransformerException, URISyntaxException {
	CMDIProfile profile = getNewTestProfileAndRead(schemaURI);
	Document domDocument = getDomDocumentForResource(documentResourceLocation);
	Node documentRootNode = XPathAPI.selectSingleNode(domDocument, rootNodePath);
	return new CMDIDocument(documentRootNode, profile, getClass().getResource(documentResourceLocation).toURI());
    }
}
