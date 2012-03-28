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
import nl.mpi.metadata.api.MetadataException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface ReferencingMetadataElement extends MetadataElement {

    Collection<? extends Reference> getReferences();

    /**
     * Creates a reference to a resource on this element
     * @param uri URI of new reference
     * @param mimetype MIME type of new reference
     * @return reference as it has been created on this element (null if none created)
     * @throws MetadataException in case any error occurs while create the reference
     */
    ResourceReference createResourceReference(URI uri, String mimetype) throws MetadataException;

    /**
     * Creates a reference to another metadata instance on this element
     * @param uri URI of new reference
     * @param mimetype MIME type of new reference
     * @return reference as it has been created on this element. Null if none has been created
     * @throws MetadataException in case any error occurs while create the reference
     */
    MetadataReference createMetadataReference(URI uri, String mimetype) throws MetadataException;

    /**
     * Removes a given resource reference from this element
     * @param reference reference to remove
     * @return the removed reference. Null if none has been removed
     * @throws MetadataException 
     */
    Reference removeReference(Reference reference) throws MetadataException;
}
