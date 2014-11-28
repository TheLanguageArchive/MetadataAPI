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
package nl.mpi.metadata.api.model;

import java.net.URI;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface Reference {

    /**
     *
     * @return an URI that identifies the document; there <em>may</em> be a
     * distinct physical location, see {@link #getLocation() }
     */
    URI getURI();

    void setURI(URI uri);

    /**
     *
     * @return the physical location of the referenced resource; this may be
     * null. Also see {@link #getURI() }.
     */
    URI getLocation();

    void setLocation(URI url);

    String getMimetype();

    void setMimeType(String mimeType);

    /**
     * @return A string representation of the type of this resource,
     * {@literal  e.g.} 'Metadata' or 'Resource', depending on type and
     * implementation.
     */
    String getType();
}
