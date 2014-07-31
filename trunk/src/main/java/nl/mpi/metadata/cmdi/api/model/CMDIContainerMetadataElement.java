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

import java.util.List;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.ContainedMetadataElementType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface CMDIContainerMetadataElement extends CMDIMetadataElement, MetadataContainer<CMDIMetadataElement> {

    /**
     * Gets a child element by type and index
     *
     * @param type type of the child to get
     * @param index index of the element to return
     * @return child, if found; null if no children of the specified type are contained in this element
     * @throws IndexOutOfBoundsException if children of the specified type do exist, but the specified index is outside the bounds of the
     * collection
     */
    CMDIMetadataElement getChildElement(CMDIProfileElement type, int index) throws IndexOutOfBoundsException;

    List<MetadataElement> getChildren(ContainedMetadataElementType childType);

    /**
     *
     * @return the total number of {@link CMDIMetadataElement} element children of this container
     */
    int getChildrenCount();

    /**
     * Finds the number children of this element that are of the specified type
     *
     * @param childType metadata type to look for
     * @return number of children of the specified type
     */
    int getChildrenCount(CMDIProfileElement childType);

    ComponentType getType();

    /**
     * Removes a child from this element.
     *
     * Sets this element's {@link #isDirty()  dirty state} to true
     *
     * @param element element to remove
     * @return whether the child was removed. Will be false if the child is not registered as a child.
     */
    boolean removeChildElement(CMDIMetadataElement element) throws MetadataElementException;
}
