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
package nl.mpi.metadata.api.model;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import nl.mpi.metadata.api.MetadataException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface ReferencingMetadataDocument<M extends MetadataElement, R extends Reference> extends MetadataDocument<M> {

    /**
     *
     * @return The number of {@link R references} that exist in this document
     */
    int getDocumentReferencesCount();

    /**
     *
     * @return All references that exist in this document (can be null)
     */
    List<Reference> getDocumentReferences();

    /**
     * Creates a reference to a resource on this element
     *
     * @param uri URI of new reference
     * @param type The type of the reference that should be specified (if supported)
     * @param mimetype MIME type of new reference that should be specified (if supported)
     * @return reference as it has been created on this element (null if none created)
     * @throws MetadataException in case any error occurs while create the reference
     */
    ResourceReference createDocumentResourceReference(URI uri, String type, String mimetype) throws MetadataException;

    /**
     * Creates a reference to another metadata instance on this element
     *
     * @param uri URI of new reference
     * @param mimetype MIME type of new reference
     * @return reference as it has been created on this element. Null if none has been created
     * @throws MetadataException in case any error occurs while create the reference
     */
    MetadataReference createDocumentMetadataReference(URI uri, String mimetype) throws MetadataException;

    /**
     * Removes a given resource reference from this element
     *
     * @param reference reference to remove
     * @return the removed reference. Null if none has been removed
     * @throws MetadataException
     */
    R removeDocumentReference(R reference) throws MetadataException;

    /**
     * Gets all metadata elements that refer the specified reference
     *
     * @param reference resource proxy to get references for
     * @return an <em>immutable</em> collection of metadata elements that references the specified reference. Can be an empty collection,
     * never
     * null.
     */
    Collection<MetadataElement> getResourceProxyReferences(Reference reference);

    R getDocumentReferenceByURI(URI uri);
}
