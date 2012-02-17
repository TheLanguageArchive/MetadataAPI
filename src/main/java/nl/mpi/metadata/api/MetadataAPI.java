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
 * @param <DT> Metadata document type class
 * @param <M> Metadata element class
 * @param <C> Metadata container class
 * @param <D> Metadata document class
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataAPI<DT extends MetadataDocumentType, MT extends MetadataElementType, M extends MetadataElement, C extends MetadataContainer<M>, D extends MetadataDocument<M>> {

    /**
     * Loads the metadata document at the specified location
     * @param uri location of document to open
     * @return opened document
     */
    D getMetadataDocument(URL url) throws IOException, MetadataException;

    /**
     * Creates a metadata document of the specified type
     * @param type type of document to create
     * @return newly instantiated document of specified type
     */
    D createMetadataDocument(DT type) throws MetadataException;

    /**
     * Creates a metadata element of the specified type
     * @param parentElement element to create new type in
     * @param type type of element to create
     * @return newly instantiated element of specified type
     */
    MetadataElement createMetadataElement(C parentElement, MT type) throws MetadataElementException;

    /**
     * Validates a metadata document to its schema
     * @param document document to validate
     * @return whether document was successfully validated
     */
    void validateMetadataDocument(D document, ErrorHandler errorHandler) throws SAXException;

    /**
     * Removes an element from the document
     * @param element element to remove
     * @return whether an element was removed from its parent
     */
    boolean removeElement(M element) throws MetadataElementException;
}
