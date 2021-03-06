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

import java.util.List;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.type.ContainedMetadataElementType;

/**
 * @param <M> Type of MetadataElement that can be contained in this container element
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataContainer<M extends MetadataElement> {

    /**
     * @param path Path for requested element
     * @return Requested element if found, otherwise null
     * @throws IllegalArgumentException when path is incorrect or invalid
     */
    M getChildElement(String path) throws IllegalArgumentException;

    int getChildrenCount();
    
    List<MetadataElement> getChildren();
    
    List<MetadataElement> getChildren(ContainedMetadataElementType childType);

    public boolean addChildElement(M element) throws MetadataElementException;

    public boolean removeChildElement(M element) throws MetadataElementException;
    
    /**
     * 
     * @param type type to check for
     * @return Whether this container in its current state can contain an additional instance of the specified type
     */
    public boolean canAddInstanceOfType(ContainedMetadataElementType type);
}
