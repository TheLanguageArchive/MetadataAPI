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

/**
 * Interface for metadata objects that carry a handler
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface HandleCarrier {

    /**
     *
     * @return String representation of the handle of this object
     */
    String getHandle();

    /**
     * Sets the handle of this object
     *
     * @param handle
     * @throws UnsupportedOperationException if the object does not support setting the handle
     * @throws IllegalArgumentException if the provided handle is of a format that cannot be converted into a handle for this object
     */
    void setHandle(String handle) throws UnsupportedOperationException, IllegalArgumentException;
}
