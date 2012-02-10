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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.model.ReferencingMetadataElement;
import org.w3c.dom.Node;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIMetadataElement implements ReferencingMetadataElement, MetadataElementAttributeContainer<Attribute> {

    private String path;
    private Collection<Attribute> attributes;

    protected CMDIMetadataElement() {
	attributes = new HashSet<Attribute>();
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getPath() {
	return path;
    }

    public synchronized boolean addAttribute(Attribute attribute) {
	return attributes.add(attribute);
    }

    public synchronized boolean removeAttribute(Attribute attribute) {
	return attributes.remove(attribute);
    }

    /**
     * 
     * @return An <em>unmodifiable</em> collection of this element's attributes
     */
    public synchronized Collection<Attribute> getAttributes() {
	return Collections.unmodifiableCollection(attributes);
    }

    /**
     * 
     * @return The CMDI document this container belongs to (more type specific than interface implemented)
     * @see nl.mpi.metadata.api.model.MetadataElement#getMetadataDocument() 
     */
    abstract public CMDIDocument getMetadataDocument();

    abstract public Node getDomNode();
}
