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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Map.Entry;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.dom.DomBuildingMode;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.model.Reference;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import static nl.mpi.metadata.cmdi.api.CMDIConstants.*;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.model.MultilingualElement;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * Class for building CMDI profile instances on basis of profile schema's.
 *
 * Some code has been taken from the nl.mpi.arbil.data.ArbilComponentBuilder
 * class of the Arbil metadata editor
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 * @author Peter.Withers@mpi.nl
 * @see <a href="http://tla.mpi.nl/tools/tla-tools/arbil">Arbil Metadata
 * editor</a>
 */
public class CMDIDomBuilder implements MetadataDOMBuilder<CMDIDocument> {

    private static final BigInteger DUMMY_NUMBER_TO_ADD = BigInteger.valueOf(3);
    private final static Logger logger = LoggerFactory.getLogger(CMDIDomBuilder.class);
    private final EntityResolver entityResolver;
    private final DOMBuilderFactory domBuilderFactory;

    /**
     * Creates CMDIDomBuilder with a specified EntityResolver
     *
     * @param entityResolver
     * @see #getEntityResolver()
     */
    public CMDIDomBuilder(EntityResolver entityResolver, DOMBuilderFactory domBuilderFactory) {
        this.entityResolver = entityResolver;
        this.domBuilderFactory = domBuilderFactory;
    }

    @Override
    public Document buildDomForDocument(CMDIDocument metadataDocument) throws MetadataDocumentException {
        // Get base document
        final Document domDocument = getBaseDocument(metadataDocument);
        final XPathFactory xPathFactory = XPathFactory.newInstance();

        // See if headers need updating
        if (metadataDocument.getHeaderDirtyState().isDirty()) {
            pruneHeader(metadataDocument, domDocument, xPathFactory);
            setHeaders(metadataDocument, domDocument, xPathFactory);
        }

        // See if resource proxies need updating
        if (metadataDocument.getResourceProxiesDirtyState().isDirty()) {
            pruneResourceProxies(metadataDocument, domDocument, xPathFactory);
            buildProxies(metadataDocument, domDocument, xPathFactory);
        }

        buildComponents(metadataDocument, domDocument, xPathFactory);
        return domDocument;
    }

    /**
     * Will create a base DOM document for the specified metadata document. If
     * the document specifies a file location, this is loaded as a DOM object;
     * if not, a new DOM will be constructed by calling
     * {@link #createDomFromSchema(java.net.URI, boolean)} with
     * {@code metadataDocument.getType().getSchemaLocation()}.
     *
     * @param metadataDocument metadata document to crease base DOM for
     * @return base DOM document, either loaded from file or created, in its
     * original state
     * @throws MetadataDocumentException
     */
    protected Document getBaseDocument(CMDIDocument metadataDocument) throws MetadataDocumentException {
        if (metadataDocument.getFileLocation() != null) {
            // No previously saved location
            try {
                return domBuilderFactory.newDOMBuilder().parse(metadataDocument.getFileLocation().toString());
            } catch (IOException ioEx) {
                throw new MetadataDocumentException(metadataDocument, "IOException while trying to parse original document file", ioEx);
            } catch (SAXException sEx) {
                throw new MetadataDocumentException(metadataDocument, "SAXException while trying to parse original document file", sEx);
            }
        } else {
            // 
            try {
                return createDomFromSchema(metadataDocument.getType().getSchemaLocation(), DomBuildingMode.EMPTY);
            } catch (IOException ioEx) {
                throw new MetadataDocumentException(metadataDocument, "IOException while trying to create new DOM from schema", ioEx);
            } catch (XmlException xEx) {
                throw new MetadataDocumentException(metadataDocument, "XmlException while trying to create new DOM from schema", xEx);
            }
        }
    }

