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
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.dom.MetadataDocumentReader;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIDocumentImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of metadata document reader for CMDI documents.
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentReader implements MetadataDocumentReader<CMDIDocument> {

    private static final Logger logger = LoggerFactory.getLogger(CMDIDocumentReader.class);
    private static final CMDINamespaceContext CMDI_NAMESPACE_CONTEXT = new CMDINamespaceContext();
    private final CMDIProfileContainer profileContainer;
    private final CMDIComponentReader componentReader;
    private final CMDIResourceProxyReader resourceReader;

    /**
     * Creates a CMDI document reader that uses the specified profile container and a new {@link CMDIComponentReader} and
     * {@link CMDIResourceProxyReader}
     *
     * @param profileContainer profile container that should be used to retrieve CMDI profiles
     * @param elementFactory metadata element factory to use for creating new components and elements
     * @see CMDIComponentReader
     */
    public CMDIDocumentReader(CMDIProfileContainer profileContainer, CMDIMetadataElementFactory elementFactory) {
	this(profileContainer, new CMDIComponentReader(elementFactory), new CMDIResourceProxyReader());
    }

    /**
     * * Creates a CMDI document reader that uses the specified profile container and component reader
     *
     * @param profileContainer profile container that should be used to retrieve CMDI profiles
     * @param componentReader component reader that should be used for reading CMDI components
     */
    public CMDIDocumentReader(CMDIProfileContainer profileContainer, CMDIComponentReader componentReader, CMDIResourceProxyReader resourceReader) {
	this.profileContainer = profileContainer;
	this.componentReader = componentReader;
	this.resourceReader = resourceReader;
    }

    /**
     * Reads the specified document into a new {@link CMDIDocument} instance
     *
     * @param document DOM object to read. Should have been parsed with a <em>namespace aware</em> builder!!
     * @param documentURI URI for the document. Can be null if no identifier is available (e.g. file has not been saved)
     * @return representation of the read document
     * @throws MetadataDocumentException if an unexpected circumstance is detected while reading the document
     * @throws IOException if an I/O error occurs while reading the profile schema through the {@link CMDIProfileContainer} referenced in
     * the document
     */
    @Override
    public CMDIDocument read(final Document document, final URI documentURI) throws MetadataException, DOMException, IOException {
	final XPath xPath = XPathFactory.newInstance().newXPath();
	// set namespace context so that 'cmd' and 'xsi' prefixes get mapped to the corresponding namespaces properly
	xPath.setNamespaceContext(CMDI_NAMESPACE_CONTEXT);

	final CMDIProfile profile = getProfileForDocument(document, documentURI, xPath);
	final CMDIDocument cmdiDocument = createCMDIDocument(xPath, document, documentURI, profile);

	readHeader(cmdiDocument, document, xPath);
	resourceReader.readResourceProxies(cmdiDocument, document, xPath);
	componentReader.readComponents(cmdiDocument, document, xPath);

	cmdiDocument.setAllClean();
	return cmdiDocument;
    }

    private CMDIDocument createCMDIDocument(final XPath xPath, final Document document, URI documentURI, final CMDIProfile profile) throws MetadataException {
	final String rootComponentNodePath = profile.getPathString();
	try {
	    final Node rootComponentNode = (Node) xPath.evaluate(rootComponentNodePath, document, XPathConstants.NODE);

	    if (rootComponentNode == null) {
		throw new MetadataException(String.format("Root component node not found at specified path: %1$s", rootComponentNodePath));
	    }

	    logger.debug("Found documentNode at {}", rootComponentNodePath);
	    //TODO: Use factory
	    return new CMDIDocumentImpl(profile, documentURI);
	} catch (XPathExpressionException ex) {
	    throw new MetadataException(
		    String.format("XPathExpressionException while looking up root component node at specified path: %1$s", rootComponentNodePath),
		    ex);
	}
    }

    /**
     * Determines the URI of the profile schema and loads the schema through the {@link CMDIProfileContainer} of this instance.
     *
     * @param document DOM of document to load profile for
     * @return profile referenced by the document
     * @throws MetadataDocumentException
     * @throws IOException
     */
    private CMDIProfile getProfileForDocument(final Document document, final URI documentURI, final XPath xPath) throws MetadataException, IOException {
	try {
	    URI profileURI = getProfileURI(document, xPath);
	    if (profileURI == null) {
		throw new MetadataException("No profile URI found in metadata document");
	    }
	    if (documentURI != null) {
		profileURI = documentURI.resolve(profileURI);
	    }
	    try {
		return profileContainer.getProfile(profileURI);
	    } catch (CMDITypeException ctEx) {
		throw new MetadataException(String.format("CMDITypeException occurred while trying to retrieve profile $1%s. See nested exception for details.", profileURI), ctEx);
	    }
	} catch (XPathExpressionException ex) {
	    throw new MetadataException("XPathExpressionException while looking for profile URI in metadata document. See nested exception for details.", ex);
	} catch (URISyntaxException uEx) {
	    throw new MetadataException("URISyntaxException while looking for profile URI in metadata document. See nested exception for details.", uEx);
	}
    }

    /**
     * Locates the schemaLocation specification and extracts the location of the schema specified for the CMD namespace.
     *
     * A degree of robustness against typos and XML mistakes is achieved by returning the schema location provided if
     * there is only one, <em>irrespective of the namespace mentioned</em>.
     *
     * @param document DOM of document to find schema URI for
     * @param xPath XPath instance ready to use with CMDI documents, name space context that knows about 'cmd' and 'xsi'
     * (such as {@link CMDINamespaceContext} is assumed)
     * @return URI of schema, null if not present
     * @throws TransformerException
     * @throws URISyntaxException
     */
    protected URI getProfileURI(final Document document, final XPath xPath) throws URISyntaxException, XPathExpressionException {
	// Find the <CMD xsi:schemaLocation="..."> attribute
	final String schemaLocationValue = xPath.evaluate("/cmd:CMD/@xsi:schemaLocation", document);
	if (schemaLocationValue != null) {
	    // SchemaLocation value consists of {namespace,location} pairs. Find CMD namespace and get the location of its schema
	    final String schemaLocationString = schemaLocationValue.trim();
	    // Tokenize
	    final String[] schemaLocationTokens = schemaLocationString.split("\\s+");
	    // Get document namespace to check against

	    // If there's just one {namespace,location} pair, return its location (i.e. don't check namespace)
	    if (schemaLocationTokens.length == 2) {
		// Output a warning message if namespace does not match document namespace
		if (logger.isWarnEnabled()) {
		    final URI documentNSUri = getDocumentNamespace(document);
		    if (!new URI(schemaLocationTokens[0]).equals(documentNSUri)) {
			logger.warn("Found one schema location for document, but namespace does not match document namespace. "
				+ "Document namespace is {}, location specified for {}", documentNSUri, schemaLocationTokens[0]);
		    }
		}
		return new URI(schemaLocationTokens[1]);
	    } else {
		final URI documentNSUri = getDocumentNamespace(document);
		// Multiple (or no (valid)) pairs, iterate and check for document namespace match
		for (int i = 0; i < schemaLocationTokens.length; i += 2) {
		    // Check if namespace matches CMD namespace
		    if (documentNSUri.equals(new URI(schemaLocationTokens[i]))) {
			// If so, take next token as URI
			return new URI(schemaLocationTokens[i + 1]);
		    }
		}
	    }
	}
	// No schemaLocation specified (so null)
	return null;
    }

    private URI getDocumentNamespace(final Document document) throws URISyntaxException {
	// Try to get document namespace from root element
	final Node documentElement = document.getDocumentElement();
	if (documentElement != null) {
	    final String documentNS = documentElement.getNamespaceURI();
	    if (documentNS != null) {
		return new URI(documentNS);
	    }
	}
	logger.warn("Cannot find namespace in document, assuming standard namespace {}", CMDIConstants.CMD_NAMESPACE);
	return new URI(CMDIConstants.CMD_NAMESPACE);
    }

    private void readHeader(final CMDIDocument cmdiDocument, final Document document, final XPath xPath) throws MetadataDocumentException {
	try {
	    // Find the <Header> Element. Should be there!
	    final Node headerNode = (Node) xPath.evaluate("/cmd:CMD/cmd:Header", document, XPathConstants.NODE);
	    if (headerNode == null) {
		throw new MetadataDocumentException(cmdiDocument, "Header node not found in CMDI document");
	    }
	    // Get the Header child elements
	    final NodeList headerChildren = headerNode.getChildNodes();
	    for (int i = 0; i < headerChildren.getLength(); i++) {
		final Node headerChild = headerChildren.item(i);
		if (headerChildren.item(i) instanceof org.w3c.dom.Element) {
		    addHeaderInformationFromDocument(headerChild, cmdiDocument);
		}
	    }
	} catch (XPathExpressionException ex) {
	    throw new MetadataDocumentException(cmdiDocument,
		    "XPathExpressionException while reading header information in document. See nested exception for details.", ex);
	}
    }

    private void addHeaderInformationFromDocument(final Node headerChild, final CMDIDocument cmdiDocument) throws DOMException {
	// Put String values in header info
	// Take name from element name, value from text content
	// TODO: Some fields should have different type (e.g. URI or Date)
	HeaderInfo headerInfo = new HeaderInfo(headerChild.getLocalName(), headerChild.getTextContent());
	// (CMDI header does not support attributes)
	// Put into metadata document
	try {
	    cmdiDocument.putHeaderInformation(headerInfo);
	} catch (MetadataException mdEx) {
	    logger.warn("Skipping header that is rejected by document", mdEx);
	}
    }
}
