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

import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class AttributeImpl<T> implements Attribute<T> {

    private final MetadataElementAttributeType type;
    private final CMDIMetadataElement parent;
    private transient String pathString;
    private T value;

    /**
     *
     * @param type attribute type of the new attribute
     * @throws NullPointerException if type is null, this is not allowed
     */
    public AttributeImpl(MetadataElementAttributeType type, CMDIMetadataElement parent) throws NullPointerException {
	if (type == null) {
	    throw new NullPointerException("Type cannot be null for a new Attribute");
	}
	this.type = type;
	this.parent = parent;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    @Override
    public MetadataElementAttributeType getType() {
	return type;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    @Override
    public T getValue() {
	return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    @Override
    public void setValue(T value) {
	this.value = value;
    }

    @Override
    public synchronized String getPathString() {
	if (pathString == null) {
	    pathString = createAttributePathString();
	}
	return pathString;
    }

    private String createAttributePathString() {
	final String nsURI = getType().getNamespaceURI();
	final String localPart = getType().getName();
	final StringBuilder path = new StringBuilder();
	if (parent instanceof CMDIMetadataElementImpl) {
	    // Add path char sequence, should be more efficient if it's a StringBuilder, no unnecessary toString() conversions
	    path.append(((CMDIMetadataElementImpl) parent).getPathCharSequence());
	} else {
	    path.append(parent.getPathString());
	}
	path.append("/@");
	if (nsURI != null && nsURI.length() > 0) {
	    path.append("{");
	    path.append(nsURI);
	    path.append("}");
	}
	path.append(localPart).toString();
	return path.toString();
    }

    @Override
    public String toString() {
	return String.format("[@%1$s=%2$s]", getType().getName(), getValue());
    }
}
