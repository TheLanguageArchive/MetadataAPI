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

import java.io.IOException;
import java.net.URL;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 *
 * @param <M> Metadata element type
 * @param <C> Metadata container type
 * @param <D> Metadata document type
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataAPI<M extends MetadataElement, C extends MetadataContainer<M>, D extends MetadataDocument<M>> {

    /**
     * Loads the metadata document at the specified location
     * @param uri Location of document to open
     * @return Openen document
     */
    D getMetadataDocument(URL url) throws IOException, IllegalArgumentException;

    /**
     * Creates a metadata document of the specified type
     * @param type Type of document to create
     * @return Newly instantiated document of specified type
     */
    D createMetadataDocument(MetadataDocumentType type);

    /**
     * Creates a metadata element of the specified type
     * @param parentElement Element to create new type in
     * @param type Type of element to create
     * @return Newly instantiated element of specified type
     */
    MetadataElement createMetadataElement(C parentElement, MetadataElementType type);

    /**
     * Validates a metadata document to its schema
     * @param document Document to validate
     * @return Whether document was successfully validated
     */
    void validateMetadataDocument(D document, ErrorHandler errorHandler) throws SAXException;

    /**
     * Removes an element from the document
     * @param element Element to remove
     * @return Removed element (null of none removed)
     */
    MetadataElement removeElement(M element) throws MetadataDocumentException;
}