    private void pruneHeader(CMDIDocument metadataDocument, Document domDocument, XPathFactory xPathFactory) throws DOMException, MetadataDocumentException {
        try {
            final Node headerNode = (Node) newXPath(xPathFactory).evaluate(CMD_HEADER_PATH, domDocument, XPathConstants.NODE);
            // Remove header items
            removeChildren(headerNode);
        } catch (XPathExpressionException tEx) {
            throw new MetadataDocumentException(metadataDocument, "XPathExpressionException while preparing for building metadata DOM", tEx);
        } catch (MetadataException mdEx) {
            throw new MetadataDocumentException(metadataDocument, "MetadataException while preparing for building metadata DOM", mdEx);
        }
    }

    private void pruneResourceProxies(CMDIDocument metadataDocument, Document domDocument, XPathFactory xPathFactory) throws DOMException, MetadataDocumentException {
        try {
            Node resourceProxyListNode = (Node) newXPath(xPathFactory).evaluate(CMD_RESOURCE_PROXY_LIST_PATH, domDocument, XPathConstants.NODE);
            // Remove resource proxies
            removeChildren(resourceProxyListNode);
        } catch (XPathExpressionException tEx) {
            throw new MetadataDocumentException(metadataDocument, "XPathExpressionException while preparing for building metadata DOM", tEx);
        } catch (MetadataException mdEx) {
            throw new MetadataDocumentException(metadataDocument, "MetadataException while preparing for building metadata DOM", mdEx);
        }
    }

    private void removeChildren(Node parent) throws DOMException, MetadataException {
        // replace node by undeep clone of itself
        parent.getParentNode().replaceChild(parent.cloneNode(false), parent);
    }

    private void setHeaders(CMDIDocument metadataDocument, Document domDocument, XPathFactory xPathFactory) throws MetadataDocumentException {
        try {
            Node headerNode = (Node) newXPath(xPathFactory).evaluate(CMD_HEADER_PATH, domDocument, XPathConstants.NODE);
            for (HeaderInfo header : metadataDocument.getHeaderInformation()) {
                org.w3c.dom.Element headerItemNode = domDocument.createElementNS(CMD_NAMESPACE, header.getName());
                headerItemNode.setTextContent(header.getValue());
                for (Entry<String, String> attribute : header.getAttributes().entrySet()) {
                    headerItemNode.setAttribute(attribute.getKey(), attribute.getValue());
                }
                headerNode.appendChild(headerItemNode);
            }
            metadataDocument.getHeaderDirtyState().setDirty(false);
        } catch (XPathExpressionException tEx) {
            throw new MetadataDocumentException(metadataDocument, "XPathExpressionException while setting headers in metadata DOM", tEx);
        }
    }

    private void buildProxies(CMDIDocument metadataDocument, Document domDocument, XPathFactory xPathFactory) throws MetadataDocumentException {
        final Collection<Reference> documentResourceProxies = metadataDocument.getDocumentReferences();
        if (documentResourceProxies.size() > 0) {
            try {
                final Node proxiesNode = (Node) newXPath(xPathFactory).evaluate(CMD_RESOURCE_PROXY_LIST_PATH, domDocument, XPathConstants.NODE);
                for (Reference resourceProxy : documentResourceProxies) {
                    // We can safely cast resourceProxy to ResourceProxy since only ResourceProxies can be added to CMDIDocument
                    buildResourceProxy(domDocument, proxiesNode, (ResourceProxy) resourceProxy);
                }
                metadataDocument.getResourceProxiesDirtyState().setDirty(false);
            } catch (XPathExpressionException tEx) {
                throw new MetadataDocumentException(metadataDocument, "XPathExpressionException while building resource proxies in DOM", tEx);
            }
        }
    }

