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

import nl.mpi.metadata.api.model.MetadataDocument;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataDocumentException extends Exception {

    protected MetadataDocument document;

    public MetadataDocumentException(MetadataDocument document, String message) {
	super(message);
	this.document = document;
    }

    public MetadataDocumentException(MetadataDocument document, Throwable cause) {
	super(cause);
	this.document = document;
    }

    public MetadataDocumentException(MetadataDocument document, String message, Throwable cause) {
	super(message, cause);
	this.document = document;
    }

    public MetadataDocumentException(String message) {
	this(null, message);
    }

    public MetadataDocumentException(String message, Throwable cause) {
	this(null, message, cause);
    }

    public MetadataDocumentException(Throwable cause) {
	this(null, null, cause);
    }

    /**
     * Get the value of document
     *
     * @return the value of document
     */
    public MetadataDocument getDocument() {
	return document;
    }

    /**
     * Set the value of document
     *
     * @param document new value of document
     */
    public void setDocument(MetadataDocument document) {
	this.document = document;
    }
}
