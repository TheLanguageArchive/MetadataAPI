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

import java.io.OutputStream;
import java.net.URI;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.model.MetadataDocument;
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

    public CMDIDocument getMetadataDocument(URI uri) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public CMDIDocument createMetadataDocument(MetadataDocumentType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement createMetadataElement(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean validateMetadataDocument(CMDIDocument document) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String insertElement(CMDIContainerMetadataElement parent, CMDIMetadataElement element) throws MetadataDocumentException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement removeElement(CMDIContainerMetadataElement parent, CMDIMetadataElement element) throws MetadataDocumentException {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
