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

import nl.mpi.metadata.cmdi.api.model.impl.DisplayValueStrategy;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface CMDIMetadataElementFactory {

    /**
     * Instantiates a new profile element ({@link Element} or {@link Component}) of the specified type.
     * This method will <em>not</em> add it as a child to the parent.
     *
     * @param parentElement container that the newly created element/component will consider its parent
     * @param type element or component type from the profile to instantiate
     * @param strategy
     * @return new element or component instance depending on the type
     */
    CMDIMetadataElement createNewMetadataElement(CMDIContainerMetadataElement parentElement, CMDIProfileElement type);
    
    /**
     * Instantiates a new attribute of the specified type and value type
     * @param <T> value type for attribute
     * @param parent parent element of attribute
     * @param type attribute type for attribute
     * @return new attribute
     */
    <T> Attribute<T> createAttribute(CMDIMetadataElement parent, CMDIAttributeType type);
}