    private void buildResourceProxy(Document domDocument, final Node proxiesNode, ResourceProxy resourceProxy) throws MetadataDocumentException, DOMException {
        // Create proxy node
        final org.w3c.dom.Element proxyNode = (org.w3c.dom.Element) domDocument.createElementNS(CMD_NAMESPACE, CMD_RESOURCE_PROXY_ELEMENT);
        proxyNode.setAttribute(CMD_RESOURCE_PROXY_ID_ATTRIBUTE, resourceProxy.getId());
        proxiesNode.appendChild(proxyNode);

        final org.w3c.dom.Element resourceTypeNode = (org.w3c.dom.Element) domDocument.createElementNS(CMD_NAMESPACE, CMD_RESOURCE_PROXY_TYPE_ELEMENT);
        resourceTypeNode.setTextContent(resourceProxy.getType());
        if (resourceProxy.getMimetype() != null) {
            resourceTypeNode.setAttribute(CMD_RESOURCE_PROXY_TYPE_MIMETYPE_ATTRIBUTE, resourceProxy.getMimetype());
        }
        proxyNode.appendChild(resourceTypeNode);

        final org.w3c.dom.Element resourceRefNode = (org.w3c.dom.Element) domDocument.createElementNS(CMD_NAMESPACE, CMD_RESOURCE_PROXY_REF_ELEMENT);

        URI resourceProxyUri = resourceProxy.getURI();
        String resourceProxyUriStr = "";
        if (resourceProxyUri != null) {
            resourceProxyUriStr = resourceProxyUri.toString();
        }
        resourceRefNode.setTextContent(resourceProxyUriStr);
        if (resourceProxy.getLocation() != null) {
            resourceRefNode.setAttributeNS(
                    CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAMESPACE,
                    CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_PREFIX + ":" + CMD_RESOURCE_PROXY_LOCATION_ATTRIBUTE_NAME,
                    resourceProxy.getLocation().toString());
        }
        proxyNode.appendChild(resourceRefNode);
    }

    private void buildComponents(CMDIDocument metadataDocument, Document domDocument, XPathFactory xPathFactory) throws MetadataDocumentException {
        try {
            final String schemaLocation = metadataDocument.getType().getSchemaLocation().toString();
            final Node componentsNode = (Node) newXPath(xPathFactory).evaluate(CMD_COMPONENTS_PATH, domDocument, XPathConstants.NODE);
            buildMetadataElement(domDocument, componentsNode, metadataDocument, schemaLocation, xPathFactory, false);
        } catch (DOMException domEx) {
            throw new MetadataDocumentException(metadataDocument, "DOMException while building components in DOM", domEx);
        } catch (XPathExpressionException tEx) {
            throw new MetadataDocumentException(metadataDocument, "XPathExpressionException while building components in DOM", tEx);
        }
    }

