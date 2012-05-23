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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElement implements ReferencingMetadataElement<ResourceProxy>, MetadataElementAttributeContainer<Attribute> {

    private final Collection<Attribute> attributes;
    private final Collection<ResourceProxy> resourceProxies;

    protected CMDIMetadataElement() {
	this.attributes = new HashSet<Attribute>();
	this.resourceProxies = new HashSet<ResourceProxy>();
    }

    public synchronized boolean addAttribute(Attribute attribute) {
	return attributes.add(attribute);
    }

    public synchronized boolean removeAttribute(Attribute attribute) {
	return attributes.remove(attribute);
    }

    /**
     *
     * @return An <em>unmodifiable</em> collection of this element's attributes
     */
    public synchronized Collection<Attribute> getAttributes() {
	return Collections.unmodifiableCollection(attributes);
    }

    /**
     * Adds a reference to a resource proxy in {@link #getMetadataDocument() this document} to this element
     *
     * @param id ID of resource proxy to add as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document.
     */
    public ResourceProxy addDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxy != null) {
	    if (resourceProxies.add(resourceProxy)) {
		return resourceProxy;
	    }
	}
	return null;
    }

    /**
     *
     * @param id ID of resource proxy to remove as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document or not removed.
     */
    public ResourceProxy removeDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxy != null) {
	    if (resourceProxies.remove(resourceProxy)) {
		return resourceProxy;
	    }
	}
	return null;
    }

    /**
     *
     * @return an <em>unmodifiable</em> copy of the collection of resource proxies referenced by this element
     */
    public Collection<Reference> getReferences() {
	return Collections.<Reference>unmodifiableCollection(resourceProxies);
    }

    /**
     * Creates a <em>non-metadata</em> resource proxy for the specified uri with the specified mimetype, and adds a reference to that proxy
     * in this metadata element. This (at this moment) will not check whether a resource proxy with the same URI already exist, so callers
     * should make sure to check first, or duplicates may occur.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     * @see #createMetadataReference(java.net.URI, java.lang.String)
     * @see CMDIDocument#addDocumentResourceProxy(nl.mpi.metadata.cmdi.api.model.ResourceProxy)
     */
    public DataResourceProxy createResourceReference(URI uri, String mimetype) {
	DataResourceProxy resourceProxy = getMetadataDocument().createDocumentResourceReference(uri, mimetype);
	return (DataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    /**
     * Creates a <em>metadata</em> resource proxy for the specified uri with the specified mimetype, and adds a reference to that proxy
     * in this metadata element. This (at this moment) will not check whether a resource proxy with the same URI already exist, so callers
     * should make sure to check first, or duplicates may occur.
     *
     * @param uri URI of the new resource proxy
     * @param mimetype mimetype of the new resource proxy (can be null)
     * @return the newly created resource proxy. Null if not created or added.
     * @see #createResourceReference(java.net.URI, java.lang.String)
     * @see CMDIDocument#addDocumentResourceProxy(nl.mpi.metadata.cmdi.api.model.ResourceProxy)
     */
    public MetadataResourceProxy createMetadataReference(URI uri, String mimetype) {
	MetadataResourceProxy resourceProxy = getMetadataDocument().createDocumentMetadataReference(uri, mimetype);
	return (MetadataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    public ResourceProxy removeReference(ResourceProxy reference) throws MetadataException {
	return removeDocumentResourceProxyReference(reference.getId());
    }

    /**
     *
     * @return The CMDI document this container belongs to (more type specific than interface implemented)
     * @see nl.mpi.metadata.api.model.MetadataElement#getMetadataDocument()
     */
    abstract public CMDIDocument getMetadataDocument();

    public abstract CMDIProfileElement getType();

    @Override
    public String toString() {
	return getName();
    }

    /**
     * Constructs path string from parent's path string, element name and index among siblings.
     *
     * @return XPath to this CMDIMetadataElement from the DOM root
     */
    public final String getPathString() {
	return getPathCharSequence().toString();
    }

    /**
     * Determines the path of this metadata element by extending its parent path
     *
     * @return the path of this element. CharSequence to prevent excessive conversions to string
     * @throws AssertionError if called on an extension that does not implement {@link ContainedMetadataElement}
     */
    protected CharSequence getPathCharSequence() throws AssertionError {
	if (this instanceof ContainedMetadataElement) {
	    // Component and Element implement ContainedMetadataElement, path can be inferred from parent
	    final CMDIContainerMetadataElement parentContainer = (CMDIContainerMetadataElement) ((ContainedMetadataElement) this).getParent();
	    // Get index among siblings
	    final List<CMDIMetadataElement> siblings = parentContainer.getChildren(getType());
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

	    return pathStringBuilder;
	} else {
	    // CMDIDocument should override thiss
	    throw new AssertionError("Element path cannot be determined for class " + getClass() + " because it does not implement ContainedMetadataElement");
	}
    }
}
