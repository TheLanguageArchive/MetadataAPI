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
package nl.mpi.metadata.cmdi.api;

import javax.xml.transform.TransformerException;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
import nl.mpi.metadata.cmdi.api.type.ElementType;
import org.apache.xpath.CachedXPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIComponentReader {

    private static Logger logger = LoggerFactory.getLogger(CMDIComponentReader.class);

    public void readComponents(final CMDIDocument cmdiDocument, final Document domDocument, final CachedXPathAPI xPathAPI) throws DOMException, MetadataDocumentException {
	Node rootComponentNode = getRootComponentNode(cmdiDocument, domDocument, xPathAPI);
	final CMDIProfile profile = cmdiDocument.getType();
	readComponents(rootComponentNode, cmdiDocument, profile);
    }

    private Node getRootComponentNode(final CMDIDocument cmdiDocument, final Document domDocument, final CachedXPathAPI xPathAPI) throws MetadataDocumentException {
	final String rootComponentNodePath = cmdiDocument.getType().getPathString();
	try {
	    final Node rootComponentNode = xPathAPI.selectSingleNode(domDocument, rootComponentNodePath);

	    if (rootComponentNode == null) {
		throw new MetadataDocumentException(String.format("Root component node not found at specified path: %1$s", rootComponentNodePath));
	    }

	    logger.debug("Found documentNode at {}", rootComponentNodePath);

	    return rootComponentNode;
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(
		    String.format("TransormationException while looking up root component node at specified path: %1$s", rootComponentNodePath),
		    tEx);
	}
    }

    private void readComponents(final Node domNode, final CMDIContainerMetadataElement parentElement, final ComponentType parentType) throws MetadataDocumentException {
	readChildElements(domNode, parentElement, parentType);
	readAttributes(domNode, parentElement, parentType);
    }

    private void readChildElements(final Node parentNode, final CMDIContainerMetadataElement parentElement, final ComponentType parentType) throws MetadataDocumentException {
	NodeList childNodes = parentNode.getChildNodes();
	for (int i = 0; i < childNodes.getLength(); i++) {
	    final Node childNode = childNodes.item(i);
	    if (childNode instanceof org.w3c.dom.Element) {
		logger.debug("Found DOM Element node {}, will create CMDI metadata element", childNode);
		CMDIProfileElement childType = parentType.getContainableTypeByName(childNode.getLocalName());
		if (childType == null) {
		    throw new MetadataDocumentException(String.format("Cannot infer component type for DOM node: %1$s", childNode));
		}
		CMDIMetadataElement childElement = createElementInstance(parentElement, childNode, childType);
		parentElement.addChildElement(childElement);

		if (childElement instanceof Component) {
		    logger.debug("Reading child elements for component");
		    readComponents(childNode, (Component) childElement, (ComponentType) childType);
		}

	    } else {
		logger.debug("Skipping non-element node {}", childNode);
	    }
	}
    }

    private CMDIMetadataElement createElementInstance(final CMDIContainerMetadataElement parentElement, final Node instanceNode, final CMDIProfileElement type) throws AssertionError {
	if (type instanceof ElementType) {
	    logger.debug("Adding {} as CMDI element child to {}", type.getName(), parentElement.getName());
	    return new Element((ElementType) type, parentElement, instanceNode.getTextContent());
	} else if (type instanceof ComponentType) {
	    return new Component((ComponentType) type, parentElement);
	} else {
	    // None of the above types
	    throw new AssertionError("Cannot handle CMDIMetadataElement type " + type.getClass().getName());
	}
    }

    private void readAttributes(Node domNode, CMDIContainerMetadataElement parentElement, ComponentType parentType) {
	//TODO
    }
}
