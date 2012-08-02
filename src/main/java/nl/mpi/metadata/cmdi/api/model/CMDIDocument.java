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
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HandleCarrier;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ReferencingMetadataDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 * An instance of a CMDI profile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 * @see CMDIProfile
 */
public interface CMDIDocument extends CMDIContainerMetadataElement, HandleCarrier, MetadataDocument<CMDIMetadataElement>, ReferencingMetadataDocument<CMDIMetadataElement, ResourceProxy> {

    /**
     * Adds an existing resource proxy to the resource proxy map for this document
     *
     * @param resourceProxy resource proxy to add
     */
    void addDocumentResourceProxy(ResourceProxy resourceProxy);

    /**
     * Gets the resource proxy with the specified id
     *
     * @param id ID of the resource proxy to retrieve
     * @return Resource proxy with the specified id or null if not found
     */
    ResourceProxy getDocumentResourceProxy(String id);

    /**
     * Gets the resource proxy with the specified URI
     *
     * @param uri URI of resource proxy to retrieve
     * @return Resource proxy with the specified URI or null if not found. If there are multiple with the same URI, the first one
     * encountered is returned
     */
    ResourceProxy getDocumentReferenceByURI(URI uri);

    /**
     * @return an <em>unmodifiable</em> copy of the MetadataDocumentListeners collection
     */
    Collection<MetadataDocumentListener> getMetadataDocumentListeners();

    CMDIProfile getType();

    CMDIProfile getDocumentType();

    void putHeaderInformation(HeaderInfo headerInfoItem) throws MetadataException;

    HeaderInfo getHeaderInformation(String name);

    /**
     * Removes a resource proxy from the resource proxy map for this document. Does not check if it is linked from any of the metadata
     * elements.
     *
     * @param id ID of resource proxy to remove
     */
    void removeDocumentResourceProxy(String id);

    void removeHeaderInformation(String name);

    MetadataResourceProxy createDocumentMetadataReference(URI uri, String mimetype) throws MetadataException;

    DataResourceProxy createDocumentResourceReference(URI uri, String mimetype) throws MetadataException;

    /**
     *
     * @param proxy resource proxy to get references for
     * @return an <em>immutable</em> collection of metadata elements that references the specified proxy. Can be an empty collection, never
     * null.
     */
    Collection<MetadataElement> getResourceProxyReferences(Reference proxy);

    /**
     * Registers a metadata element as a reference container for the specified proxy
     *
     * @param proxy resource proxy that is referenced
     * @param referencingElement element that references the proxy
     */
    void registerResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement);

    /**
     * Unregisters a metadata element as a reference container for the specified proxy
     *
     * @param proxy resource proxy that is referenced
     * @param referencingElement element that references the proxy
     */
    boolean unregisterResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement);
}
