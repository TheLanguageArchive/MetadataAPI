/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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
import nl.mpi.metadata.api.type.ContainedMetadataElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;
import org.apache.xmlbeans.SchemaProperty;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public interface CMDIProfileElement extends ContainedMetadataElementType<CMDIProfileElement>, DataCategoryType {

    /**
     *
     * @return the collection of attributes including excluded attributes
     * @see #setExcludedAttributes(java.util.Collection)
     */
    Collection<MetadataElementAttributeType> getAllAttributes();

    /**
     * Retrieves the specified attribute. Looks in all attributes, including excluded attributes (those specified
     * through {@link #setExcludedAttributes(java.util.Collection)}.
     *
     * @param namespaceURI namespace URI of attribute to find. Specify null to ignore namespace
     * @param name name of attribute to find
     * @return attribute with specified name (and optionally namespace), or null if no match found
     * @see #getAllAttributes()
     */
    MetadataElementAttributeType getAttributeTypeByName(String namespaceURI, String name);

    Collection<MetadataElementAttributeType> getAttributes();

    DataCategory getDataCategory();

    String getDescription();

    int getMaxOccurences();

    int getMinOccurences();

    String getName();

    ComponentType getParent();

    /**
     * Returns an XPath that, when applied to an <em>instance</em> of this profile element, returns a list
     * of the nodes in that instance that are of this type.
     *
     * @return XPath string to find instances
     */
    String getPathString();

    SchemaProperty getSchemaElement();

    /**
     * @param attributes the attributes to set
     */
    void setAttributes(Collection<MetadataElementAttributeType> attributes);

    /**
     * Sets the data category for this profile element
     *
     * @param dataCategory
     */
    void setDataCategory(DataCategory dataCategory);

    /**
     * Sets the element description
     *
     * @param description description to set for this element
     */
    void setDescription(String description);

    /**
     * Sets the attributes read but not excluded in attribute set. These will be returned by {@link #getAllAttributes() }
     * but not by {@link #getAttributes() }
     *
     * @param excludedAttributes attributes excluded in attribute set
     * @see #getAllAttributes()
     */
    void setExcludedAttributes(Collection<MetadataElementAttributeType> excludedAttributes);
    
}
