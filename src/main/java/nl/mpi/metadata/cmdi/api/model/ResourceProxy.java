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
import nl.mpi.metadata.api.model.HandleCarrier;
import nl.mpi.metadata.api.model.Reference;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class ResourceProxy implements Reference, HandleCarrier {

    private final String id;
    private URI uri;
    private String mimeType;

    public ResourceProxy(String id, URI uri) {
	this(id, uri, null);
    }

    public ResourceProxy(String id, URI uri, String mimeType) {
	this.id = id;
	this.uri = uri;
	this.mimeType = mimeType;
    }

    public String getId() {
	return id;
    }

    public synchronized URI getURI() {
	return uri;
    }

    public synchronized void setURI(URI uri) {
	this.uri = uri;
    }

    public synchronized String getMimetype() {
	return mimeType;
    }

    public synchronized void setMimeType(String mimeType) {
	this.mimeType = mimeType;
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
    public String getHandle() {
	return String.valueOf(getURI());
    }

    /**
     * ResourceProxies require a string that represents a valid URI as a handle
     *
     * @throws IllegalArgumentException if provided handle does not represent
     */
    public void setHandle(String handle) throws IllegalArgumentException {
	try {
	    setURI(new URI(handle));
	} catch (URISyntaxException usEx) {
	    throw new IllegalArgumentException("ResourceProxy only supports URI handles", usEx);
	}
    }

    @Override
    public String toString() {
	return String.format("{%1$s} %2$s", getId(), getURI());
    }
}
