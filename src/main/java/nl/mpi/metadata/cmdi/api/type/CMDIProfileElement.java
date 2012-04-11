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
package nl.mpi.metadata.cmdi.api.type;

import java.util.Collection;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.ContainedMetadataElementType;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;
import org.apache.xmlbeans.SchemaProperty;

/**
 * Base class for Component and Element types
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIProfileElement implements DataCategoryType, ContainedMetadataElementType<CMDIProfileElement> {

    protected final ComponentType parent;
    protected final SchemaProperty schemaElement;
    protected final QName qName;
    protected String description;
    protected DataCategory dataCategory;
    private Collection<MetadataElementAttributeType> attributes;

    protected CMDIProfileElement(SchemaProperty schemaElement, ComponentType parent) {
	this.parent = parent;
	this.schemaElement = schemaElement;
	this.qName = schemaElement.getName();
    }

    /**
     * Returns an XPath that, when applied to an <em>instance</em> of this profile element, returns a list
     * of the nodes in that instance that are of this type.
     *
     * @see org.apache.xpath.XPathAPI#selectNodeList(org.w3c.dom.Node, java.lang.String)
     * @return XPath string to find instances
     */
    public abstract String getPathString();

    public MetadataElementAttributeType getAttributeTypeByName(String namespaceURI, String name) {
	for (MetadataElementAttributeType child : getAttributes()) {
	    if ((namespaceURI == null && child.getNamespaceURI() == null || namespaceURI != null && namespaceURI.equals(child.getNamespaceURI()))
		    && child.getName().equals(name)) {
		return child;
	    }
	}
	return null;
    }

    public Collection<MetadataElementAttributeType> getAttributes() {
	return attributes;
    }

    public DataCategory getDataCategory() {
	return dataCategory;
    }

    /**
     * Sets the data category for this profile element
     *
     * @param dataCategory
     */
    public void setDataCategory(DataCategory dataCategory) {
	this.dataCategory = dataCategory;
    }

    public String getDescription() {
	return description;
    }

    /**
     * Sets the element description
     *
     * @param description description to set for this element
     */
    public void setDescription(String description) {
	this.description = description;
    }

    public int getMaxOccurences(MetadataContainerElementType container) {
	if (getSchemaElement().getMaxOccurs() != null && container.equals(getParent())) {
	    return getSchemaElement().getMaxOccurs().intValue();
	} else {
	    return -1;
	}
    }

    public int getMinOccurences(MetadataContainerElementType container) {
	if (getSchemaElement().getMinOccurs() != null) {
	    return getSchemaElement().getMinOccurs().intValue();
	} else {
	    return 0;
	}
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

    public ComponentType getParent() {
	return parent;
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
	return qName.getLocalPart();
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Collection<MetadataElementAttributeType> attributes) {
	this.attributes = attributes;
    }
}
