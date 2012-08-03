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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 *
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentImpl extends CMDIContainerMetadataElementImpl implements CMDIDocument {

    private final CMDIProfile profile;
    private final List<HeaderInfo> headerInfo;
    private final Map<String, ResourceProxy> resourceProxies;
    private final Map<Reference, Collection<CMDIMetadataElement>> resourceProxyReferences;
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

	this.headerInfo = new LinkedList<HeaderInfo>(); // LinkedHashMap so that order is preserved
	this.resourceProxies = new LinkedHashMap<String, ResourceProxy>(); // LinkedHashMap so that order is preserved
	this.resourceProxyReferences = new HashMap<Reference, Collection<CMDIMetadataElement>>();
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

    /**
     * Puts a header item in the document. If a header item with the same name already exists, it gets replaced by the provided one.
     * Header items are guaranteed to be inserted in the order as specified by the the {@link CMDIProfile} this document is an instance of.
     *
     * @param headerInfoItem
     * @throws CMDITypeException if the {@link CMDIProfile} does not allow this header item (by its name)
     * @throws MetadataElementException if the header item could not be inserted into the document
     */
    @Override
    public synchronized void putHeaderInformation(HeaderInfo headerInfoItem) throws CMDITypeException, MetadataElementException {
	if (profile.getHeaderNames().contains(headerInfoItem.getName())) {
	    HeaderInfo oldInfo = getHeaderInformation(headerInfoItem.getName());
	    if (oldInfo == null) {
		addNewHeaderInfo(headerInfoItem);
	    } else {
		replaceHeaderInfo(oldInfo, headerInfoItem);
	    }
	} else {
	    throw new CMDITypeException(profile, "Profile does not support header with name " + headerInfoItem.getName());
	}
    }

    /**
     * Adds a new item to the header info list
     *
     * @param headerInfoItem item to add
     * @throws MetadataElementException if adding the items fails
     */
    private void addNewHeaderInfo(HeaderInfo headerInfoItem) throws MetadataElementException {
	HeaderInfo insertBeforeHeaderInfo = getInsertBeforeHeaderInfo(headerInfoItem);
	if (insertBeforeHeaderInfo == null) {
	    // Insert at the end of the list
	    if (!headerInfo.add(headerInfoItem)) {
		throw new MetadataElementException(this, String.format("Failed to add header info item %1$s", headerInfoItem));
	    }
	} else {
	    // Insert in place
	    headerInfo.add(headerInfo.indexOf(insertBeforeHeaderInfo), headerInfoItem);
	}
    }

    private HeaderInfo getInsertBeforeHeaderInfo(HeaderInfo headerInfoItem) {
	final String itemName = headerInfoItem.getName();
	final List<String> profileHeaderNames = profile.getHeaderNames();
	for (int index = profileHeaderNames.indexOf(itemName) + 1; index < profileHeaderNames.size(); index++) {
	    // See if the document has an header item with this name
	    HeaderInfo nextItem = getHeaderInformation(profileHeaderNames.get(index));
	    if (nextItem != null) {
		// Item exist, new item should be inserted before this
		return nextItem;
	    }
	}
	return null;
    }

    /**
     * Replaces the old info by the new info, keeping the index of the old info
     *
     * @param oldInfo item to replace
     * @param newInfo new item
     */
    private void replaceHeaderInfo(HeaderInfo oldInfo, HeaderInfo newInfo) {
	int index = headerInfo.indexOf(oldInfo);
	headerInfo.remove(oldInfo);
	headerInfo.add(index, newInfo);
    }

    public synchronized HeaderInfo getHeaderInformation(String name) {
	for (HeaderInfo info : headerInfo) {
	    if (info.getName().equals(name)) {
		return info;
	    }
	}
	return null;
    }

    @Override
    public synchronized void removeHeaderInformation(String name) {
	HeaderInfo info = getHeaderInformation(name);
	if (info != null) {
	    headerInfo.remove(info);
	}
    }

    /**
     *
     * @return An <em>unmodifiable</em> copy of the collection of header info entries
     */
    @Override
    public synchronized List<HeaderInfo> getHeaderInformation() {
	return Collections.unmodifiableList(headerInfo);
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
    public synchronized ResourceProxy getDocumentReferenceByURI(URI uri) {
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
	final ResourceProxy resourceProxy = getDocumentReferenceByURI(uri);
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
    public synchronized Collection<MetadataElement> getResourceProxyReferences(Reference proxy) {
	final Collection<CMDIMetadataElement> references = resourceProxyReferences.get(proxy);
	if (references == null) {
	    return Collections.emptySet();
	} else {
	    return Collections.<MetadataElement>unmodifiableCollection(references);
	}
    }

    /**
     * Registers a metadata element as a reference container for the specified proxy
     *
     * @param proxy resource proxy that is referenced
     * @param referencingElement element that references the proxy
     */
    public synchronized void registerResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement) {
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
    public synchronized boolean unregisterResourceProxyReference(ResourceProxy proxy, CMDIMetadataElement referencingElement) {
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
	final ResourceProxy resourceProxy = getDocumentReferenceByURI(uri);
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
    public void setHandle(String handle) throws MetadataException {
	putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, handle));
    }

    /**
     *
     * @return Path of the root of the document
     */
    @Override
    protected final CharSequence getPathCharSequence() {
	return getType().getPath();
    }
}
