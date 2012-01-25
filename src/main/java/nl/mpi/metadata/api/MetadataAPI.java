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
package nl.mpi.metadata.api;

import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import java.net.URI;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataAPI {

    /**
     * Loads the metadata document at the specified location
     * @param uri Location of document to open
     * @return Openen document
     */
    MetadataDocument getMetadataDocument(URI uri);

    /**
     * Creates a metadata document of the specified type
     * @param type Type of document to create
     * @return Newly instantiated document of specified type
     */
    MetadataDocument createMetadataDocument(MetadataDocumentType type);

    /**
     * Creates a metadata element of the specified type
     * @param type Type of element to create
     * @return Newly instantiated element of specified type
     */
    MetadataElement createMetadataElement(MetadataElementType type);

    /**
     * Validates a metadata document to its schema
     * @param document Document to validate
     * @return Whether document was successfully validated
     */
    boolean validateMetadataDocument(MetadataDocument document);

    /**
     * Inserts an element in the specified location
     * @param path Path of element to insert specified element into
     * @param element Element to insert
     * @return Resulting path of the inserted element
     */
    String insertElement(MetadataElement parent, MetadataElement element) throws MetadataDocumentException;

    /**
     * Removes an element from the document
     * @param path Path of element to remove
     * @return Removed element (null of none removed)
     */
    MetadataElement removeElement(MetadataElement parent, MetadataElement element) throws MetadataDocumentException;
}
