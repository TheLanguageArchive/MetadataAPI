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
package nl.mpi.metadata.cmdi.api.model.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 *
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentImpl extends CMDIContainerMetadataElementImpl implements CMDIDocument {

    private final CMDIProfile profile;
    private final Map<String, HeaderInfo> headerInfo;
    private final Map<String, ResourceProxy> resourceProxies;
    private final Map<ResourceProxy, Collection<CMDIMetadataElement>> resourceProxyReferences;
    private final Collection<MetadataDocumentListener> listeners;
    private URI fileLocation;

    /**
     * Construct an unsaved profile instance (no location associated)
     *
     * @param profile
     */
    public CMDIDocumentImpl(CMDIProfile profile) {
	this(profile, null);
    }

    /**
     * Create a profile instance that has a location associated
     *
     * @param profile
     * @param fileLocation
     */
    public CMDIDocumentImpl(CMDIProfile profile, URI fileLocation) {
	super(profile);

	this.profile = profile;
	this.fileLocation = fileLocation;

	this.headerInfo = new LinkedHashMap<String, HeaderInfo>(); // LinkedHashMap so that order is preserved
	this.resourceProxies = new LinkedHashMap<String, ResourceProxy>(); // LinkedHashMap so that order is preserved
	this.resourceProxyReferences = new HashMap<ResourceProxy, Collection<CMDIMetadataElement>>();
	this.listeners = new HashSet<MetadataDocumentListener>();
    }

    @Override
    public CMDIProfile getType() {
	return profile;
    }

    @Override
    public CMDIProfile getDocumentType() {
	return getType();
    }

    @Override
    public URI getFileLocation() {
	return fileLocation;
    }

    @Override
    public void setFileLocation(URI location) {
	this.fileLocation = location;
    }

    @Override
    public synchronized void putHeaderInformation(HeaderInfo headerInfoItem) {
	headerInfo.put(headerInfoItem.getName(), headerInfoItem);
    }

    public synchronized HeaderInfo getHeaderInformation(String name) {
	return headerInfo.get(name);
    }

    @Override
    public synchronized void removeHeaderInformation(String name) {
	headerInfo.remove(name);
    }

    /**
     *
     * @return An <em>unmodifiable</em> copy of the collection of header info entries
     */
    @Override
    public synchronized Collection<HeaderInfo> getHeaderInformation() {
	return Collections.unmodifiableCollection(headerInfo.values());
    }

    /**
     * Gets the resource proxy with the specified id
     *
     * @param id ID of the resource proxy to retrieve
     * @return Resource proxy with the specified id or null if not found
     */
    @Override
    public synchronized ResourceProxy getDocumentResourceProxy(String id) {
	return resourceProxies.get(id);
    }

    /**
     * Gets the resource proxy with the specified URI
     *
     * @param uri URI of resource proxy to retrieve
     * @return Resource proxy with the specified URI or null if not found. If there are multiple with the same URI, the first one
     * encountered is returned
     */
    @Override
    public synchronized ResourceProxy getDocumentResourceProxy(URI uri) {
	for (ResourceProxy proxy : resourceProxies.values()) {
	    if (proxy.getURI().equals(uri)) {
		return proxy;
	    }
	}
	return null;
    }

    /**
     * Adds an existing resource proxy to the resource proxy map for this document
     *
     * @param resourceProxy resource proxy to add
     */
    @Override
    public synchronized void addDocumentResourceProxy(ResourceProxy resourceProxy) {
	resourceProxies.put(resourceProxy.getId(), resourceProxy);
    }

    /**
     * Creates a new non-metadata resource proxy in this document if it does not exist yet. If a reference with the same URI already exist,
     * it will be retrieved. In this case, the MIME type will be ignored!
     * New references will not be linked by any element including the document root node.
     *
     * @param uri URI for resource proxy
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link DataResourceProxy} (i.e. is a {@link MetadataResourceProxy})
     */
    @Override
    public synchronized DataResourceProxy createDocumentResourceReference(URI uri, String mimetype) throws MetadataException {
	final ResourceProxy resourceProxy = getDocumentResourceProxy(uri);
	if (resourceProxy == null) {
	    final DataResourceProxy newResourceProxy = new DataResourceProxy(newUUID(), uri, mimetype);
	    addDocumentResourceProxy(newResourceProxy);
	    return newResourceProxy;
	} else {
	    if (resourceProxy instanceof DataResourceProxy) {
		return (DataResourceProxy) resourceProxy;
	    } else {
		throw new MetadataException(String.format("Resource proxy conflict: %1$s found while trying to add DataResourceProxy", resourceProxy.getClass()));
	    }
	}
    }

    /**
     *
     * @param proxy resource proxy to get references for
     * @return an <em>immutable</em> collection of metadata elements that references the specified proxy. Can be an empty collection, never
     * null.
     */
    protected synchronized Collection<CMDIMetadataElement> getResourceProxyReferences(ResourceProxy proxy) {
	final Collection<CMDIMetadataElement> references = resourceProxyReferences.get(proxy);
	if (references == null) {
	    return Collections.emptySet();
	} else {
	    return Collections.unmodifiableCollection(references);
	}
    }

    /**
     * Registers a metadata element as a reference container for the specified proxy
     *
     * @param proxy resource proxy that is referenced
     * @param referencingElement element that references the proxy
     */
    protected synchronized void registerResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement) {
	Collection<CMDIMetadataElement> references = resourceProxyReferences.get(proxy);
	if (references == null) {
	    references = new HashSet<CMDIMetadataElement>();
	    resourceProxyReferences.put(proxy, references);
	}
	references.add(referencingElement);
    }

    /**
     * Unregisters a metadata element as a reference container for the specified proxy
     *
     * @param proxy resource proxy that is referenced
     * @param referencingElement element that references the proxy
     */
    protected synchronized boolean unregisterResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement) {
	final Collection<CMDIMetadataElement> references = resourceProxyReferences.get(proxy);
	if (references != null) {
	    return references.remove(referencingElement);
	}
	return false;
    }

    /**
     * Creates a new metadata resource proxy in this document if it does not exist yet. If a reference with the same URI already exist,
     * it will be retrieved. In this case, the MIME type will be ignored!
     * New references will not be linked by any element including the document root node.
     *
     * @param uri URI for resource proxy
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link MetadataResourceProxy} (i.e. is a {@link DataResourceProxy})
     */
    @Override
    public MetadataResourceProxy createDocumentMetadataReference(URI uri, String mimetype) throws MetadataException {
	final ResourceProxy resourceProxy = getDocumentResourceProxy(uri);
	if (resourceProxy == null) {
	    final MetadataResourceProxy newResourceProxy = new MetadataResourceProxy(newUUID(), uri, mimetype);
	    addDocumentResourceProxy(newResourceProxy);
	    return newResourceProxy;
	} else {
	    if (resourceProxy instanceof MetadataResourceProxy) {
		return (MetadataResourceProxy) resourceProxy;
	    } else {
		throw new MetadataException(String.format("Resource proxy conflict: %1$s found while trying to add MetadataResourceProxy", resourceProxy.getClass()));
	    }
	}
    }

    private static String newUUID() {
	return UUID.randomUUID().toString();
    }

    /**
     * Removes a resource proxy from the resource proxy map for this document. Does not check if it is linked from any of the metadata
     * elements.
     *
     * @param id ID of resource proxy to remove
     */
    @Override
    public synchronized void removeDocumentResourceProxy(String id) {
	resourceProxies.remove(id);
    }

    /**
     * Removes the specified resource proxy from the document. Will not attempt to remove all references.
     *
     * @param reference resource proxy to remove
     * @return removed resource proxy. Null if non removed.
     */
    @Override
    public ResourceProxy removeDocumentReference(ResourceProxy reference) {
	removeDocumentResourceProxy(reference.getId());
	return reference;
    }

    /**
     *
     * @return An immutable list of resource proxy defined in this document. This includes both referenced (from elements) and unreferenced
     * resource proxies.
     */
    @Override
    public Collection<Reference> getDocumentReferences() {
	return Collections.<Reference>unmodifiableCollection(resourceProxies.values());
    }

    @Override
    public synchronized void addMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.add(listener);
    }

    @Override
    public synchronized void removeMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.remove(listener);
    }

    /**
     * @return an <em>unmodifiable</em> copy of the MetadataDocumentListeners collection
     */
    @Override
    public Collection<MetadataDocumentListener> getMetadataDocumentListeners() {
	return Collections.unmodifiableCollection(listeners);
    }

    /**
     *
     * @return This document
     */
    @Override
    public CMDIDocument getMetadataDocument() {
	return this;
    }

    @Override
    public String getHandle() {
	HeaderInfo handleInfo = getHeaderInformation(CMDIConstants.CMD_HEADER_MD_SELF_LINK);
	if (handleInfo != null) {
	    if (handleInfo.getValue() != null) {
		return handleInfo.getValue().toString();
	    }
	}
	return null;
    }

    @Override
    public void setHandle(String handle) {
	putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, handle));
    }

    /**
     *
     * @return Path of the root of the document
     */
    @Override
    protected final String getPathCharSequence() {
	return getType().getPathString();
    }
}
