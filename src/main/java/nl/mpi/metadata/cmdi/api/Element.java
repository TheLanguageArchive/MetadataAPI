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
package nl.mpi.metadata.cmdi.api;

import java.net.URI;
import java.util.Collection;
import javax.xml.xpath.XPath;
import nl.mpi.metadata.api.MetadataDocument;
import nl.mpi.metadata.api.MetadataElementAttribute;
import nl.mpi.metadata.api.MetadataField;
import nl.mpi.metadata.api.Reference;
import nl.mpi.metadata.api.ReferencingMetadataElement;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.cmdi.api.type.ElementType;

/**
 * A CMDI Element. Instance of @see nl.mpi.metadata.cmdi.api.type.ElementType
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class Element implements MetadataField, ReferencingMetadataElement {

    public String getName() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getValue() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setValue(Object value) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Component getParent() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public ElementType getType() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataDocument getDocument() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public XPath getPath() {
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
