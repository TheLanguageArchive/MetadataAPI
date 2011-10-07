/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.workspace;

import java.io.OutputStream;
import java.net.URI;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class GenericMetadataWorkspace implements MetadataWorkspace {

    private MetadataAPI api;

    public GenericMetadataWorkspace(MetadataAPI api) {
	this.api = api;
    }

    public Iterable<MetadataDocument> getOpenMetadataDocuments() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataDocument openMetadataDocument(URI uri) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void closeMetadataDocument(MetadataDocument document) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void saveMetadataDocument(OutputStream os, MetadataDocument document) {
	api.writeMetadataDocument(os, document);
    }

    public void closeAllMetadataDocuments() {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
