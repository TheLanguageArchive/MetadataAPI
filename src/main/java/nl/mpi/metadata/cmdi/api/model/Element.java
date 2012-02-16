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
package nl.mpi.metadata.cmdi.api.model;

import java.net.URI;
import java.util.Collection;
import nl.mpi.metadata.api.model.MetadataField;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 * A CMDI Element. Instance of @see nl.mpi.metadata.cmdi.api.type.ElementType
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class Element<T> extends CMDIMetadataElement implements MetadataField<T, CMDIMetadataElement> {

    private final CMDIDocument metadataDocument;
    private CMDIContainerMetadataElement parent;
    private ElementType elementType;
    private T value;

    public Element(final ElementType elementType, final CMDIContainerMetadataElement parent, final T value) {
	this.elementType = elementType;
	this.value = value;
	this.parent = parent;
	this.metadataDocument = parent.getMetadataDocument();
    }

    public String getName() {
	return elementType.getName();
    }

    public T getValue() {
	return value;
    }

    public void setValue(T value) {
	this.value = value;
    }

    public CMDIContainerMetadataElement getParent() {
	return parent;
    }

    public ElementType getType() {
	return elementType;
    }

    public CMDIDocument getMetadataDocument() {
	return metadataDocument;
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

    public DataResourceProxy createResourceReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataResourceProxy createMetadataReference(URI uri, String mimetype) {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
