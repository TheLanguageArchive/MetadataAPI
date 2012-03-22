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
import nl.mpi.metadata.api.model.Reference;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class ResourceProxy implements Reference {

    private final String id;
    private final URI uri;
    private final String mimeType;

    public ResourceProxy(String id, URI uri, String mimeType) {
	this.id = id;
	this.uri = uri;
	this.mimeType = mimeType;
    }

    public String getId() {
	return id;
    }

    public URI getURI() {
	return uri;
    }

    public String getMimetype() {
	return mimeType;
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
}
