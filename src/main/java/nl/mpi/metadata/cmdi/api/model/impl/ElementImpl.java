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

import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 * A CMDI Element. Instance of {@link nl.mpi.metadata.cmdi.api.type.ElementType}
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ElementImpl<T> extends CMDIMetadataElementImpl implements Element<T> {

    private final CMDIDocument metadataDocument;
    private final CMDIContainerMetadataElement parent;
    private final ElementType elementType;
    private T value;

    /**
     * Constructs an element with no value specified
     *
     * @param elementType type of the element
     * @param parent container that will be this element's parent
     */
    public ElementImpl(final ElementType elementType, final CMDIContainerMetadataElement parent) {
	this(elementType, parent, null);
    }

    /**
     * Constructs an element
     *
     * @param elementType type of the element
     * @param parent container that will be this element's parent
     * @param value initial value of the element
     */
    public ElementImpl(final ElementType elementType, final CMDIContainerMetadataElement parent, final T value) {
	this.elementType = elementType;
	this.value = value;
	this.parent = parent;
	this.metadataDocument = parent.getMetadataDocument();
    }

    @Override
    public String getName() {
	return elementType.getName();
    }

    @Override
    public String getDisplayValue() {
	return getValue() == null ? null : getValue().toString();
    }

    @Override
    public T getValue() {
	return value;
    }

    @Override
    public void setValue(T value) {
	this.value = value;
    }

    @Override
    public CMDIContainerMetadataElement getParent() {
	return parent;
    }

    @Override
    public ElementType getType() {
	return elementType;
    }

    @Override
    public CMDIDocument getMetadataDocument() {
	return metadataDocument;
    }

    @Override
    public void addMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMetadataElementListener(MetadataElementListener listener) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
	return String.format("[%1$s=%2$s]", getType().getName(), getValue());
    }
}
