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
package nl.mpi.metadata.api.model;

import java.net.URI;
import java.util.Collection;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.type.MetadataDocumentType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataDocument {

    /**
     * 
     * @return Type of the document
     */
    MetadataDocumentType getType();

    /**
     * 
     * @return Location of the file this document represents (can be null)
     */
    URI getFileLocation();

    /**
     * @return Header information in this document (can be null)
     * 
     */
    Collection<HeaderInfo> getHeaderInformation();

    /**
     * 
     * @param path Path for requested element
     * @return Requested element if found, otherwise null
     */
    MetadataElement getElement(String path);

    /**
     * Inserts an element in the specified location
     * @param path Path of element to insert specified element into
     * @param element Element to insert
     * @return Resulting path of the inserted element
     */
    String insertElement(String path, MetadataElement element);

    /**
     * Removes an element from the document
     * @param path Path of element to remove
     * @return Removed element (null of none removed)
     */
    MetadataElement removeElement(String path);

    /**
     * Registers a @see MetadataDocumentListener for this document
     * @param listener Listener to add
     */
    void addMetadataDocumentListener(MetadataDocumentListener listener);

    /**
     * Unregisters a @see MetadataDocumentListener from this document
     * @param listener Listener to remove
     */
    void removeMetadataDocumentListener(MetadataDocumentListener listener);
}
