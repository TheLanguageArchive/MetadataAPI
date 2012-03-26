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

import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import org.apache.xpath.CachedXPathAPI;
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
     * Will create a new ComponentReader with a new {@link CMDIMetadataElementFactory}.
     */
    public CMDIComponentReader() {
	this(new CMDIMetadataElementFactory());
    }

    /**
     * Will create a new ComponentReader with the specified CMDIMetadataElementFactory
     *
     * @param elementFactory element factory to use for instantiating profile elements
     */
    public CMDIComponentReader(CMDIMetadataElementFactory elementFactory) {
	this.elementFactory = elementFactory;
    }
    
    public void readComponents(final CMDIDocument cmdiDocument, final Document domDocument, final CachedXPathAPI xPathAPI) throws DOMException, MetadataException {
	final Node rootComponentNode = getRootComponentNode(cmdiDocument, domDocument, xPathAPI);
	final CMDIProfile profile = cmdiDocument.getType();
	readElement(rootComponentNode, cmdiDocument, profile);
    }
    
    public static Node getRootComponentNode(final CMDIDocument cmdiDocument, final Document domDocument, final CachedXPathAPI xPathAPI) throws MetadataException {
	final String rootComponentNodePath = cmdiDocument.getType().getPathString();
	try {
	    final Node rootComponentNode = xPathAPI.selectSingleNode(domDocument, rootComponentNodePath);
	    
	    if (rootComponentNode == null) {
		throw new MetadataException(String.format("Root component node not found at specified path: %1$s", rootComponentNodePath));
	    }
	    
	    logger.debug("Found documentNode at {}", rootComponentNodePath);
	    
	    return rootComponentNode;
	} catch (TransformerException tEx) {
	    throw new MetadataException(
		    String.format("TransormationException while looking up root component node at specified path: %1$s", rootComponentNodePath),
		    tEx);
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
    }
    
    private void readChildElements(final Node parentNode, final CMDIContainerMetadataElement parentElement, final ComponentType parentType) throws MetadataException {
	NodeList childNodes = parentNode.getChildNodes();
	for (int i = 0; i < childNodes.getLength(); i++) {
	    final Node childNode = childNodes.item(i);
	    if (childNode instanceof org.w3c.dom.Element) {
		logger.debug("Found DOM Element node {}, will create CMDI metadata element", childNode);
		CMDIProfileElement childType = parentType.getContainableTypeByName(childNode.getLocalName());
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
	    for (MetadataElementAttributeType attributeType : metadataType.getAttributes()) {
		final Node attributeNode = getAttributeNodeByType(attributesMap, attributeType);
		if (attributeNode != null) {
		    if (CMDIConstants.CMD_RESOURCE_PROXY_REFERENCE_ATTRIBUTE.equals(attributeNode.getLocalName())) {
			metadataElement.addDocumentResourceProxyReference(attributeNode.getNodeValue());
		    } else {
			Attribute<String> attribute = new Attribute<String>(attributeType);
			attribute.setValue(attributeNode.getNodeValue());
			metadataElement.addAttribute(attribute);
		    }
		}
	    }
	}
    }
    
    private Node getAttributeNodeByType(final NamedNodeMap attributesMap, final MetadataElementAttributeType attributeType) throws DOMException {
	return attributesMap.getNamedItemNS(attributeType.getNamespaceURI(), attributeType.getName());
    }
}
