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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URI;
import java.util.Collection;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.DirtyStateProvider;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface CMDIMetadataElement extends MetadataElementAttributeContainer<Attribute>, ReferencingMetadataElement<ResourceProxy>, DirtyStateProvider {

    /**
     * Adds a reference to a resource proxy in {@link #getMetadataDocument() this document} to this element
     *
     * @param id ID of resource proxy to add as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document.
     */
    ResourceProxy addDocumentResourceProxyReference(String id);

    /**
     * Creates or retrieves a <em>metadata</em> resource proxy for the specified uri with the specified mimetype, then adds a reference to
     * that proxy in this metadata element.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     */
    MetadataResourceProxy createMetadataReference(URI uri, String mimetype) throws MetadataException;

    /**
     * Creates or retrieves a <em>non-metadata</em> resource proxy for the specified uri with the specified mimetype, yhen adds a reference
     * to that proxy in this metadata element.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     */
    DataResourceProxy createResourceReference(URI uri, String mimetype) throws MetadataException;

    /**
     *
     * @return An <em>unmodifiable</em> collection of this element's attributes
     */
    Collection<Attribute> getAttributes();

    /**
     *
     * @return The CMDI document this container belongs to (more type specific than interface implemented)
     * @see nl.mpi.metadata.api.model.MetadataElement#getMetadataDocument()
     */
    CMDIDocument getMetadataDocument();

    CMDIProfileElement getType();

    /**
     *
     * @param id ID of resource proxy to remove as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document or not removed.
     */
    ResourceProxy removeDocumentResourceProxyReference(String id);

    ResourceProxy removeReference(ResourceProxy reference) throws MetadataException;

    void setDirty(boolean dirty);
}
