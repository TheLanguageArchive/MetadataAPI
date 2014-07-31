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
import java.net.URL;
import java.util.Collection;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HandleCarrier;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ReferencingMetadataDocument;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 * An instance of a CMDI profile
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 * @see CMDIProfile
 */
public interface CMDIDocument extends CMDIContainerMetadataElement, HandleCarrier, MetadataDocument<CMDIMetadataElement>, ReferencingMetadataDocument<CMDIMetadataElement, ResourceProxy> {

    /**
     * Adds an existing resource proxy to the resource proxy map for this document.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
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
     * Gets the resource proxy with the specified location (URL)
     *
     * @param location URL of resource proxy to retrieve
     * @return Resource proxy with the specified location or null if not found. If there are multiple with the same location, the first one
     * encountered is returned
     */
    ResourceProxy getDocumentReferenceByLocation(URL location);

    /**
     * @return an <em>unmodifiable</em> copy of the MetadataDocumentListeners collection
     */
    Collection<MetadataDocumentListener> getMetadataDocumentListeners();

    CMDIProfile getType();

    CMDIProfile getDocumentType();

    /**
     * Puts a header item in the document. If a header item with the same name already exists, it gets replaced by the provided one.
     * Header items are guaranteed to be inserted in the order as specified by the the {@link CMDIProfile} this document is an instance of.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param headerInfoItem
     * @throws CMDITypeException if the {@link CMDIProfile} does not allow this header item (by its name)
     * @throws MetadataElementException if the header item could not be inserted into the document
     */
    void putHeaderInformation(HeaderInfo headerInfoItem) throws MetadataException;

    HeaderInfo getHeaderInformation(String name);

    /**
     * Removes a resource proxy from the resource proxy map for this document. Does not check if it is linked from any of the metadata
     * elements.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param id ID of resource proxy to remove
     */
    void removeDocumentResourceProxy(String id);

    /**
     * Removes a header item from the document.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param name
     */
    void removeHeaderInformation(String name);

    MetadataResourceProxy createDocumentMetadataReference(URI uri, String mimetype) throws MetadataException;
    
    MetadataResourceProxy createDocumentMetadataReference(URI uri, URL location, String mimetype) throws MetadataException;

    /**
     * Creates and adds a new non-metadata resource proxy in this document if it does not exist yet. If a reference with the same URI
     * does already exist, it will be retrieved. In this case, the MIME type will be ignored.
     * New references will not be linked by any element including the document root node.
     *
     * @param uri URI for resource proxy
     * @param type type for resource proxy, null for default ({@link CMDIConstants#CMD_RESOURCE_PROXY_TYPE_RESOURCE})
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link DataResourceProxy} (i.e. is a
     * {@link MetadataResourceProxy})
     */
    DataResourceProxy createDocumentResourceReference(URI uri, String type, String mimetype) throws MetadataException;
    
    /**
     * @see CMDIDocument#createDocumentResourceReference(java.net.URI, java.lang.String, java.lang.String)
     * 
     * @param uri URI for resource proxy
     * @param location local URL for resource proxy
     * @param type type for resource proxy, null for default ({@link CMDIConstants#CMD_RESOURCE_PROXY_TYPE_RESOURCE})
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link DataResourceProxy} (i.e. is a
     * {@link MetadataResourceProxy})
     */
    DataResourceProxy createDocumentResourceReference(URI uri, URL location, String type, String mimetype) throws MetadataException;

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

    /**
     * Sets the dirty state of this document as a metadata element to false as well as the {@link #getHeaderDirtyState() header's} and
     * {@link #getResourceProxiesDirtyState() } dirty states;
     */
    void setAllClean();

    /**
     *
     * @return modifiable state that indicates whether the list of resource proxies has changed
     */
    SettableDirtyStateProvider getHeaderDirtyState();

    /**
     *
     * @return modifiable state that indicates whether the list of resource proxies or one of its items has unsaved changes
     */
    SettableDirtyStateProvider getResourceProxiesDirtyState();
}
