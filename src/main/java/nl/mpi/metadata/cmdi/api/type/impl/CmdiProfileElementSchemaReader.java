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
package nl.mpi.metadata.cmdi.api.type.impl;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.type.ControlledVocabularyItem;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.dom.CMDINamespaceContext;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.CMDITypeException;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Takes an existing CMDI profile element and reads properties, attributes and
 * child elements from the schema. Construct a new instance for each profile
 * schema to read. Not thread-safe.
 *
 * TODO: Can be refactored in such a way that it creates elements (from
 * SchemaProperties) rather than manipulates them. This requires fewer setters
 * on the type model objects
 *
 * TODO: Make reading annotations optional (because it requires DOM to be loaded
 * and does XPath processing, which slow things down)
 *
 * TODO: performance could potentially be improved by scanning the document once
 * for all annotations, storing them in a map with element path as key, and
 * looking them up in this phase.
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CmdiProfileElementSchemaReader {

    private final static Logger logger = LoggerFactory.getLogger(CmdiProfileElementSchemaReader.class);
    private final Document schemaDocument;
    private final XPath xPath;

    /**
     * Creates a new schema reader for the specified document
     *
     * @param document DOM representation of the profile schema file to read. It
     * is needed to read annotation data from the schema (i.e. display
     * priorities, data categories and element documentation).
     */
    public CmdiProfileElementSchemaReader(Document document) {
        this.schemaDocument = document;
        this.xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new CMDINamespaceContext());
    }

    public void readSchema(CMDIProfileElementImpl profileElement) throws CMDITypeException {

        if (profileElement.getSchemaElement() == null) {
            throw new CMDITypeException(null, "Cannot read schema, it has not been set or loaded");
        }
        logger.debug("Reading schema for {}", profileElement.getSchemaElement().getName());
        readProperties(profileElement);
        readAttributes(profileElement);

        if (profileElement instanceof ComponentTypeImpl) {
            readChildren((ComponentTypeImpl) profileElement);
        }
    }

    protected void readProperties(CMDIProfileElementImpl profileElement) {
        if (profileElement instanceof ComponentTypeImpl) {
            readComponentId(profileElement);
        } else if (profileElement instanceof ControlledVocabularyElementTypeImpl) {
            readVocabularyItems((ControlledVocabularyElementTypeImpl) profileElement);
        }
        searchForAnnotations(profileElement);
    }

    protected void readAttributes(CMDIProfileElementImpl profileElement) {
        SchemaProperty[] attributeProperties = profileElement.getSchemaElement().getType().getAttributeProperties();
        if (attributeProperties != null && attributeProperties.length > 0) {
            Collection<MetadataElementAttributeType> attributes = new ArrayList<MetadataElementAttributeType>(attributeProperties.length);
            Collection<MetadataElementAttributeType> excludedAttributes = new ArrayList<MetadataElementAttributeType>();
            for (SchemaProperty attributeProperty : attributeProperties) {
                readAttribute(attributeProperty, profileElement, attributes, excludedAttributes);
            }
            profileElement.setAttributes(attributes);
            profileElement.setExcludedAttributes(excludedAttributes);
        } else {
            Collection<MetadataElementAttributeType> attributes = Collections.emptySet();
            profileElement.setAttributes(attributes);
        }
    }

    private void readAttribute(SchemaProperty attributeProperty, CMDIProfileElementImpl profileElement, Collection<MetadataElementAttributeType> attributes, Collection<MetadataElementAttributeType> excludedAttributes) {
        final QName attributeName = attributeProperty.getName();
        final String attributeLocalPart = attributeName.getLocalPart();
        final String attributeNamespaceURI = attributeName.getNamespaceURI();

        logger.debug("Creating attribute type '{}' of type {}", attributeName, attributeProperty.getType());

        //Elements should be checked for xml:lang attribute, if so should be set to multilingual
        boolean multilingualAttribute = false;
        if (profileElement instanceof ElementTypeImpl) {
            multilingualAttribute = readMultilingual((ElementTypeImpl) profileElement, attributeLocalPart, attributeNamespaceURI);
        }

        final String type = attributeProperty.getType().toString();  // consider .getName().getLocalPart()) but getName can
        CMDIAttributeTypeImpl attribute = new CMDIAttributeTypeImpl(profileElement.getPathString(), attributeNamespaceURI, attributeLocalPart, type);
        attribute.setSchemaElement(attributeProperty);

        // be null, see documentation
        attribute.setDefaultValue(attributeProperty.getDefaultText());
        attribute.setMandatory(attributeProperty.getMinOccurs().compareTo(BigInteger.ZERO) > 0);

        // Language attribute is an excluded attribute
        if (multilingualAttribute) {
            excludedAttributes.add(attribute);
        } else {
            attributes.add(attribute);
        }
    }

    private boolean readMultilingual(ElementTypeImpl elementType, final String attributeLocalPart, final String attributeNamespaceURI) {
        final boolean multilingual = CMDIConstants.CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAME.equals(attributeLocalPart)
                && CMDIConstants.CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAMESPACE_URI.equals(attributeNamespaceURI);
        elementType.setMultilingual(multilingual);
        logger.debug("Set multilingual property of {} to {}", elementType, multilingual);
        return multilingual;
    }

    /**
     * Recursively loads children (components, elements) for a
     * {@link ComponentTypeImpl}
     *
     * @throws CMDITypeException
     */
    private void readChildren(ComponentTypeImpl componentType) throws CMDITypeException {
        SchemaProperty[] elements = componentType.getSchemaElement().getType().getElementProperties();

        List<CMDIProfileElement> children;
        if (elements != null && elements.length > 0) {
            children = new ArrayList<CMDIProfileElement>(elements.length);
            for (SchemaProperty child : elements) {

                CMDIProfileElementImpl childElement;

                // Is the element a Component (if so, it has ComponentId property)
                boolean isComponent = null != child.getType().getAttributeProperty(new QName("ComponentId"))
                        || child.getType().getElementProperties().length > 0;
                if (isComponent) {
                    // Component id found, so create component
                    logger.debug("Creating child component type {}", child.getName().toString());
                    childElement = new ComponentTypeImpl(child, componentType, createChildPath(componentType, child));
                } else {
                    final XmlAnySimpleType[] enumValues = child.getType().getEnumerationValues();
                    // Not a component, so create element
                    if (enumValues != null && enumValues.length > 0) {
                        logger.debug("Creating child CV element type {}", child.getName().toString());
                        childElement = new ControlledVocabularyElementTypeImpl(child, componentType, createChildPath(componentType, child));
                    } else {
                        logger.debug("Creating child element type {}", child.getName().toString());
                        childElement = new ElementTypeImpl(child, componentType, createChildPath(componentType, child));
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

    private StringBuilder createChildPath(ComponentTypeImpl componentType, SchemaProperty child) {
        return new StringBuilder(componentType.getPath()).append("/cmd:").append(child.getName().getLocalPart());
    }

    private void readComponentId(CMDIProfileElementImpl profileElement) {
        SchemaProperty componentIdAttribute = profileElement.getSchemaElement().getType().getAttributeProperty(new QName("ComponentId"));
        // attribute may not be present, e.g. for profile root component
        final String componentId = (componentIdAttribute == null) ? null : componentIdAttribute.getDefaultText();
        ((ComponentTypeImpl) profileElement).setComponentId(componentId);
    }

    /**
     * Reads the allowed controlled vocabulary items from the element type
     */
    private void readVocabularyItems(ControlledVocabularyElementTypeImpl profileElement) {
        XmlAnySimpleType[] itemTypes = profileElement.getSchemaElement().getType().getEnumerationValues();
        List<ControlledVocabularyItem> items;
        if (itemTypes != null && itemTypes.length > 0) {
            items = new ArrayList<ControlledVocabularyItem>();
            for (XmlAnySimpleType itemType : profileElement.getSchemaElement().getType().getEnumerationValues()) {
                CMDIControlledVocabularyItemImpl item = new CMDIControlledVocabularyItemImpl();
                item.setValue(itemType.getStringValue());
                //TODO: Find out how to get the datcat and label attributes on the vocabulary item
                // (Arbil can do this) - also enable related assertions in test
                // TODO: item.setDescription(itemDescription);
                // TODO: item.setDataCategory(itemDataCategory);
                items.add(item);
            }
        } else {
            items = Collections.emptyList();
        }
        profileElement.setItems(items);
    }

    /**
     * Searches the schema document for annotations for the specified profile
     * element
     */
    private void searchForAnnotations(CMDIProfileElementImpl profileElement) {
        final SchemaParticle schemaParticle = profileElement.getSchemaElement().getType().getContentModel();
        if (schemaParticle != null && schemaParticle.getParticleType() == SchemaParticle.ELEMENT) {
            SchemaLocalElement schemaLocalElement = (SchemaLocalElement) schemaParticle;
            saveAnnotationData(profileElement, schemaLocalElement);
        } else {
            // In case of complex type, try on element specification (xs:element)
            if (schemaDocument != null) {
                // Path to element specification in schema file
                final String elementPath = profileElement.getPathString().replaceFirst("/cmd:", "//*[@name='").replaceAll("/cmd:", "']//*[@name='") + "']";
                try {
                    // Get element specification
                    Node elementSpecNode = (Node) xPath.evaluate(elementPath, schemaDocument, XPathConstants.NODE);
                    if (elementSpecNode != null) {
                        // Get all attributes on the xs:element and look for annotation data
                        NamedNodeMap attributes = elementSpecNode.getAttributes();
                        for (int i = 0; i < attributes.getLength(); i++) {
                            final Node attrNode = attributes.item(i);
                            // Convert to {nsUri}localname format
                            final String nodeName = new QName(attrNode.getNamespaceURI(), attrNode.getLocalName()).toString();
                            // Check for annotation data and if so save to data structure
                            saveAnnotationData(profileElement, nodeName, attrNode.getNodeValue());
                        }
                    }
                } catch (XPathExpressionException ex) {
                    logger.error(String.format("XPathExpressionException while reading annotation for profile element $1%s", profileElement), ex);
                }
            }
        }
    }

    private void saveAnnotationData(CMDIProfileElementImpl profileElement, SchemaLocalElement schemaLocalElement) {
        SchemaAnnotation schemaAnnotation = schemaLocalElement.getAnnotation();
        if (schemaAnnotation != null) {
            for (SchemaAnnotation.Attribute annotationAttribute : schemaAnnotation.getAttributes()) {
                final String annotationValue = annotationAttribute.getValue();
                final String annotationName = annotationAttribute.getName().toString();
                saveAnnotationData(profileElement, annotationName, annotationValue);
            }
        }
    }

    private void saveAnnotationData(CMDIProfileElementImpl profileElement, final String annotationName, final String annotationValue) {
        //Annotation: {ann}documentation : the title of the book
        //Annotation: {ann}displaypriority : 1
        // todo: the url here could be removed provided that it does not make it to unspecific

        if (!"".equals(annotationValue)) {
            if ("{http://www.clarin.eu}displaypriority".equals(annotationName)) {
                if (profileElement instanceof ElementTypeImpl) {
                    try {
                        int displayPriority = Integer.parseInt(annotationValue);
                        ((ElementTypeImpl) profileElement).setDisplayPriority(displayPriority);
                    } catch (NumberFormatException nfEx) {
                        logger.warn(String.format("NumberFormatException in display priority (value: %1$s) for element %2$s", annotationValue, profileElement), nfEx);
                    }
                }
            }
            if ("{http://www.clarin.eu}documentation".equals(annotationName)) {
                profileElement.setDescription(annotationValue);
            }
            DataCategory datCat = null;
            if ("{http://www.isocat.org/ns/dcr}datcat".equals(annotationName)) {
                try {
                    URI dcUri = new URI(annotationValue);
                    datCat = new DataCategory(dcUri);
                } catch (URISyntaxException usEx) {
                    logger.warn(String.format("URISyntaxException in datcat (value: %1$s) for element %2$s", annotationValue, profileElement), usEx);
                }
            }
            if (datCat != null) {
                profileElement.setDataCategory(datCat);
            }
        }
    }
}
