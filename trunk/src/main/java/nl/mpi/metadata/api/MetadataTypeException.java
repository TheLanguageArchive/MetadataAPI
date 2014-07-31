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

import nl.mpi.metadata.api.type.MetadataElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataTypeException extends MetadataException {

    private MetadataElementType type;

    public MetadataTypeException(MetadataElementType type) {
	this.type = type;
    }

    public MetadataTypeException(MetadataElementType type, String message, Throwable cause) {
	super(message, cause);
	this.type = type;
    }

    public MetadataTypeException(MetadataElementType type, Throwable cause) {
	super(cause);
	this.type = type;
    }

    public MetadataTypeException(MetadataElementType type, String message) {
	super(message);
	this.type = type;
    }

    /**
     * @return the type
     */
    public MetadataElementType getType() {
	return type;
    }
}
