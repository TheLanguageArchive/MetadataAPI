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
package nl.mpi.metadata.cmdi.api.type.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import org.apache.xmlbeans.SchemaProperty;

/**
 * Base class for Component and Element types
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public abstract class CMDIProfileElementImpl implements CMDIProfileElement {

    protected final ComponentTypeImpl parent;
    protected final SchemaProperty schemaElement;
    protected final QName qName;
    protected String description;
    protected DataCategory dataCategory;
    private Collection<MetadataElementAttributeType> attributes;
    private Collection<MetadataElementAttributeType> excludedAttributes;

    protected CMDIProfileElementImpl(SchemaProperty schemaElement, ComponentTypeImpl parent) {
	this.parent = parent;
	this.schemaElement = schemaElement;
	this.qName = schemaElement.getName();
    }

    /**
     * Retrieves the specified attribute. Looks in all attributes, including excluded attributes (those specified
     * through {@link #setExcludedAttributes(java.util.Collection)}.
     *
     * @param namespaceURI namespace URI of attribute to find. Specify null to ignore namespace
     * @param name name of attribute to find
     * @return attribute with specified name (and optionally namespace), or null if no match found
     * @see #getAllAttributes()
     */
    @Override
    public MetadataElementAttributeType getAttributeTypeByName(String namespaceURI, String name) {
	for (MetadataElementAttributeType child : getAllAttributes()) {
	    if ((namespaceURI == null || namespaceURI.equals(child.getNamespaceURI()))
		    && child.getName().equals(name)) {
		return child;
	    }
	}
	return null;
    }

    @Override
    public Collection<MetadataElementAttributeType> getAttributes() {
	return attributes;
    }

    /**
     *
     * @return the collection of attributes including excluded attributes
     * @see #setExcludedAttributes(java.util.Collection)
     */
    @Override
    public Collection<MetadataElementAttributeType> getAllAttributes() {
	// TODO: Maybe cache this
	if (excludedAttributes == null || excludedAttributes.isEmpty()) {
	    return attributes;
	} else {
	    Set<MetadataElementAttributeType> result = new HashSet<MetadataElementAttributeType>(attributes.size() + excludedAttributes.size());
	    result.addAll(attributes);
	    result.addAll(excludedAttributes);
	    return result;
	}
    }

    @Override
    public DataCategory getDataCategory() {
	return dataCategory;
    }

    /**
     * Sets the data category for this profile element
     *
     * @param dataCategory
     */
    @Override
    public void setDataCategory(DataCategory dataCategory) {
	this.dataCategory = dataCategory;
    }

    @Override
    public String getDescription() {
	return description;
    }

    /**
     * Sets the element description
     *
     * @param description description to set for this element
     */
    @Override
    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public int getMaxOccurences() {
	if (getSchemaElement().getMaxOccurs() != null) {
	    return getSchemaElement().getMaxOccurs().intValue();
	} else {
	    return -1;
	}
    }

    @Override
    public int getMinOccurences() {
	if (getSchemaElement().getMinOccurs() != null) {
	    return getSchemaElement().getMinOccurs().intValue();
	} else {
	    return 0;
	}
    }

    @Override
    public String getName() {
	if (qName.getLocalPart() != null) {
	    return qName.getLocalPart();
	} else {
	    return qName.toString();
	}
    }

    @Override
    public SchemaProperty getSchemaElement() {
	return schemaElement;
    }

    @Override
    public ComponentTypeImpl getParent() {
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
	final CMDIProfileElementImpl other = (CMDIProfileElementImpl) obj;
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
    @Override
    public void setAttributes(Collection<MetadataElementAttributeType> attributes) {
	this.attributes = attributes;
    }

    /**
     * Sets the attributes read but not excluded in attribute set. These will be returned by {@link #getAllAttributes() }
     * but not by {@link #getAttributes() }
     *
     * @param excludedAttributes attributes excluded in attribute set
     * @see #getAllAttributes()
     */
    @Override
    public void setExcludedAttributes(Collection<MetadataElementAttributeType> excludedAttributes) {
	this.excludedAttributes = excludedAttributes;
    }
}
