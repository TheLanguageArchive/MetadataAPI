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
import java.util.List;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.type.MetadataDocumentType;

/**
 * @param <M> Type of MetadataElement that can be contained in this document
 * @see MetadataElement
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataDocument<M extends MetadataElement> extends MetadataContainer<M>, MetadataElement {

    MetadataDocumentType getDocumentType();

    /**
     *
     * @return Location of the file this document represents (can be null)
     */
    URI getFileLocation();

    /**
     *
     * @param location Location of the file this document represents (can be null)
     */
    void setFileLocation(URI location);

    /**
     * @return An <em>unmodifiable list</em> of the header information in this document (can be null)
     *
     */
    List<HeaderInfo> getHeaderInformation();

    /**
     * Puts a header item in the document. If a header item with the same name already exists, it gets replaced by the provided one.
     * Header items are guaranteed to be inserted in the order as specified by the the {@link MetadataDocumentType} this document is an
     * instance of.
     *
     * @param headerInfoItem
     * @throws MetadataException if the {@link MetadataDocumentType} does not allow this header item (e.g. by its name)
     * @throws MetadataElementException if the header item could not be inserted into the document
     */
    void putHeaderInformation(HeaderInfo headerInfoItem) throws MetadataException;

    /**
     * Registers a {@link MetadataDocumentListener} for this document
     *
     * @param listener Listener to add
     */
    void addMetadataDocumentListener(MetadataDocumentListener listener);

    /**
     * Unregisters a {@link MetadataDocumentListener} from this document
     *
     * @param listener Listener to remove
     */
    void removeMetadataDocumentListener(MetadataDocumentListener listener);
}
