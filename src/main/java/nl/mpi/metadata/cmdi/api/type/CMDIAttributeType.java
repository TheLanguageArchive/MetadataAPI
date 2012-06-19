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
package nl.mpi.metadata.cmdi.api.type;

import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import org.apache.xmlbeans.SchemaProperty;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIAttributeType implements MetadataElementAttributeType {

    private String name;
    private String namespaceURI;
    private String type;
    private boolean mandatory;
    private String defaultValue;
    private SchemaProperty schemaElement;

    /**
     * 
     * @return the name of the attribute
     */
    public String getName() {
	return name;
    }

    /**
     * @param name new name for attribute
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * 
     * @return string representation of the type of the attribute
     */
    public String getType() {
	return type;
    }

    /**
     * @param type new value of attribute type
     */
    public void setType(String type) {
	this.type = type;
    }

    /**
     * @return attribute is mandatory
     */
    public boolean isMandatory() {
	return mandatory;
    }

    /**
     * @param mandatory attribute is mandatory
     */
    public void setMandatory(boolean mandatory) {
	this.mandatory = mandatory;
    }

    /**
     * Get the default value of this attribute
     *
     * @return the value of defaultValue
     */
    public String getDefaultValue() {
	return defaultValue;
    }

    /**
     * Set the default value of this attribute
     *
     * @param defaultValue new value of defaultValue
     */
    public void setDefaultValue(String defaultValue) {
	this.defaultValue = defaultValue;
    }

    /**
     * Get the value of namespace
     *
     * @return the value of namespace
     */
    public String getNamespaceURI() {
	return namespaceURI;
    }

    /**
     * Set the value of namespace
     *
     * @param namespace new value of namespace
     */
    public void setNamespaceURI(String namespace) {
	this.namespaceURI = namespace;
    }

    @Override
    public String toString() {
	return getType().toString();
    }
    
    
    public SchemaProperty getSchemaElement() {
	return schemaElement;
    }

    protected final void setSchemaElement(SchemaProperty element) {
	this.schemaElement = element;
    }
}