    private void buildMetadataElement(Document domDocument, Node parentNode, CMDIMetadataElement metadataElement, String schemaLocation, XPathFactory xPathFactory, boolean forceDirty) throws DOMException, XPathExpressionException {
        logger.debug("Building metadata element [{}]", metadataElement);

        final SchemaProperty elementSchemaProperty = metadataElement.getType().getSchemaElement();
        org.w3c.dom.Element elementNode;
        try {
            if (parentNode == null) {
                elementNode = (org.w3c.dom.Element) newXPath(xPathFactory).evaluate(metadataElement.getPathString(), domDocument, XPathConstants.NODE);
            } else {
                elementNode = (org.w3c.dom.Element) newXPath(xPathFactory).evaluate(metadataElement.getPathString(), parentNode, XPathConstants.NODE);
            }
        } catch (XPathExpressionException ex) {
            logger.error("XPathExpressionException encountered looking up {} in document {}, parent node {}", metadataElement.getPathString(), domDocument, parentNode);
            throw ex;
        }

        // dirty or force treat as dirty?
        final boolean dirty = metadataElement.isDirty() || forceDirty;
        if (dirty) {
            logger.debug("Metadata element is dirty. Rewriting component in DOM.");
            // Add child node to DOM
            if (parentNode == null) {
                elementNode = appendElementNode(domDocument, schemaLocation, parentNode, elementSchemaProperty, false);
            } else {
                Node nextNode = null; //if applicable, we should insert the replacement node before the current next sibling
                if (elementNode != null) {
                    //get next sibling before we remove
                    nextNode = elementNode.getNextSibling();
                    //remove existing node
                    parentNode.removeChild(elementNode);
                }
                elementNode = insertElementNode(domDocument, parentNode, nextNode, elementSchemaProperty);
            }
            // Set value if element
            if (metadataElement instanceof Element) {
                final Object elementValue = ((Element) metadataElement).getValue();
                elementNode.setTextContent(elementValue == null ? "" : elementValue.toString());
            }
            // Add attributes
            buildElementAttributes(domDocument, elementNode, metadataElement);
            // Add proxy refs
            buildProxyReferences(metadataElement, elementNode);

            metadataElement.setDirty(false);
        } else {
            logger.debug("Metadata element not marked dirty. Leaving as is in DOM.");
        }

        // Iterate over children if container
        if (metadataElement instanceof CMDIContainerMetadataElement) {
            if (logger.isDebugEnabled()) {
                logger.debug("Element is container. Iterating over {} child elements", ((CMDIContainerMetadataElement) metadataElement).getChildrenCount());
            }
            for (MetadataElement child : ((CMDIContainerMetadataElement) metadataElement).getChildren()) {
                // children should be treated as dirty if parent is (forced) dirty
                buildMetadataElement(domDocument, elementNode, (CMDIMetadataElement) child, schemaLocation, xPathFactory, dirty);
            }
        }
    }

    private void buildElementAttributes(Document domDocument, org.w3c.dom.Element elementNode, CMDIMetadataElement metadataElement) throws DOMException {
        for (Attribute attribute : metadataElement.getAttributes()) {
            if (attribute.getType() instanceof CMDIAttributeType) {
                Node attrNode = appendAttributeNode(domDocument, elementNode, ((CMDIAttributeType) attribute.getType()).getSchemaElement());
                attrNode.setNodeValue(attribute.getValue().toString());
            } else {
                logger.info("Found attribute of type other than CMDIAttributeType. Skipping attribute {}", attribute);
            }
        }
        if (metadataElement instanceof MultilingualElement) {
            buildLanguageAttribute(domDocument, elementNode, (MultilingualElement) metadataElement);
        }
    }

    private void buildLanguageAttribute(Document domDocument, org.w3c.dom.Element elementNode, MultilingualElement metadataElement) throws DOMException {
        if (metadataElement.getLanguage() != null) {
            final MetadataElementAttributeType languageAttributeType = metadataElement.getType().getAttributeTypeByName(CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAMESPACE_URI, CMD_ELEMENT_LANGUAGE_ATTRIBUTE_NAME);
            if (languageAttributeType instanceof CMDIAttributeType) {
                Node attrNode = appendAttributeNode(domDocument, elementNode, ((CMDIAttributeType) languageAttributeType).getSchemaElement());
                attrNode.setNodeValue(metadataElement.getLanguage());
            }
        }
    }

    private void buildProxyReferences(CMDIMetadataElement metadataElement, org.w3c.dom.Element elementNode) throws DOMException {
        final Collection<Reference> references = metadataElement.getReferences();
        if (references.size() > 0) {
            StringBuilder refBuilder = new StringBuilder();
            for (Reference resourceProxy : references) {
                // We can safely cast resourceProxy to ResourceProxy since only ResourceProxies can be added to CMDIDocument
                refBuilder.append(((ResourceProxy) resourceProxy).getId()).append(" ");
            }
            elementNode.setAttribute(CMD_RESOURCE_PROXY_REFERENCE_ATTRIBUTE, refBuilder.toString().trim());
        }
    }

