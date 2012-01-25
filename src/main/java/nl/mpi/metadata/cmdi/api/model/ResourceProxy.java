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
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElementAttribute;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.type.MetadataElementType;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ResourceProxy implements Reference {

    public URI getURI() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getMimetype() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElementType getType() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataDocument getMetadataDocument() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPath() {
	throw new UnsupportedOperationException("Not supported yet.");
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
}
