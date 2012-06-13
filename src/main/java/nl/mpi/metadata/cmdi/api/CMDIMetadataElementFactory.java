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
package nl.mpi.metadata.cmdi.api;

import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.impl.ComponentImpl;
import nl.mpi.metadata.cmdi.api.model.impl.ElementImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIMetadataElementFactory {

    /**
     * Instantiates a new profile element ({@link ElementImpl} or {@link ComponentImpl}) of the specified type.
     * This method will <em>not</em> add it as a child to the parent.
     *
     * @param parentElement container that the newly created element/component will consider its parent
     * @param type element or component type from the profile to instantiate
     * @return new element or component instance depending on the type
     */
    public CMDIMetadataElement createNewMetadataElement(CMDIContainerMetadataElement parentElement, CMDIProfileElement type) {
	//TODO: Add creation of CMDIDocument
	if (type instanceof ElementType) {
	    return new ElementImpl((ElementType) type, parentElement);
	} else if (type instanceof ComponentType) {
	    return new ComponentImpl((ComponentType) type, parentElement);
	} else {
	    // None of the above types
	    throw new AssertionError("Cannot handle CMDIMetadataElement type " + type.getClass().getName());
	}
    }
}
