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
package nl.mpi.metadata.cmdi.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import org.apache.xpath.CachedXPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentReader implements MetadataDocumentReader<CMDIDocument> {

    private static Logger logger = LoggerFactory.getLogger(CMDIDocumentReader.class);
    private final CMDIProfileContainer profileContainer;
    private final CMDIComponentReader componentReader;

    /**
     * Creates a CMDI document reader that uses the specified profile container
     * @param profileContainer profile container that gets used to retrieve CMDI profiles
     */
    public CMDIDocumentReader(CMDIProfileContainer profileContainer) {
	this(profileContainer, new CMDIComponentReader());
    }

    public CMDIDocumentReader(CMDIProfileContainer profileContainer, CMDIComponentReader componentReader) {
	this.profileContainer = profileContainer;
	this.componentReader = componentReader;
    }

    /**
     * Reads the specified document into a new {@link CMDIDocument} instance
     * @param document DOM object to read. Should have been parsed with a <em>namespace aware</em> builder!!
     * @param documentURI URI for the document. Can be null if no identifier is available (e.g. file has not been saved)
     * @return
     * @throws MetadataDocumentException if an unexpected circumstance is detected while reading the document
     * @throws IOException if an I/O error occurs while reading the profile schema through the {@link CMDIProfileContainer} referenced in the document
     */
    public CMDIDocument read(final Document document, final URI documentURI) throws MetadataDocumentException, DOMException, IOException {
	final CachedXPathAPI xPathAPI = new CachedXPathAPI();
	final CMDIProfile profile = getProfileForDocument(document, documentURI, xPathAPI);
	final CMDIDocument cmdiDocument = createCMDIDocument(xPathAPI, document, documentURI, profile);

	readHeader(cmdiDocument, document, xPathAPI);
	componentReader.readComponents(cmdiDocument, document, xPathAPI);

	return cmdiDocument;
    }

    private CMDIDocument createCMDIDocument(final CachedXPathAPI xPathAPI, final Document document, URI documentURI, final CMDIProfile profile) throws MetadataDocumentException {
	final String rootComponentNodePath = profile.getPathString();
	try {
	    final Node rootComponentNode = xPathAPI.selectSingleNode(document, rootComponentNodePath);

	    if (rootComponentNode == null) {
		throw new MetadataDocumentException(String.format("Root component node not found at specified path: %1$s", rootComponentNodePath));
	    }

	    logger.debug("Found documentNode at {}", rootComponentNodePath);
	    return new CMDIDocument(profile, documentURI);
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(
		    String.format("TransormationException while looking up root component node at specified path: %1$s", rootComponentNodePath),
		    tEx);
	}
    }

    /**
     * Determines the URI of the profile schema and loads the schema through the {@link CMDIProfileContainer} of this instance.
     * @param document DOM of document to load profile for
     * @return profile referenced by the document
     * @throws MetadataDocumentException
     * @throws IOException 
     */
    private CMDIProfile getProfileForDocument(final Document document, final URI documentURI, final CachedXPathAPI xPathAPI) throws MetadataDocumentException, IOException {
	try {
	    URI profileURI = getProfileURI(document, xPathAPI);
	    if (profileURI == null) {
		throw new MetadataDocumentException("No profile URI found in metadata document");
	    }
	    if (documentURI != null) {
		profileURI = documentURI.resolve(profileURI);
	    }
	    try {
		return profileContainer.getProfile(profileURI);
	    } catch (CMDITypeException ctEx) {
		throw new MetadataDocumentException(String.format("CMDITypeException occurred while trying to retrieve profile $1%s. See nested exception for details.", profileURI), ctEx);
	    }
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException("TransformationException while looking for profile URI in metadata document. See nested exception for details.", tEx);
	} catch (URISyntaxException uEx) {
	    throw new MetadataDocumentException("URISyntaxException while looking for profile URI in metadata document. See nested exception for details.", uEx);
	}
    }

    /**
     * Locates the schemaLocation specification and extracts the location of the schema specified for the CMD namespace.
     * @param document DOM of document to find schema URI for
     * @return URI of schema, null if not present
     * @throws TransformerException 
     * @throws URISyntaxException 
     */
    private URI getProfileURI(final Document document, final CachedXPathAPI xPathAPI) throws TransformerException, URISyntaxException {
	// Find the <CMD xsi:schemaLocation="..."> attribute
	final Node schemaLocationNode = xPathAPI.selectSingleNode(document, "/:CMD/@xsi:schemaLocation");
	if (schemaLocationNode != null) {
	    // SchemaLocation value consists of {namespace,location} pairs. Find CMD namespace and get the location of its schema
	    final String schemaLocationString = schemaLocationNode.getNodeValue().trim();
	    // Tokenize
	    final String[] schemaLocationTokens = schemaLocationString.split("\\s");
	    for (int i = 0; i < schemaLocationTokens.length; i += 2) {
		// Check if namespace matches CMD namespace
		if (schemaLocationTokens[i].equals(CMDIConstants.CMD_NAMESPACE)) {
		    // If so, take next token as URI
		    return new URI(schemaLocationTokens[i + 1]);
		}
	    }
	}
	// No schemaLocation specified (so null)
	return null;
    }

    private void readHeader(final CMDIDocument cmdiDocument, final Document document, final CachedXPathAPI xPathAPI) throws MetadataDocumentException {
	try {
	    // Find the <Header> Element. Should be there!
	    final Node headerNode = xPathAPI.selectSingleNode(document, "/:CMD/:Header");
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
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(cmdiDocument,
		    "TransformationException while reading header information in document. See nested exception for details.", tEx);
	}
    }

    private void addHeaderInformationFromDocument(final Node headerChild, final CMDIDocument cmdiDocument) throws DOMException {
	// Put String values in header info
	// TODO: Some fields should have different type (e.g. URI or Date)
	HeaderInfo<String> headerInfo = new HeaderInfo<String>();
	// Take name from element name
	headerInfo.setName(headerChild.getNodeName());
	// Take value from text content
	headerInfo.setValue(headerChild.getTextContent());
	// (CMDI header does not support attributes)
	// Put into metadata document
	cmdiDocument.putHeaderInformation(headerInfo);
    }
}
