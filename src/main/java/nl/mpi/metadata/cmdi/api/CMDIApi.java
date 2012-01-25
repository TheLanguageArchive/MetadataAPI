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
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;

/**
 * CMDI implementation of the @see MetadataAPI
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApi implements MetadataAPI<CMDIMetadataElement, CMDIContainerMetadataElement, CMDIDocument> {

    private MetadataDocumentReader<CMDIDocument> documentReader;

    public CMDIApi() {
	this(new CMDIDocumentReader());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader) {
	this.documentReader = documentReader;
    }

    public CMDIDocument getMetadataDocument(URL url) throws IOException {
	InputStream documentStream = url.openStream();
	try {
	    return documentReader.read(documentStream);
	} finally {
	    documentStream.close();
	}
    }

    public CMDIDocument createMetadataDocument(MetadataDocumentType type) {
	// Create new DOM instance
	// Add boilerplate (using schema(?)...)
	// Create new MetadataDocument
	// Construct a minimal instance using createMetadataElement
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement createMetadataElement(CMDIContainerMetadataElement parent, MetadataElementType type) {
	// Take the type of the parent
	// Check if child type is allowed
	// Add to DOM
	// Instantiate
	// Add to parent
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement removeElement(CMDIMetadataElement element) throws MetadataDocumentException {
	// Find parent
	// Remove from DOM
	// Remove as child in parent
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean validateMetadataDocument(CMDIDocument document) {
	// Use stock validator. Resolve schema and execute
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the value of documentReader
     *
     * @return the value of documentReader
     */
    public MetadataDocumentReader<CMDIDocument> getDocumentReader() {
	return documentReader;
    }
}
