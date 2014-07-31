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
package nl.mpi.metadata.cmdi.api.type.datacategory;

import java.net.URI;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DataCategory {

    private URI identifier;

    public DataCategory(URI identifier) {
	this.identifier = identifier;
    }

    public URI getIdentifier() {
	return identifier;
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 53 * hash + (this.identifier != null ? this.identifier.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final DataCategory other = (DataCategory) obj;
	if (this.identifier != other.identifier && (this.identifier == null || !this.identifier.equals(other.identifier))) {
	    return false;
	}
	return true;
    }
}
