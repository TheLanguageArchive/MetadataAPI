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
public class MetadataElementException extends MetadataDocumentException {

    protected MetadataElement element;

    public MetadataElementException(MetadataElement element, String message) {
	super(element.getMetadataDocument(), message);
	this.element = element;
    }

    public MetadataElementException(MetadataElement element, Throwable cause) {
	super(element.getMetadataDocument(), cause);
	this.element = element;
    }

    public MetadataElementException(MetadataElement element, String message, Throwable cause) {
	super(element.getMetadataDocument(), message, cause);
	this.element = element;
    }

    public MetadataElement getElement() {
	return element;
    }
}
