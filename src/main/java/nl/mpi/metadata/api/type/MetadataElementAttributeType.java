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
package nl.mpi.metadata.api.type;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface MetadataElementAttributeType {

    /**
     * Get the default value of this attribute
     *
     * @return the value of defaultValue
     */
    String getDefaultValue();

    /**
     *
     * @return the name of the attribute
     */
    String getName();

    /**
     * Get the value of namespace
     *
     * @return the value of namespace
     */
    String getNamespaceURI();

    /**
     *
     * @return string representation of the type of the attribute
     */
    String getType();

    /**
     * @return attribute is mandatory
     */
    boolean isMandatory();

    /**
     * Set the default value of this attribute
     *
     * @param defaultValue new value of defaultValue
     */
    void setDefaultValue(String defaultValue);

    /**
     * @param mandatory attribute is mandatory
     */
    void setMandatory(boolean mandatory);

    /**
     * @param name new name for attribute
     */
    void setName(String name);

    /**
     * Set the value of namespace
     *
     * @param namespace new value of namespace
     */
    void setNamespaceURI(String namespace);

    /**
     * @param type new value of attribute type
     */
    void setType(String type);
    
}
