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
import java.net.URISyntaxException;
import nl.mpi.metadata.api.model.DirtyStateProvider;
import nl.mpi.metadata.api.model.HandleCarrier;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.util.HandleUtil;
import nl.mpi.metadata.cmdi.api.CMDIConstants;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class ResourceProxy implements Reference, HandleCarrier, DirtyStateProvider {

    private final String id;
    private final String type;
    private URI uri;
    private String mimeType;
    private boolean dirty;
    private final HandleUtil handleUtil = new HandleUtil();

    public ResourceProxy(String id, URI uri, String type) {
	this(id, uri, type, null);
    }

    public ResourceProxy(String id, URI uri, String type, String mimeType) {
	this.id = id;
	this.uri = uri;
	this.mimeType = mimeType;
	this.type = type;

	this.dirty = true;
    }

    public String getId() {
	return id;
    }

    public synchronized URI getURI() {
	return uri;
    }

    public synchronized void setURI(URI uri) {
	this.uri = uri;
	setDirty(true);
    }

    public synchronized String getMimetype() {
	return mimeType;
    }

    public synchronized void setMimeType(String mimeType) {
	this.mimeType = mimeType;
	setDirty(true);
    }

    /**
     * Provides a string representation of the type of this proxy. Usually one of the constants defined in {@link CMDIConstants}.
     *
     * @return The type of this resource proxy
     */
    public String getType() {
	return type;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ResourceProxy other = (ResourceProxy) obj;
	if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
	    return false;
	}
	if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
	    return false;
	}
	if (this.uri != other.uri && (this.uri == null || !this.uri.equals(other.uri))) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
	hash = 53 * hash + (this.uri != null ? this.uri.hashCode() : 0);
	return hash;
    }

    /**
     *
     * @return String representation of the value returned by {@link #getURI() }
     */
    @Override
    public URI getHandle() {
	return handleUtil.createHandleUri(String.valueOf(getURI()));
    }

    /**
     * ResourceProxies require a string that represents a valid URI as a handle
     *
     * @throws IllegalArgumentException if provided handle does not represent
     */
    @Override
    public void setHandle(URI handle) throws IllegalArgumentException {
	if (handleUtil.isHandleUri(handle)) {
	    setURI(handle);
	} else {
	    throw new IllegalArgumentException("Illegal handle URI: " + handle.toString());
	}
    }

    @Override
    public String toString() {
	return String.format("{%1$s} %2$s", getId(), getURI());
    }

    public boolean isDirty() {
	return dirty;
    }

    public void setDirty(boolean dirty) {
	this.dirty = dirty;
    }
}
