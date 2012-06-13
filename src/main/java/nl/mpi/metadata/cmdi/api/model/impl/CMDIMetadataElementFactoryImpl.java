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
package nl.mpi.metadata.cmdi.api.model.impl;

import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIMetadataElementFactoryImpl implements CMDIMetadataElementFactory {

    /**
     * Instantiates a new profile element ({@link Element} or {@link Component}) of the specified type.
     * This method will <em>not</em> add it as a child to the parent.
     *
     * @param parentElement container that the newly created element/component will consider its parent
     * @param type element or component type from the profile to instantiate
     * @return new element or component instance depending on the type
     */
    @Override
    public CMDIMetadataElement createNewMetadataElement(CMDIContainerMetadataElement parentElement, CMDIProfileElement type) {
	//TODO: Add creation of CMDIDocument
	if (type instanceof ElementType) {
	    // Class to instantiate depends on whether element type is multilingual (xml:lang attribute is present)
	    if (((ElementType) type).isMultilingual()) {
		return new MultilingualElementImpl((ElementType) type, parentElement);
	    } else {
		return new ElementImpl((ElementType) type, parentElement);
	    }
	} else if (type instanceof ComponentType) {
	    return new ComponentImpl((ComponentType) type, parentElement);
	} else {
	    // None of the above types
	    throw new AssertionError("Cannot handle CMDIMetadataElement type " + type.getClass().getName());
	}
    }
}
