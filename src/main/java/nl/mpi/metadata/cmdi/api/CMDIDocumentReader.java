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
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentReader implements MetadataDocumentReader<CMDIDocument> {

    private final CMDIProfileContainer profileContainer;

    public CMDIDocumentReader(CMDIProfileContainer profileContainer) {
	this.profileContainer = profileContainer;
    }

    public CMDIDocument read(final Document document) throws MetadataDocumentException {
	CMDIProfile profile = getProfileForDocument(document);
	CMDIDocument cmdiDocument = new CMDIDocument(document, profile);
	// Do actual reading
	return cmdiDocument;
    }

    private CMDIProfile getProfileForDocument(final Document document) throws MetadataDocumentException {
	try {
	    URI profileURI = getProfileURI(document);
	    if (profileURI == null) {
		throw new MetadataDocumentException("No profile URI found in metadata document");
	    }
	    try {
		return profileContainer.getProfile(profileURI);
	    } catch (IOException ioEx) {
		throw new MetadataDocumentException(String.format("IOException occurred while trying to retrieve profile %1$s. See nested exception for details.", profileURI), ioEx);
	    } catch (CMDITypeException ctEx) {
		throw new MetadataDocumentException(String.format("CMDITypeException occurred while trying to retrieve profile %1$s. See nested exception for details.", profileURI), ctEx);
	    }
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException("TransformationException while looking for profile URI in metadata document. See nested exception for details.", tEx);
	} catch (URISyntaxException uEx) {
	    throw new MetadataDocumentException("URISyntaxException while looking for profile URI in metadata document. See nested exception for details.", uEx);
	}
    }

    private URI getProfileURI(final Document document) throws TransformerException, URISyntaxException {
	Node schemaLocationNode = XPathAPI.selectSingleNode(document, "/CMD/@schemaLocation");
	if (schemaLocationNode != null) {
	    String schemaLocationString = schemaLocationNode.getNodeValue().trim();
	    String[] schemaLocationTokens = schemaLocationString.split("\\s");
	    for (int i = 0; i < schemaLocationTokens.length; i += 2) {
		// Check if namespace matches CMD namespace
		if (schemaLocationTokens[i].equals(CMDIConstants.CMD_NAMESPACE)) {
		    // If so, take next token as URI
		    return new URI(schemaLocationTokens[i + 1]);
		}
	    }
	}
	return null;
    }
}
