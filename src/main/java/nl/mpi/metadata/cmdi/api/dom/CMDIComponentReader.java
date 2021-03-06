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
package nl.mpi.metadata.cmdi.api.dom;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIComponentReader {

    private static Logger logger = LoggerFactory.getLogger(CMDIComponentReader.class);
    private CMDIMetadataElementFactory elementFactory;

    /**
     * Will create a new ComponentReader with the specified CMDIMetadataElementFactory
     *
     * @param elementFactory element factory to use for instantiating profile elements
     */
    public CMDIComponentReader(CMDIMetadataElementFactory elementFactory) {
	this.elementFactory = elementFactory;
    }

    public void readComponents(final CMDIDocument cmdiDocument, final Document domDocument, final XPath xPath) throws DOMException, MetadataException {
	final Node rootComponentNode = getRootComponentNode(cmdiDocument, domDocument, xPath);
	final CMDIProfile profile = cmdiDocument.getType();
	readElement(rootComponentNode, cmdiDocument, profile);
    }

    public static Node getRootComponentNode(final CMDIDocument cmdiDocument, final Document domDocument, final XPath xPath) throws MetadataException {
	final String rootComponentNodePath = cmdiDocument.getType().getPathString();
	try {
	    final Node rootComponentNode = (Node) xPath.evaluate(rootComponentNodePath, domDocument, XPathConstants.NODE);

	    if (rootComponentNode == null) {
		throw new MetadataException(String.format("Root component node not found at specified path: %1$s", rootComponentNodePath));
	    }

	    logger.debug("Found documentNode at {}", rootComponentNodePath);

	    return rootComponentNode;
	} catch (XPathExpressionException ex) {
	    throw new MetadataException(
		    String.format("XPathExpressionException while looking up root component node at specified path: %1$s", rootComponentNodePath),
		    ex);
	}
    }

    private void readElement(final Node domNode, final CMDIMetadataElement element, final CMDIProfileElement type) throws MetadataException {
	if (element instanceof CMDIContainerMetadataElement) {
	    if (type instanceof ComponentType) {
		logger.debug("Reading child elements for component");
		readChildElements(domNode, (CMDIContainerMetadataElement) element, (ComponentType) type);
	    } else {
		throw new AssertionError("Found Component node but specified type is not a ComponentType");
	    }
	}
	readAttributes(domNode, element, type);
	// Element is freshly read and has not been altered. Unsetting the default 'dirty' state!
	element.setDirty(false);
    }

    private void readChildElements(final Node parentNode, final CMDIContainerMetadataElement parentElement, final ComponentType parentType) throws MetadataException {
	NodeList childNodes = parentNode.getChildNodes();
	for (int i = 0; i < childNodes.getLength(); i++) {
	    final Node childNode = childNodes.item(i);
	    if (childNode instanceof org.w3c.dom.Element) {
		logger.debug("Found DOM Element node {}, will create CMDI metadata element", childNode);
		CMDIProfileElement childType = parentType.getType(childNode.getLocalName());
		if (childType == null) {
		    throw new MetadataException(String.format("Cannot infer component type for DOM node: %1$s", childNode));
		}
		CMDIMetadataElement childElement = createElementInstance(parentElement, childNode, childType);
		parentElement.addChildElement(childElement);
		readElement(childNode, childElement, childType);
	    } else {
		logger.debug("Skipping non-element node {}", childNode);
	    }
	}
    }

    private CMDIMetadataElement createElementInstance(final CMDIContainerMetadataElement parentElement, final Node instanceNode, final CMDIProfileElement type) throws AssertionError {
	CMDIMetadataElement elementInstance = elementFactory.createNewMetadataElement(parentElement, type);
	if (elementInstance instanceof Element) {
	    ((Element) elementInstance).setValue(instanceNode.getTextContent());
	}
	return elementInstance;
    }

    private void readAttributes(Node instanceNode, CMDIMetadataElement metadataElement, CMDIProfileElement metadataType) {
	final NamedNodeMap attributesMap = instanceNode.getAttributes();
	if (attributesMap.getLength() > 0) {
	    for (MetadataElementAttributeType attributeType : metadataType.getAllAttributes()) {
		final Node attributeNode = getAttributeNodeByType(attributesMap, attributeType);
		if (attributeNode != null) {
		    final String localName = attributeNode.getLocalName();
		    // What kind of attribute is it?
		    if (CMDIConstants.CMD_RESOURCE_PROXY_REFERENCE_ATTRIBUTE.equals(localName)) {
			// Attribute is resource proxy reference
			readProxyReferenceAttribute(attributeNode, metadataElement);
		    } else if (metadataElement instanceof MultilingualElement
			    && CMDIConstants.CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAMESPACE_URI.equals(attributeNode.getNamespaceURI())
			    && CMDIConstants.CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAME.equals(localName)) {
			// Attribute is language specification for multilingual
			readLanguageAttribute(attributeNode, (MultilingualElement) metadataElement);
		    } else {
			// Other attribute, add as element attribute
			readElementAttribute((CMDIAttributeType) attributeType, attributeNode, metadataElement);
		    }
		}
	    }
	}
    }

    private Node getAttributeNodeByType(NamedNodeMap attributesMap, MetadataElementAttributeType attributeType) throws DOMException {
	final String namespaceURI = attributeType.getNamespaceURI();
	final String name = attributeType.getName();
	if (namespaceURI == null || namespaceURI.length() == 0) {
	    return attributesMap.getNamedItem(name);
	} else {
	    return attributesMap.getNamedItemNS(namespaceURI, name);
	}
    }

    private void readLanguageAttribute(Node attributeNode, MultilingualElement metadataElement) throws DOMException {
	final String elementLanguage = attributeNode.getNodeValue();
	metadataElement.setLanguage(elementLanguage);
    }

    private void readProxyReferenceAttribute(Node attributeNode, CMDIMetadataElement metadataElement) throws DOMException {
	// Split reference list (which is the node value) on whitespace
	final String[] refs = attributeNode.getNodeValue().split("\\s+");
	for (String ref : refs) {
	    metadataElement.addDocumentResourceProxyReference(ref);
	}
    }

    private void readElementAttribute(CMDIAttributeType attributeType, Node attributeNode, CMDIMetadataElement metadataElement) throws DOMException {
	final Attribute<String> attribute = elementFactory.createAttribute(metadataElement, attributeType);
	attribute.setValue(attributeNode.getNodeValue());
	metadataElement.addAttribute(attribute);
    }
}
