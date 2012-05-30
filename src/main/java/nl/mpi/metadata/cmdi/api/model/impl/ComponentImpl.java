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
package nl.mpi.metadata.cmdi.api.model.impl;

import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 * A CMDI ComponentImpl. Instance of ComponentType
 * @see ComponentType
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentImpl extends CMDIContainerMetadataElementImpl implements Component {

    private final CMDIDocument metadataDocument;
    private final CMDIContainerMetadataElement parent;

    public ComponentImpl(ComponentType type, CMDIContainerMetadataElement parent) {
	super(type);
	this.parent = parent;
	// If parent is a document, it will return itself
	this.metadataDocument = parent.getMetadataDocument();
    }

    public MetadataContainer<CMDIMetadataElement> getParent() {
	return parent;
    }

    @Override
    public CMDIDocument getMetadataDocument() {
	return metadataDocument;
    }
}
