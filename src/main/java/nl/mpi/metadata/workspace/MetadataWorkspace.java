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
import nl.mpi.metadata.api.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataWorkspace {

    /**
     * 
     * @return All metadata documents that are currently opened
     */
    Iterable<MetadataDocument> getOpenMetadataDocuments();

    /**
     * Opens metadata document if not already openend, and retrieves it
     * @param uri URI of document to open
     * @return Open MetadataDocument 
     */
    MetadataDocument openMetadataDocument(URI uri);

    /**
     * Closes specified metadata document, i.e. clears it from in-memory storage
     * @param document Metadata document to close
     */
    void closeMetadataDocument(MetadataDocument document);

    /**
     * Saves opened metadata document
     * @param os Stream to write to
     * @param document Document to save
     */
    void saveMetadataDocument(OutputStream os, MetadataDocument document);

    /**
     * Closes all open metadata documents
     */
    void closeAllMetadataDocuments();
}
