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
package nl.mpi.metadata.api;

import nl.mpi.metadata.api.model.MetadataElement;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataElementException extends Exception {

    protected MetadataElement element;

    public MetadataElementException(MetadataElement element, String message) {
	super(message);
	this.element = element;
    }

    public MetadataElementException(MetadataElement element, Throwable cause) {
	super(cause);
	this.element = element;
    }

    public MetadataElementException(MetadataElement element, String message, Throwable cause) {
	super(message, cause);
	this.element = element;
    }

    /**
     * Get the value of element
     *
     * @return the value of element
     */
    public MetadataElement getElement() {
	return element;
    }

    /**
     * Set the value of element
     *
     * @param element new value of element
     */
    public void setElement(MetadataElement document) {
	this.element = document;
    }
}
