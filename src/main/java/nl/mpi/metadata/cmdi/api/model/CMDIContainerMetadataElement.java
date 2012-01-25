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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.model.ContainerMetadataElement;
import nl.mpi.metadata.api.model.MetadataElementAttribute;
import nl.mpi.metadata.api.model.MetadataReference;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.model.ResourceReference;
import nl.mpi.metadata.cmdi.api.type.ComponentType;

/**
 * Abstract base class for Component and Profile instance classes
 * @see Component
 * @see CMDIDocument
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIContainerMetadataElement implements CMDIMetadataElement, ContainerMetadataElement<CMDIMetadataElement> {

    private String path;
    private ComponentType type;
    private List<CMDIMetadataElement> children;

    public CMDIContainerMetadataElement(ComponentType type) {
	this.type = type;
	this.children = new ArrayList<CMDIMetadataElement>();
    }

    public CMDIMetadataElement getChildElement(String path) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @return An <em>unmodifiable</em> copy of the list of children
     */
    public List<CMDIMetadataElement> getChildren() {
	return Collections.unmodifiableList(children);
    }

    public Collection<MetadataElementAttribute> getAttributes() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Reference> getReferences() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResourceReference createResourceReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataReference createMetadataReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getName() {
	return type.getName();
    }

    public ComponentType getType() {
	return type;
    }

    /**
     * 
     * @return The document this container belongs to
     */
    public abstract CMDIDocument getDocument();
}
