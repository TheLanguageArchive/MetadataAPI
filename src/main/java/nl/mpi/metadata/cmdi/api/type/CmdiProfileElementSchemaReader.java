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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes an existing CMDI profile element and reads properties, attributes and child elements from the schema
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CmdiProfileElementSchemaReader {

    private final static Logger logger = LoggerFactory.getLogger(CmdiProfileElementSchemaReader.class);

    public void readSchema(CMDIProfileElement profileElement) throws CMDITypeException {
	if (profileElement.getSchemaElement() == null) {
	    throw new CMDITypeException(null, "Cannot read schema, it has not been set or loaded");
	}
	logger.debug("Reading schema for {}", profileElement.getSchemaElement().getName());
	readProperties(profileElement);
	readAttributes(profileElement);

	if (profileElement instanceof ComponentType) {
	    readChildren((ComponentType) profileElement);
	}
    }

    protected void readProperties(CMDIProfileElement profileElement) {
	if (profileElement instanceof ComponentType) {
	    readComponentId(profileElement);
	} else if (profileElement instanceof ControlledVocabularyElementType) {
	    readVocabularyItems((ControlledVocabularyElementType) profileElement);
	}
    }

    protected void readAttributes(CMDIProfileElement profileElement) {
	SchemaProperty[] attributeProperties = profileElement.getSchemaElement().getType().getAttributeProperties();
	if (attributeProperties != null && attributeProperties.length > 0) {
	    Collection<MetadataElementAttributeType> attributes = new ArrayList<MetadataElementAttributeType>(attributeProperties.length);
	    for (SchemaProperty attributeProperty : attributeProperties) {
		final QName attributeName = attributeProperty.getName();
		logger.debug("Creating attribute type '{}' of type {}", attributeName, attributeProperty.getType());

		CMDIAttributeType attribute = new CMDIAttributeType();
		attribute.setSchemaElement(attributeProperty);
		attribute.setName(attributeName.getLocalPart());
		if (attributeName.getNamespaceURI() != null) {
		    attribute.setNamespaceURI(attributeName.getNamespaceURI());
		}

		attribute.setType(attributeProperty.getType().toString());  // consider .getName().getLocalPart()) but getName can
		// be null, see documentation
		attribute.setDefaultValue(attributeProperty.getDefaultText());
		attribute.setMandatory(attributeProperty.getMinOccurs().compareTo(BigInteger.ZERO) > 0);
		attributes.add(attribute);
	    }
	    profileElement.setAttributes(attributes);
	} else {
	    Collection<MetadataElementAttributeType> attributes = Collections.emptySet();
	    profileElement.setAttributes(attributes);
	}
    }

    /**
     * Recursively loads children (components, elements) for a {@link ComponentType}
     * @throws CMDITypeException 
     */
    private void readChildren(ComponentType componentType) throws CMDITypeException {
	SchemaProperty[] elements = componentType.getSchemaElement().getType().getElementProperties();

	List<CMDIProfileElement> children;
	if (elements != null && elements.length > 0) {
	    children = new ArrayList<CMDIProfileElement>(elements.length);
	    for (SchemaProperty child : elements) {

		CMDIProfileElement childElement;

		// Is the element a Component (if so, it has ComponentId property)
		boolean isComponent = null != child.getType().getAttributeProperty(new QName("ComponentId"))
			|| child.getType().getElementProperties().length > 0;
		if (isComponent) {
		    // Component id found, so create component
		    logger.debug("Creating child component type {}", child.getName().toString());
		    childElement = new ComponentType(child, componentType, createChildPath(componentType, child));
		} else {
		    // Not a component, so create element
		    if (child.getType().hasStringEnumValues()) {
			logger.debug("Creating child CV element type {}", child.getName().toString());
			childElement = new ControlledVocabularyElementType(child, componentType, createChildPath(componentType, child));
		    } else {
			logger.debug("Creating child element type {}", child.getName().toString());
			childElement = new ElementType(child, componentType, createChildPath(componentType, child));
		    }
		}
		readSchema(childElement);
		children.add(childElement);
	    }
	} else {
	    children = Collections.emptyList();
	}
	componentType.setChildren(children);
    }

    private StringBuilder createChildPath(ComponentType componentType, SchemaProperty child) {
	return new StringBuilder(componentType.getPath()).append("/:").append(child.getName().getLocalPart());
    }

    private void readComponentId(CMDIProfileElement profileElement) {
	SchemaProperty componentIdAttribute = profileElement.getSchemaElement().getType().getAttributeProperty(new QName("ComponentId"));
	// attribute may not be present, e.g. for profile root component
	final String componentId = (componentIdAttribute == null) ? null : componentIdAttribute.getDefaultText();
	((ComponentType) profileElement).setComponentId(componentId);
    }

    /**
     * Reads the allowed controlled vocabulary items from the element type
     */
    private void readVocabularyItems(ControlledVocabularyElementType profileElement) {
	XmlAnySimpleType[] itemTypes = profileElement.getSchemaElement().getType().getEnumerationValues();
	List<ControlledVocabularyItem> items;
	if (itemTypes != null && itemTypes.length > 0) {
	    items = new ArrayList<ControlledVocabularyItem>();
	    for (XmlAnySimpleType itemType : profileElement.getSchemaElement().getType().getEnumerationValues()) {
		CMDIControlledVocabularyItem item = new CMDIControlledVocabularyItem();
		item.setValue(itemType.getStringValue());
		// TODO: item.setDescription(itemDescription);
		// TODO: item.setDataCategory(itemDataCategory);
		items.add(item);
	    }
	} else {
	    items = Collections.emptyList();
	}
	profileElement.setItems(items);
    }
}
