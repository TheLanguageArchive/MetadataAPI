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

import nl.mpi.metadata.api.model.MetadataElementAttribute;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class Attribute<T> implements MetadataElementAttribute<T> {

    private final MetadataElementAttributeType type;
    private T value;

    public Attribute(MetadataElementAttributeType type) {
	this.type = type;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public MetadataElementAttributeType getType() {
	return type;
    }

    /**
     * Get the value of value
     *
     * @return the value of value
     */
    public T getValue() {
	return value;
    }

    /**
     * Set the value of value
     *
     * @param value new value of value
     */
    public void setValue(T value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return String.format("[@%1$s=%2$s]", getType().getName(), getValue());
    }
}
