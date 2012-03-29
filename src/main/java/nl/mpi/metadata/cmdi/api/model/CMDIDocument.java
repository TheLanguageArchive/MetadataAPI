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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ReferencingMetadataDocument;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 *
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocument extends CMDIContainerMetadataElement implements ReferencingMetadataDocument<CMDIMetadataElement, ResourceProxy> {

    private CMDIProfile profile;
    private URI fileLocation;
    private final Map<String, HeaderInfo> headerInfo;
    private final Map<String, ResourceProxy> resourceProxies;
    private final Collection<MetadataDocumentListener> listeners;

    /**
     * Construct an unsaved profile instance (no location associated)
     *
     * @param profile
     */
    public CMDIDocument(CMDIProfile profile) {
	this(profile, null);
    }

    /**
     * Create a profile instance that has a location associated
     *
     * @param profile
     * @param fileLocation
     */
    public CMDIDocument(CMDIProfile profile, URI fileLocation) {
	super(profile);

	this.profile = profile;
	this.fileLocation = fileLocation;

	this.headerInfo = new LinkedHashMap<String, HeaderInfo>(); // LinkedHashMap so that order is preserved
	this.resourceProxies = new LinkedHashMap<String, ResourceProxy>(); // LinkedHashMap so that order is preserved
	this.listeners = new HashSet<MetadataDocumentListener>();
    }

    @Override
    public CMDIProfile getType() {
	return profile;
    }

    public URI getFileLocation() {
	return fileLocation;
    }

    public synchronized void putHeaderInformation(HeaderInfo headerInfoItem) {
	headerInfo.put(headerInfoItem.getName(), headerInfoItem);
    }

    public synchronized HeaderInfo getHeaderInformation(String name) {
	return headerInfo.get(name);
    }

    public synchronized void removeHeaderInformation(String name) {
	headerInfo.remove(name);
    }

    /**
     *
     * @return An <em>unmodifiable</em> copy of the collection of header info entries
     */
    public synchronized Collection<HeaderInfo> getHeaderInformation() {
	return Collections.unmodifiableCollection(headerInfo.values());
    }

    /**
     * Gets the resource proxy with the specified id
     *
     * @param id ID of the resource proxy to retrieve
     * @return Resource proxy with the specified id or null if not found
     */
    public synchronized ResourceProxy getDocumentResourceProxy(String id) {
	return resourceProxies.get(id);
    }

    /**
     * Adds an existing resource proxy to the resource proxy map for this document
     *
     * @param resourceProxy resource proxy to add
     */
    public synchronized void addDocumentResourceProxy(ResourceProxy resourceProxy) {
	resourceProxies.put(resourceProxy.getId(), resourceProxy);
    }

    /**
     * Creates a new non-metadata resource proxy in this document. It will not be linked by any element including the document root node.
     *
     * @param uri URI for new resource proxy
     * @param mimetype MIME type for new resource proxy
     * @return newly created resource
     */
    public DataResourceProxy createDocumentResourceReference(URI uri, String mimetype) {
	DataResourceProxy resourceProxy = new DataResourceProxy(newUUID(), uri, mimetype);
	addDocumentResourceProxy(resourceProxy);
	return resourceProxy;
    }

    /**
     * Creates a new metadata resource proxy in this document. It will not be linked by any element including the document root node.
     *
     * @param uri URI for new resource proxy
     * @param mimetype MIME type for new resource proxy
     * @return newly created resource
     */
    public MetadataResourceProxy createDocumentMetadataReference(URI uri, String mimetype) {
	MetadataResourceProxy resourceProxy = new MetadataResourceProxy(newUUID(), uri, mimetype);
	addDocumentResourceProxy(resourceProxy);
	return resourceProxy;
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
    public synchronized void removeDocumentResourceProxy(String id) {
	resourceProxies.remove(id);
    }

    /**
     * Removes the specified resource proxy from the document. Will not attempt to remove all references.
     *
     * @param reference resource proxy to remove
     * @return removed resource proxy. Null if non removed.
     */
    public Reference removeDocumentReference(ResourceProxy reference) {
	removeDocumentResourceProxy(reference.getId());
	return reference;
    }

    /**
     *
     * @return an immutable list of all resource proxies present in the resource proxy map in this document
     */
    protected synchronized Collection<ResourceProxy> getDocumentResourceProxies() {
	return Collections.unmodifiableCollection(resourceProxies.values());
    }

    /**
     *
     * @return An immutable list of resource proxy defined in this document. This includes both referenced (from elements) and unreferenced
     * resource proxies.
     */
    public Collection<Reference> getDocumentReferences() {
	return Collections.<Reference>unmodifiableCollection(resourceProxies.values());
    }

    public synchronized void addMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.add(listener);
    }

    public synchronized void removeMetadataDocumentListener(MetadataDocumentListener listener) {
	listeners.remove(listener);
    }

    /**
     * @return an <em>unmodifiable</em> copy of the MetadataDocumentListeners collection
     */
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
}
