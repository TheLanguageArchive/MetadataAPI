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
import java.util.UUID;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElement implements ReferencingMetadataElement, MetadataElementAttributeContainer<Attribute> {

    private Collection<Attribute> attributes;
    private Collection<ResourceProxy> resourceProxies;

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
     *
     * @param id ID of resource proxy to add as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document.
     */
    public ResourceProxy addDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxy != null && resourceProxies.add(resourceProxy)) {
	    return resourceProxy;
	} else {
	    return null;
	}
    }

    /**
     *
     * @param id ID of resource proxy to remove as reference
     * @return the resource proxy that has been added as a reference. Null if not found in document or not removed.
     */
    public ResourceProxy removeDocumentResourceProxyReference(String id) {
	ResourceProxy resourceProxy = getMetadataDocument().getDocumentResourceProxy(id);
	if (resourceProxies.remove(resourceProxy)) {
	    return resourceProxy;
	} else {
	    return null;
	}
    }

    public Collection<ResourceProxy> getReferences() {
	return Collections.unmodifiableCollection(resourceProxies);
    }

    public DataResourceProxy createResourceReference(URI uri, String mimetype) {
	DataResourceProxy resourceProxy = new DataResourceProxy(getNewId(), uri, mimetype);
	getMetadataDocument().addDocumentResourceProxy(resourceProxy);
	return (DataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    public MetadataResourceProxy createMetadataReference(URI uri, String mimetype) {
	MetadataResourceProxy resourceProxy = new MetadataResourceProxy(getNewId(), uri, mimetype);
	getMetadataDocument().addDocumentResourceProxy(resourceProxy);
	return (MetadataResourceProxy) addDocumentResourceProxyReference(resourceProxy.getId());
    }

    private String getNewId() {
	return UUID.randomUUID().toString();
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
}
