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

/**
 * @param <T> Field type
 * @param <M> Type of element that can be contained by the parent
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataField<T, M extends MetadataElement> extends ContainedMetadataElement<M> {

    /**
     * 
     * @return Name of field
     */
    String getName();

    /**
     * 
     * @return Value of field
     */
    T getValue();

    void setValue(T value);
}
