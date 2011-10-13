/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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
package nl.mpi.metadata.cmdi.api.type;

import java.util.Collection;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;
import org.apache.xmlbeans.SchemaProperty;

/**
 * Base class for Component and Element types
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIProfileElement implements DataCategoryType, MetadataElementType {

    protected ComponentType parent;
    protected SchemaProperty schemaElement;
    protected QName qName;
    protected String description;
    protected DataCategory dataCategory;
    protected Collection<MetadataElementAttributeType> attributes;

    public CMDIProfileElement(SchemaProperty schemaElement, ComponentType parent) {
	this.schemaElement = schemaElement;
	this.parent = parent;
    }

    public Collection<MetadataElementAttributeType> getAttributes() {
	return attributes;
    }

    public DataCategory getDataCategory() {
	return dataCategory;
    }

    public String getDescription() {
	return description;
    }

    public int getMaxOccurences(MetadataContainerElementType container) {
	if (schemaElement.getMaxOccurs() != null) {
	    return schemaElement.getMaxOccurs().intValue();
	} else {
	    return -1;
	}
    }

    public int getMinOccurences(MetadataContainerElementType container) {
	return schemaElement.getMinOccurs().intValue();
    }

    public String getName() {
	if (qName.getLocalPart() != null) {
	    return qName.getLocalPart();
	} else {
	    return qName.toString();
	}
    }

    public SchemaProperty getSchemaElement() {
	return schemaElement;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final CMDIProfileElement other = (CMDIProfileElement) obj;
	if (this.parent != other.parent && (this.parent == null || !this.parent.equals(other.parent))) {
	    return false;
	}
	if (this.qName != other.qName && (this.qName == null || !this.qName.equals(other.qName))) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 67 * hash + (this.parent != null ? this.parent.hashCode() : 0);
	hash = 67 * hash + (this.qName != null ? this.qName.hashCode() : 0);
	return hash;
    }

    @Override
    public String toString() {
	return qName.toString();
    }

    protected void readProperties() {
	qName = schemaElement.getName();
    }

    protected final void setSchemaElement(SchemaProperty element) {
	this.schemaElement = element;
    }
}
