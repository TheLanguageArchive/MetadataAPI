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
package nl.mpi.metadata.api;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.api.dom.DomBuildingMode;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.MetadataElementAttribute;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.api.type.MetadataElementType;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 *
 * @param <DT> Metadata document type class
 * @param <C> Metadata container class
 * @param <D> Metadata document class
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataAPI<DT extends MetadataDocumentType, MT extends MetadataElementType, AT extends MetadataElementAttributeType, A extends MetadataElementAttribute, C extends MetadataContainer, D extends MetadataDocument> {

    /**
     * Loads the metadata document type (profile) at the specified location
     *
     * @param uri location of the document type to open
     * @return loaded document type
     * @throws IOException in case of an I/O exception while reading the document type from the location
     * @throws MetadataException in case of a processing error while loading the document type
     */
    MetadataDocumentType getMetadataDocumentType(URI uri) throws IOException, MetadataException;

    /**
     * Loads the metadata document at the specified location
     *
     * @param url location of document to open
     * @return loaded document
     * @throws IOException in case of an I/O exception while reading the document from the location
     * @throws MetadataException in case of a processing error while loading the document
     */
    MetadataDocument getMetadataDocument(URL url) throws IOException, MetadataException;

    /**
     * Creates a metadata document of the specified type using building mode {@link DomBuildingMode#MANDATORY}
     *
     * @param type type of document to create
     * @return newly instantiated document of specified type
     * @throws MetadataException in case of a processing error while creating the document
     * @see #createMetadataDocument(nl.mpi.metadata.api.type.MetadataDocumentType, nl.mpi.metadata.api.dom.DomBuildingMode)
     */
    MetadataDocument createMetadataDocument(DT type) throws MetadataException;

    /**
     * Creates a metadata document of the specified type using the specified building mode
     *
     * @param type type of document to create
     * @param buildingMode building mode for constructing the new document
     * @return newly instantiated document of specified type
     * @throws MetadataException in case of a processing error while creating the document
     */
    MetadataDocument createMetadataDocument(DT type, DomBuildingMode buildingMode) throws MetadataException;

    /**
     * Creates a new element of the specified type and adds it to the specified container
     *
     * @param container element container to add the new element to
     * @param elementType type of the new element to be added
     * @return newly created element
     * @throws MetadataException
     * @throws MetadataElementException if the specified child type is not compatible with the type of the container
     */
    MetadataElement insertMetadataElement(C container, MT elementType) throws MetadataException, MetadataElementException;

    /**
     * Creates a new attribute of the specified type and adds it to the specified container
     *
     * @param container attribute container to add the new element to
     * @param attributeType type of the new element to be added
     * @return newly created element
     * @throws MetadataException
     * @throws MetadataElementException if the specified attribute type is not compatible with the type of the attribute container
     */
    MetadataElementAttribute insertAttribute(MetadataElementAttributeContainer<A> container, AT attributeType) throws MetadataException, MetadataElementException;

    /**
     * Validates a metadata document to its schema
     *
     * @param document document to validate
     */
    void validateMetadataDocument(D document, ErrorHandler errorHandler) throws SAXException;

    void writeMetadataDocument(D document, StreamResult target) throws IOException, TransformerException, MetadataException;
}
