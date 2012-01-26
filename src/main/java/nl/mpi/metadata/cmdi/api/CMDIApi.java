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
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * CMDI implementation of the @see MetadataAPI
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApi implements MetadataAPI<CMDIMetadataElement, CMDIContainerMetadataElement, CMDIDocument> {

    private MetadataDocumentReader<CMDIDocument> documentReader;
    private MetadataValidator<CMDIDocument> cmdiValidator;

    public CMDIApi() {
	this(new CMDIDocumentReader());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader) {
	this(documentReader, new DefaultCMDIValidator());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator) {
	this.documentReader = documentReader;
	this.cmdiValidator = cmdiValidator;
    }

    public CMDIDocument getMetadataDocument(URL url) throws IOException {
	InputStream documentStream = url.openStream();
	try {
	    return getDocumentReader().read(documentStream);
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

    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
	getCmdiValidator().validateMetadataDocument(document, errorHandler);
    }

    /**
     * Get the value of documentReader
     *
     * @return the value of documentReader
     */
    public MetadataDocumentReader<CMDIDocument> getDocumentReader() {
	return documentReader;
    }

    /**
     * @return the cmdiValidator
     */
    public MetadataValidator<CMDIDocument> getCmdiValidator() {
	return cmdiValidator;
    }

    /**
     * @param cmdiValidator the cmdiValidator to set
     */
    public void setCmdiValidator(MetadataValidator<CMDIDocument> cmdiValidator) {
	this.cmdiValidator = cmdiValidator;
    }
}