    public final Document createDomFromSchema(URI xsdFile, DomBuildingMode buildingMode) throws FileNotFoundException, XmlException, MalformedURLException, IOException {
        Document workingDocument = domBuilderFactory.newDOMBuilder().newDocument();
        SchemaType schemaType = getFirstSchemaType(xsdFile);
        constructXml(schemaType.getElementProperties()[0], workingDocument, xsdFile.toString(), null, buildingMode);
        return reloadDom(workingDocument);
    }

    private SchemaType getFirstSchemaType(URI uri) throws FileNotFoundException, XmlException, MalformedURLException, IOException {
        final InputStream inputStream = CMDIEntityResolver.getInputStreamForURI(entityResolver, uri);
        try {
            //Since we're dealing with xml schema files here the character encoding is assumed to be UTF-8
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setCharacterEncoding("UTF-8");
            xmlOptions.setEntityResolver(getEntityResolver());
            SchemaTypeSystem sts = XmlBeans.compileXsd(new XmlObject[]{XmlObject.Factory.parse(inputStream, xmlOptions)}, XmlBeans.getBuiltinTypeSystem(), xmlOptions);
            // there can only be a single root node so we just get the first one, note that the IMDI schema specifies two (METATRANSCRIPT and VocabularyDef)
            return sts.documentTypes()[0];
        } finally {
            inputStream.close();
        }
    }

    private Node constructXml(SchemaProperty currentSchemaProperty, Document workingDocument, String nameSpaceUri, org.w3c.dom.Element parentElement, final DomBuildingMode buildingMode) {
        final SchemaType currentSchemaType = currentSchemaProperty.getType();
        final Node currentElement = appendNode(workingDocument, nameSpaceUri, parentElement, currentSchemaProperty, true);

        // EMPTY mode building should not continue after root Component
        if (DomBuildingMode.EMPTY != buildingMode || !shouldTerminateEmptyModeBuilding(currentElement)) {
            for (SchemaProperty schemaProperty : currentSchemaType.getElementProperties()) {
                BigInteger maxNumberToAdd;
                if (DomBuildingMode.DUMMY == buildingMode) {
                    maxNumberToAdd = schemaProperty.getMaxOccurs();
                    if (maxNumberToAdd == null) {
                        maxNumberToAdd = DUMMY_NUMBER_TO_ADD;
                    } else if (DUMMY_NUMBER_TO_ADD.compareTo(maxNumberToAdd) == -1) {
                        // limit the number added and make sure it is less than the max number to add
                        maxNumberToAdd = DUMMY_NUMBER_TO_ADD;
                    }
                } else {
                    maxNumberToAdd = schemaProperty.getMinOccurs();
                    if (maxNumberToAdd == null) {
                        maxNumberToAdd = BigInteger.ZERO;
                    }
                }
                if (currentElement instanceof org.w3c.dom.Element) {
                    for (BigInteger addNodeCounter = BigInteger.ZERO; addNodeCounter.compareTo(maxNumberToAdd) < 0; addNodeCounter = addNodeCounter.add(BigInteger.ONE)) {
                        constructXml(schemaProperty, workingDocument, nameSpaceUri, (org.w3c.dom.Element) currentElement, buildingMode);
                    }
                }
            }
        }
        return currentElement;
    }

    private boolean shouldTerminateEmptyModeBuilding(final Node currentElement) {
        // Building in EMPTY mode should stop at first child of /CMD/Components, so check parent node name
        return currentElement.getParentNode().getNodeName().equals(CMD_COMPONENTS_NODE_NAME);
    }

    private Node appendNode(Document workingDocument, String nameSpaceUri, org.w3c.dom.Element parentElement, SchemaProperty schemaProperty, boolean addRequiredAttributes) {
        if (schemaProperty.isAttribute()) {
            return appendAttributeNode(workingDocument, parentElement, schemaProperty);
        } else {
            return appendElementNode(workingDocument, nameSpaceUri, parentElement, schemaProperty, addRequiredAttributes);
        }
    }

