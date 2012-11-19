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
package nl.mpi.metadata.cmdi.api.model.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElementImpl implements CMDIMetadataElement {

    private final static Logger logger = LoggerFactory.getLogger(CMDIMetadataElementImpl.class);
    private final Collection<Attribute> attributes;
    private final List<ResourceProxy> resourceProxies;
    private CharSequence pathCharSequence = null;
    private boolean dirty = true;

    /**
     * Creates a new CMDI metadata element implementation. {@link #isDirty()  Dirty state} will be set to true initially!
     *
     * @see #isDirty()
     */
    protected CMDIMetadataElementImpl() {
	this.attributes = new HashSet<Attribute>();
	this.resourceProxies = new ArrayList<ResourceProxy>();
    }

    /**
     * Adds an attribute.
     * Sets this element's {@link #isDirty()  dirty state} to true if successful
     *
     * @param attribute
     * @return
     */
    @Override
    public synchronized boolean addAttribute(Attribute attribute) {
	if (attributes.add(attribute)) {
	    setDirty(true);
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Removes an attribute.
     * Sets this element's {@link #isDirty()  dirty state} to true if successful
     *
     * @param attribute
     * @return
     */
    @Override
    public synchronized boolean removeAttribute(Attribute attribute) {
	if (attributes.remove(attribute)) {
	    setDirty(true);
	    return true;
	} else {
	    return false;
	}
    }

    /**
     *
     * @return An <em>unmodifiable</em> collection of this element's attributes
     */
    @Override
    public synchronized Collection<Attribute> getAttributes() {
	return Collections.unmodifiableCollection(attributes);
    }

    /**
     * Adds a reference to a resource proxy in {@link #getMetadataDocument() this document} to this element.
     * This also registers the current element as reference to the proxy on the {@link CMDIDocument}.
     *
     * Sets this element's {@link #isDirty()  dirty state} to true if resource proxy was actually added (return value is not null)
     *
     * @param id ID of resource proxy to add as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document.
     * @see CMDIDocument#registerResourceProxyReference(nl.mpi.metadata.cmdi.api.model.ResourceProxy,
     * nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement)
     */
    @Override
    public synchronized ResourceProxy addDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxy != null) {
	    if (resourceProxies.add(resourceProxy)) {
		setDirty(true);
		getMetadataDocument().registerResourceProxyReference(resourceProxy, this);
		return resourceProxy;
	    }
	}
	return null;
    }

    /**
     * Removes the document resource proxy references. This also unregisters the current element as reference to the proxy from the
     * {@link CMDIDocument}
     *
     * Sets this element's {@link #isDirty()  dirty state} to true if resource proxy was actually added (return value is not null)
     *
     * @param id ID of resource proxy to remove as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document or not removed.
     * @see CMDIDocument#unregisterResourceProxyReference(nl.mpi.metadata.cmdi.api.model.ResourceProxy,
     * nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement)
     */
    @Override
    public synchronized ResourceProxy removeDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxy != null) {
	    if (resourceProxies.remove(resourceProxy)) {
		setDirty(true);
		if (!getMetadataDocument().unregisterResourceProxyReference(resourceProxy, this)) {
		    logger.warn("Removed resource proxy with id {} was not registered with document!");
		}
		return resourceProxy;
	    }
	}
	return null;
    }

    @Override
    public int getReferencesCount() {
	return resourceProxies.size();
    }

    /**
     *
     * @return an <em>unmodifiable</em> copy of the collection of resource proxies referenced by this element
     */
    @Override
    public List<Reference> getReferences() {
	return Collections.<Reference>unmodifiableList(resourceProxies);
    }

    /**
     * Creates or retrieves a <em>non-metadata</em> resource proxy for the specified uri with the specified mimetype, yhen adds a reference
     * to that proxy in this metadata element.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     * @see CMDIDocument#createDocumentResourceReference(java.net.URI, java.lang.String)
     * @see #addDocumentResourceProxyReference(java.lang.String)
     */
    @Override
    public DataResourceProxy createResourceReference(URI uri, String type, String mimetype) throws MetadataException {
	DataResourceProxy resourceProxy = getMetadataDocument().createDocumentResourceReference(uri, type, mimetype);
	return (DataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    /**
     * Creates or retrieves a <em>metadata</em> resource proxy for the specified uri with the specified mimetype, then adds a reference to
     * that proxy in this metadata element.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     * @see CMDIDocument#createDocumentMetadataReference(java.net.URI, java.lang.String)
     * @see #addDocumentResourceProxyReference(java.lang.String)
     */
    @Override
    public MetadataResourceProxy createMetadataReference(URI uri, String mimetype) throws MetadataException {
	MetadataResourceProxy resourceProxy = getMetadataDocument().createDocumentMetadataReference(uri, mimetype);
	return (MetadataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    /**
     *
     * @see #removeDocumentResourceProxyReference(java.lang.String)
     */
    @Override
    public ResourceProxy removeReference(ResourceProxy reference) throws MetadataException {
	return removeDocumentResourceProxyReference(reference.getId());
    }

    @Override
    public String toString() {
	return getName();
    }

    /**
     * Constructs path string from parent's path string, element name and index among siblings.
     *
     * @return XPath to this CMDIMetadataElement from the DOM root
     */
    @Override
    public final String getPathString() {
	return getPathCharSequence().toString();
    }

    /**
     * Determines the path of this metadata element by extending its parent path
     *
     * @return the path of this element. CharSequence to prevent excessive conversions to string
     * @throws AssertionError if called on an extension that does not implement {@link ContainedMetadataElement}
     */
    protected synchronized CharSequence getPathCharSequence() throws AssertionError {
	if (pathCharSequence == null) {
	    if (this instanceof ContainedMetadataElement) {
		// Component and Element implement ContainedMetadataElement, path can be inferred from parent
		final CMDIContainerMetadataElementImpl parentContainer = (CMDIContainerMetadataElementImpl) ((ContainedMetadataElement) this).getParent();
		// Get index among siblings
		final List<MetadataElement> siblings = parentContainer.getChildren(getType());
		final int index = siblings.indexOf(this);
		if (index < 0) {
		    throw new RuntimeException("Node not found in parent's children");
		}

		// Construct path
		final StringBuilder pathStringBuilder = new StringBuilder(parentContainer.getPathCharSequence());
		// Append type (=element name)
		pathStringBuilder.append("/:").append(getType().getName());
		// Append index
		pathStringBuilder.append("[").append(index + 1).append("]");

		pathCharSequence = pathStringBuilder;
	    } else {
		// CMDIDocument should override thiss
		throw new AssertionError("Element path cannot be determined for class " + getClass() + " because it does not implement ContainedMetadataElement");
	    }
	}
	return pathCharSequence;
    }

    public void setDirty(boolean dirty) {
	this.dirty = dirty;
    }

    public boolean isDirty() {
	return dirty;
    }
}
