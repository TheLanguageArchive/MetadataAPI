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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;
import org.apache.xmlbeans.SchemaProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CMDI component definition, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For example components, see http://www.clarin.eu/cmd/example/
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentType implements MetadataContainerElementType, DataCategoryType {

    private static Logger logger = LoggerFactory.getLogger(ComponentType.class);
    private ComponentType parent;
    private SchemaProperty schemaElement;
    private QName qName;
    private String description;
    private DataCategory dataCategory;
    private Collection<MetadataElementAttributeType> attributes;
    private List<MetadataElementType> children;

    public ComponentType(SchemaProperty schemaElement, ComponentType parent) {
	this.schemaElement = schemaElement;
    }

    public int getMinOccurences(MetadataContainerElementType container) {
	return schemaElement.getMinOccurs().intValue();
    }

    public int getMaxOccurences(MetadataContainerElementType container) {
	if (schemaElement.getMaxOccurs() != null) {
	    return schemaElement.getMaxOccurs().intValue();
	} else {
	    return -1;
	}
    }

    public String getName() {
	if (qName.getLocalPart() != null) {
	    return qName.getLocalPart();
	} else {
	    return qName.toString();
	}
    }

    public String getDescription() {
	return description;
    }

    public DataCategory getDataCategory() {
	return dataCategory;
    }

    public Collection<MetadataElementAttributeType> getAttributes() {
	return attributes;
    }

    public Collection<MetadataElementType> getContainableTypes() {
	return Collections.unmodifiableList(children);
    }

    public boolean canContainType(MetadataElementType type) {
	return children.contains(type);
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
	final ComponentType other = (ComponentType) obj;
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

    protected ComponentType() {
    }

    protected void readSchema() throws CMDITypeException {
	if (getSchemaElement() == null) {
	    throw new CMDITypeException("Cannot read schema, it has not been loaded");
	}
	logger.debug("Reading schema for {}", schemaElement.getName());
	readProperties();
	readChildren();
    }

    protected final void setSchemaElement(SchemaProperty element) {
	this.schemaElement = element;
    }

    private void readProperties() {
	qName = schemaElement.getName();
    }

    private void readChildren() throws CMDITypeException {
	SchemaProperty[] elements = schemaElement.getType().getElementProperties();

	if (elements != null && elements.length > 0) {
	    children = new ArrayList<MetadataElementType>(elements.length);
	    for (SchemaProperty child : elements) {
		ComponentType componentType = new ComponentType(child, this);
		componentType.readSchema();
		children.add(componentType);
	    }
	} else {
	    children = Collections.emptyList();
	}
    }
}