    private Attr appendAttributeNode(Document workingDocument, org.w3c.dom.Element parentElement, SchemaProperty schemaProperty) {
        Attr currentAttribute = workingDocument.createAttributeNS(schemaProperty.getName().getNamespaceURI(), getPrefixedName(schemaProperty));
        if (schemaProperty.getDefaultText() != null) {
            currentAttribute.setNodeValue(schemaProperty.getDefaultText());
        }
        parentElement.setAttributeNode(currentAttribute);
        return currentAttribute;
    }

    private org.w3c.dom.Element appendElementNode(Document workingDocument, String nameSpaceUri, Node parentElement, SchemaProperty schemaProperty, boolean addRequiredAttributes) {
        org.w3c.dom.Element currentElement = workingDocument.createElementNS(schemaProperty.getName().getNamespaceURI(), getPrefixedName(schemaProperty));
        SchemaType currentSchemaType = schemaProperty.getType();
        if (addRequiredAttributes) {
            for (SchemaProperty attributesProperty : currentSchemaType.getAttributeProperties()) {
                if (attributesProperty.getMinOccurs() != null && !attributesProperty.getMinOccurs().equals(BigInteger.ZERO)) {
                    currentElement.setAttributeNS(attributesProperty.getName().getNamespaceURI(), attributesProperty.getName().getLocalPart(), attributesProperty.getDefaultText());
                }
            }
        }
        if (parentElement == null) {
            // this is probably not the way to set these, however this will do for now (many other methods have been tested and all failed to function correctly)
            currentElement.setAttribute("CMDVersion", "1.1");
            currentElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            currentElement.setAttribute("xsi:schemaLocation", CMD_NAMESPACE + " " + nameSpaceUri);
            workingDocument.appendChild(currentElement);
        } else {
            parentElement.appendChild(currentElement);
        }
        return currentElement;
    }

    private org.w3c.dom.Element insertElementNode(Document workingDocument, Node parentElement, Node insertBeforeNode, SchemaProperty schemaProperty) {
        org.w3c.dom.Element currentElement = workingDocument.createElementNS(schemaProperty.getName().getNamespaceURI(), getPrefixedName(schemaProperty));
        if (insertBeforeNode == null) {
            parentElement.appendChild(currentElement);
        } else {
            parentElement.insertBefore(currentElement, insertBeforeNode);
        }
        return currentElement;
    }

    /**
     * Serializes (in memory) and de-serializes XML document causing it to be
     * re-processed
     *
     * @param builder document builder to use
     * @param document document to reload
     * @return a reloaded copy of the provided document
     */
    private Document reloadDom(Document document) {
        try {
            // Create memory output stream
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final StreamResult xmlOutput = new StreamResult(outputStream);

            // Serialize document to byte array stream
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(document), xmlOutput);

            // Parse document from in-memory byte array
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return domBuilderFactory.newDOMBuilder().parse(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException("Exception while reloading DOM", ex);
        } catch (SAXException ex) {
            throw new RuntimeException("Exception while reloading DOM", ex);
        } catch (TransformerException ex) {
            throw new RuntimeException("Exception while reloading DOM", ex);
        } catch (TransformerFactoryConfigurationError ex) {
            throw new RuntimeException("Exception while reloading DOM", ex);
        }
    }

    private String getPrefixedName(SchemaProperty schemaProperty) {
        String name = schemaProperty.getName().getLocalPart();
        if (XML_NAMESPACE.equals(schemaProperty.getName().getNamespaceURI())) {
            name = "xml:" + name;
        }
        return name;
    }

    /**
     * @return the EntityResolver used by XmlBeans
     * @see XmlOptions#setEntityResolver(org.xml.sax.EntityResolver)
     */
    private EntityResolver getEntityResolver() {
        return entityResolver;
    }

    private XPath newXPath(XPathFactory factory) {
        final XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(new CMDINamespaceContext());
        return xPath;
    }
}
