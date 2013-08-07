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

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.MetadataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Reads resource proxies from a CMDI DOM into a {@link CMDIDocument} instance
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIResourceProxyReader {

    private final static Logger logger = LoggerFactory.getLogger(CMDIResourceProxyReader.class);

    /**
     * Reads resource proxies from a specified CMDI DOM into the specified CMDI document instance. This will add
     * {@link MetadataResourceProxy MetadataResourceProxies} for proxies with type Metadata, and
     * {@link DataResourceProxy DataResourceProxies} for all other types (including SearchPage, SearchService and others).
     *
     * @param cmdiDocument CMDI document to add resource proxies to
     * @param domDocument DOM to read resource proxies from
     * @param xPathAPI XPathAPI cached XPathAPI object for the specified DOM
     * @throws DOMException in case of failure to retrieve node or node content from DOM
     * @throws MetadataException if required element or attribute is missing, or if resource type is not known
     */
    public void readResourceProxies(final CMDIDocument cmdiDocument, final Document domDocument, final XPath xPath) throws DOMException, MetadataException {
	try {
	    // Iterate over all ResourceProxy nodes
	    NodeList resourceProxyNodes = (NodeList) xPath.evaluate(CMDIConstants.CMD_RESOURCE_PROXIES_PATH, domDocument, XPathConstants.NODESET);
	    for (int i = 0; i < resourceProxyNodes.getLength(); i++) {
		Node proxyNode = resourceProxyNodes.item(i);
		try {
		    // Construct resource proxy
		    ResourceProxy resourceProxy = createResourceProxy(proxyNode, xPath);
		    // Add it to map in CMDI document
		    cmdiDocument.addDocumentResourceProxy(resourceProxy);
		} catch (MetadataException mEx) {
		    logger.warn("Skipping resource proxy due to error. See exception for details.", mEx);
		}
	    }
	} catch (XPathExpressionException ex) {
	    throw new MetadataDocumentException(cmdiDocument, "XPathExpressionException while reading resource proxies from CMDI document", ex);
	}
    }

    /**
     * Creates a ResourceProxy object from a DOM node. The result will be a {@link MetadataResourceProxy} if the resource type is Metadata,
     * otherwise it will be a {@link DataResourceProxy} with the {@link DataResourceProxy#getType() } type taken from the type specified
     * in the DOM.
     *
     * @param proxyNode DOM node of the resource proxy
     * @param xPathAPI Cached XPathAPI for the DOM
     * @return new resource proxy based on provided DOM node
     * @throws DOMException in case of failure to retrieve node or node content from DOM
     * @throws TransformerException if any of the XPath lookups fails
     * @throws MetadataException if required element or attribute is missing
     */
    protected ResourceProxy createResourceProxy(final Node proxyNode, final XPath xPath) throws DOMException, MetadataException, XPathExpressionException {
	final Node resourceTypeNode = getResourceTypeNode(proxyNode, xPath);
	final String resourceType = resourceTypeNode.getTextContent();

	final String id = getResourceProxyId(proxyNode);
	final URI resourceRef = getResourceRef(proxyNode, xPath);
	final String type = resourceTypeNode.getTextContent();
	final String mimeType = getResourceProxyMimeType(resourceTypeNode);

	if (CMDIConstants.CMD_RESOURCE_PROXY_TYPE_METADATA.equals(resourceType)) {
	    return new MetadataResourceProxy(id, resourceRef, mimeType);
	} else {
	    // Consider it to be a data resource proxy with the specified type
	    return new DataResourceProxy(id, resourceRef, type, mimeType);
	}
    }

    private Node getResourceTypeNode(final Node proxyNode, final XPath xPath) throws XPathExpressionException, MetadataException {
	Node resourceTypeNode = (Node) xPath.evaluate("./cmd:" + CMDIConstants.CMD_RESOURCE_PROXY_TYPE_ELEMENT, proxyNode, XPathConstants.NODE);
	if (resourceTypeNode != null) {
	    return resourceTypeNode;
	} else {
	    throw new MetadataException("Encountered resource proxy without ResourceType");
	}
    }

    private String getResourceProxyId(final Node proxyNode) throws DOMException, MetadataException {
	Node idAttr = proxyNode.getAttributes().getNamedItem(CMDIConstants.CMD_RESOURCE_PROXY_ID_ATTRIBUTE);
	if (idAttr instanceof Attr) {
	    return idAttr.getNodeValue();
	} else {
	    throw new MetadataException("Encountered resource proxy without id");
	}
    }

    private URI getResourceRef(final Node proxyNode, final XPath xPath) throws MetadataException, XPathExpressionException, DOMException {
	URI uri = null;
	Node resourceRefNode = (Node) xPath.evaluate("./cmd:" + CMDIConstants.CMD_RESOURCE_PROXY_REF_ELEMENT, proxyNode, XPathConstants.NODE);
	if (resourceRefNode != null) {
	    String resourceRef = resourceRefNode.getTextContent();
	    try {
		uri = new URI(resourceRef);
	    } catch (URISyntaxException usEx) {
		throw new MetadataException("URI syntax exception in ResourceRef of resource proxy", usEx);
	    }
	} else {
	    throw new MetadataException("Encountered resource proxy without ResourceRef");
	}
	return uri;
    }

    private String getResourceProxyMimeType(Node resourceTypeNode) throws DOMException {
	Node mimeTypeAttrNode = resourceTypeNode.getAttributes().getNamedItem(CMDIConstants.CMD_RESOURCE_PROXY_TYPE_MIMETYPE_ATTRIBUTE);
	if (mimeTypeAttrNode instanceof Attr) {
	    return mimeTypeAttrNode.getNodeValue();
	} else {
	    return null;
	}
    }
}
