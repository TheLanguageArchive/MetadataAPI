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
import java.net.URL;
import java.util.ArrayList;
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
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.api.util.HandleUtil;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.model.SettableDirtyStateProvider;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A CMDI metadata document. Instance of a CMDIProfile
 *
 * @see CMDIProfile
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIDocumentImpl extends CMDIContainerMetadataElementImpl implements CMDIDocument {

    private static final Logger logger = LoggerFactory.getLogger(CMDIDocumentImpl.class);
    private final CMDIProfile profile;
    private final List<HeaderInfo> headerInfo;
    private final Map<String, ResourceProxy> resourceProxies;
    private final Map<Reference, Collection<CMDIMetadataElement>> resourceProxyReferences;
    private final Collection<MetadataDocumentListener> listeners;
    private final SettableDirtyStateProvider headerDirtyState;
    private final SettableDirtyStateProvider resourceProxiesDirtyState;
    private URI fileLocation;
    private final HandleUtil handleUtil = new HandleUtil();

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
     * @param strategy
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

	this.headerDirtyState = new SettableDirtyStateProviderImpl(true);
	this.resourceProxiesDirtyState = new ResourceProxyDirtyStateProvider(true);
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
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param headerInfoItem
     * @throws CMDITypeException if the {@link CMDIProfile} does not allow this header item (by its name)
     * @throws MetadataElementException if the header item could not be inserted into the document
     */
    @Override
    public synchronized void putHeaderInformation(HeaderInfo headerInfoItem) throws CMDITypeException, MetadataElementException {
	if (profile.getHeaderNames().contains(headerInfoItem.getName())) {
	    HeaderInfo oldInfo = getHeaderInformation(headerInfoItem.getName());
	    if (oldInfo == null /* or multiple headers allowed */) {
		logger.debug("Creating new header {} on {}", headerInfoItem, this);
		addNewHeaderInfo(headerInfoItem);
	    } else {
		logger.debug("Replacing old header {} with {} on {}", oldInfo, headerInfoItem, this);
		replaceHeaderInfo(oldInfo, headerInfoItem);
	    }
	    headerDirtyState.setDirty(true);
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

    /**
     * Removes a header item from the document.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param name
     */
    @Override
    public synchronized void removeHeaderInformation(String name) {
	HeaderInfo info = getHeaderInformation(name);
	if (info != null) {
	    headerInfo.remove(info);
	    headerDirtyState.setDirty(true);
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
     * Gets the resource proxy with the specified URI
     *
     * @param uri URI of resource proxy to retrieve
     * @return Resource proxy with the specified URI or null if not found. If there are multiple with the same URI, the first one
     * encountered is returned
     */
    @Override
    public synchronized ResourceProxy getDocumentReferenceByLocation(URL location) {
        for(ResourceProxy proxy : resourceProxies.values()) {
            if(proxy.getLocation() == null) {
                continue;
            }
            if(proxy.getLocation().equals(location)) {
                return proxy;
            }
        }
        return null;
    }

    /**
     * Adds an existing resource proxy to the resource proxy map for this document.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param resourceProxy resource proxy to add
     */
    @Override
    public synchronized void addDocumentResourceProxy(ResourceProxy resourceProxy) {
	resourceProxies.put(resourceProxy.getId(), resourceProxy);
	resourceProxiesDirtyState.setDirty(true);
    }

    /**
     * Creates a resource proxy in the document <em>without a location 
     * attribute</em> (this can be set post-hoc via {@link 
     * DataResourceProxy#setLocation(java.net.URL) }
     * @param uri
     * @param type
     * @param mimetype
     * @return
     * @throws MetadataException 
     */
    @Override
    public synchronized DataResourceProxy createDocumentResourceReference(URI uri, String type, String mimetype) throws MetadataException {
	return newResourceReference(uri, null, type, mimetype);
    }
    
    @Override
    public synchronized DataResourceProxy createDocumentResourceReference(URI uri, URL location, String type, String mimetype) throws MetadataException {
        return newResourceReference(uri, location, type, mimetype);
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
     * The resource proxy is created <em>without a location attribute</em> (this
     * can be set post-hoc via {@link MetadataResourceProxy#setLocation(java.net.URL) }
     *
     * @param uri URI for resource proxy
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link MetadataResourceProxy} (i.e. is a
     * {@link DataResourceProxy})
     */
    @Override
    public MetadataResourceProxy createDocumentMetadataReference(URI uri, String mimetype) throws MetadataException {
	return newMetadataReference(uri, null, mimetype);
    }
    
    /**
     * @see CMDIDocumentImpl#createDocumentMetadataReference(java.net.URI, java.lang.String)
     * 
     * @param uri URI for resource proxy
     * @param location local URL for resource proxy
     * @param mimetype MIME type for resource proxy
     * @return newly created resource or existing resource with specified URI
     * @throws MetadataException if resource with specified URI already exists but is not a {@link MetadataResourceProxy} (i.e. is a
     * {@link DataResourceProxy})
     */
    @Override
    public MetadataResourceProxy createDocumentMetadataReference(URI uri, URL location, String mimetype) throws MetadataException {
        return newMetadataReference(uri, location, mimetype);
    }

    /**
     *
     * @param prefix will be prepended to the unique ID. Should not be empty or start with a number, otherwise violates
     * specification of XML id type!
     * @return the string resulting from the concatenation of the prefix and the string representation of a random {@link UUID }
     */
    private static String newResourceProxyId(String prefix) {
	return prefix + UUID.randomUUID().toString();
    }

    /**
     * Removes a resource proxy from the resource proxy map for this document. Does not check if it is linked from any of the metadata
     * elements.
     *
     * Sets this document's {@link #isDirty()  dirty state} to true
     *
     * @param id ID of resource proxy to remove
     */
    @Override
    public synchronized void removeDocumentResourceProxy(String id) {
	resourceProxies.remove(id);
	resourceProxiesDirtyState.setDirty(true);
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

    @Override
    public int getDocumentReferencesCount() {
	return resourceProxies.size();
    }

    /**
     *
     * @return An immutable list of resource proxy defined in this document. This includes both referenced (from elements) and unreferenced
     * resource proxies.
     */
    @Override
    public List<Reference> getDocumentReferences() {
	final Collection<ResourceProxy> proxies = resourceProxies.values();
	if (proxies instanceof List) {
	    return Collections.<Reference>unmodifiableList((List) proxies);
	} else {
	    return Collections.<Reference>unmodifiableList(new ArrayList<Reference>(proxies));
	}
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

    /**
     *
     * @return URI representation of the handle of this object, typically of scheme 'hdl' (never 'http'!)
     */
    @Override
    public URI getHandle() {
	HeaderInfo handleInfo = getHeaderInformation(CMDIConstants.CMD_HEADER_MD_SELF_LINK);
	if (handleInfo != null) {
	    if (handleInfo.getValue() != null) {
		return handleUtil.createHandleUri(handleInfo.getValue().toString());
	    }
	}
	return null;
    }

    /**
     * Sets the handle of this object
     *
     * @param handle handle to set, has to be a URL with scheme 'hdl'
     * @throws IllegalArgumentException if the provided handle is of a format that cannot be converted into a handle for this object
     * @throws If the API fails to set the handle because of some internal error
     */
    @Override
    public void setHandle(URI handle) throws MetadataException {
	if (handleUtil.isHandleUri(handle)) {
	    logger.debug("Setting handle of {} to {}", this, handle);
	    putHeaderInformation(new HeaderInfo(CMDIConstants.CMD_HEADER_MD_SELF_LINK, handle.toString()));
	} else {
	    throw new IllegalArgumentException("Illegal handle URI: " + handle.toString());
	}
    }

    /**
     *
     * @return Path of the root of the document
     */
    @Override
    protected final CharSequence getPathCharSequence() {
	return getType().getPath();
    }

    /**
     * Sets the dirty state of this document as a metadata element to false as well as the {@link #getHeaderDirtyState() header's} and
     * {@link #getResourceProxiesDirtyState() } dirty states;
     */
    public void setAllClean() {
	logger.trace("Marking {} all clean", this);
	setDirty(false);
	headerDirtyState.setDirty(false);
	resourceProxiesDirtyState.setDirty(false);
    }

    public SettableDirtyStateProvider getHeaderDirtyState() {
	return headerDirtyState;
    }

    public SettableDirtyStateProvider getResourceProxiesDirtyState() {
	return resourceProxiesDirtyState;
    }

    
    private MetadataResourceProxy newMetadataReference(URI uri, URL location, String mimetype) throws MetadataException {
        final ResourceProxy resourceProxy = getDocumentReferenceByURI(uri);
	if (resourceProxy == null) {
	    final MetadataResourceProxy newResourceProxy = new MetadataResourceProxy(newResourceProxyId("m"), uri, location, mimetype);
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
    
    private DataResourceProxy newResourceReference(URI uri, URL location, String type, String mimetype) throws MetadataException {
        final ResourceProxy resourceProxy = getDocumentReferenceByURI(uri);
	if (resourceProxy == null) {
	    if (type == null) {
		// null type should fall back to default
		type = CMDIConstants.CMD_RESOURCE_PROXY_TYPE_RESOURCE;
	    }
	    final DataResourceProxy newResourceProxy = new DataResourceProxy(newResourceProxyId("r"), uri, location, type, mimetype);
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
     * Dirty state provider for the headers. Has a general state and proxies for the state of the individual headers.
     */
    private class ResourceProxyDirtyStateProvider extends SettableDirtyStateProviderImpl {

	public ResourceProxyDirtyStateProvider(boolean dirty) {
	    super(dirty);
	}

	@Override
	public boolean isDirty() {
	    return super.isDirty() || documentHasDirtyHeaders();
	}

	private boolean documentHasDirtyHeaders() {
	    for (ResourceProxy proxy : resourceProxies.values()) {
		if (proxy.isDirty()) {
		    return true;
		}
	    }
	    return false;
	}

	@Override
	public void setDirty(boolean dirty) {
	    super.setDirty(dirty);
	    if (!dirty) {
		// Clean state should trickle down (setting dirty will already affect entire state)
		for (ResourceProxy proxy : resourceProxies.values()) {
		    proxy.setDirty(dirty);
		}
	    }
	}
    }
    
    @Override
    public String getDisplayValue() {
        String displayValue = null;
        //for (MetadataElement child : getChildren()) {
        //    MetadataElementType type = child.getType();
         //   if(type instanceof ElementType) {
           //
        displayValue = super.getDisplayValue();
        //        break;
          //  } else if(type instanceof ComponentType) {
            //    displayValue = child.getDisplayValue();
              //  break;
           // }
       // }
	return displayValue;
    }
    
}
