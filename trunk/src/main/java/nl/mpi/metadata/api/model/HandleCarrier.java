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
package nl.mpi.metadata.api.model;

import java.net.URI;
import nl.mpi.metadata.api.MetadataException;

/**
 * Interface for metadata objects that carry a handler
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface HandleCarrier {

    /**
     *
     * @return URI representation of the handle of this object, typically of scheme 'hdl' (never 'http'!). 
     * May return null if no handle or no valid handle has been set on this carrier.
     */
    URI getHandle();

    /**
     * Sets the handle of this object
     *
     * @param handle handle to set, typically a URL of scheme 'hdl'
     * @throws UnsupportedOperationException if the object does not support setting the handle
     * @throws IllegalArgumentException if the provided handle is of a format that cannot be converted into a handle for this object
     * @throws If the API fails to set the handle because of some internal error
     */
    void setHandle(URI handle) throws MetadataException, UnsupportedOperationException, IllegalArgumentException;
}
