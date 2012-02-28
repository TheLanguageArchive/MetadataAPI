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
import java.util.Map.Entry;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.api.model.HeaderInfo;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.XPathAPI;
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
 * Some code has been taken from the nl.mpi.arbil.data.ArbilComponentBuilder class of the Arbil metadata editor
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 * @author Peter.Withers@mpi.nl
 * @see <a href="http://tla.mpi.nl/tools/tla-tools/arbil">Arbil Metadata editor</a>
 */
public class CMDIDomBuilder implements MetadataDOMBuilder<CMDIDocument> {

    private final static Logger logger = LoggerFactory.getLogger(CMDIDomBuilder.class);
    private final EntityResolver entityResolver;
    private final DOMBuilderFactory domBuilderFactory;

    /**
     * Creates CMDIDomBuilder with no EntityResolver specified
     * @see #CMDIDomBuilder(org.xml.sax.EntityResolver) 
     */
    public CMDIDomBuilder(DOMBuilderFactory domBuilderFactory) {
	this(null, domBuilderFactory);
    }

    /**
     * Creates CMDIDomBuilder with a specified EntityResolver
     * @param entityResolver 
     * @see #getEntityResolver() 
     */
    public CMDIDomBuilder(EntityResolver entityResolver, DOMBuilderFactory domBuilderFactory) {
	this.entityResolver = entityResolver;
	this.domBuilderFactory = domBuilderFactory;
    }

    public Document buildDomForDocument(CMDIDocument metadataDocument) throws MetadataDocumentException {
	// Get base document
	Document domDocument = getBaseDocument(metadataDocument);
	pruneDom(metadataDocument, domDocument);
	setHeaders(metadataDocument, domDocument);
	buildComponents(metadataDocument, domDocument);
	// TODO: Set resource links
	return domDocument;
    }

    /**
     * Will create a base DOM document for the specified metadata document. If the document specifies a file location, this is loaded
     * as a DOM object; if not, a new DOM will be constructed by calling {@link #createDomFromSchema(java.net.URI, boolean)} with
     * {@code metadataDocument.getType().getSchemaLocation()}.
     * @param metadataDocument metadata document to crease base DOM for
     * @return 
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
		return createDomFromSchema(metadataDocument.getType().getSchemaLocation(), false);
	    } catch (IOException ioEx) {
		throw new MetadataDocumentException(metadataDocument, "IOException while trying to create new DOM from schema", ioEx);
	    } catch (XmlException xEx) {
		throw new MetadataDocumentException(metadataDocument, "XmlException while trying to create new DOM from schema", xEx);
	    }
	}
    }

    private void pruneDom(CMDIDocument metadataDocument, Document domDocument) throws MetadataDocumentException {
	try {
	    final CachedXPathAPI xPathAPI = new CachedXPathAPI();
	    Node headerNode = xPathAPI.selectSingleNode(domDocument, "/:CMD/:Header");
	    Node componentsNode = xPathAPI.selectSingleNode(domDocument, "/:CMD/:Components");
	    Node rootComponentNode = CMDIComponentReader.getRootComponentNode(metadataDocument, domDocument, xPathAPI);

	    // Remove header items
	    removeChildren(headerNode);
	    // Remove components
	    if (rootComponentNode.getParentNode().equals(componentsNode)) {
		componentsNode.removeChild(rootComponentNode);
	    } else {
		throw new MetadataDocumentException(metadataDocument, "Root component node specified by profile is not a child of Components node");
	    }
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(metadataDocument, "TransformerException while preparing for building metadata DOM", tEx);
	} catch (MetadataException mdEx) {
	    throw new MetadataDocumentException(metadataDocument, "MetadataException while preparing for building metadata DOM", mdEx);
	}
    }

    private void removeChildren(Node parent) throws DOMException, MetadataException {
	// replace node by undeep clone of itself
	parent.getParentNode().replaceChild(parent.cloneNode(false), parent);
    }

    private void setHeaders(CMDIDocument metadataDocument, Document domDocument) throws MetadataDocumentException {
	try {
	    Node headerNode = XPathAPI.selectSingleNode(domDocument, "/:CMD/:Header");
	    for (HeaderInfo<?> header : metadataDocument.getHeaderInformation()) {
		org.w3c.dom.Element headerItemNode = domDocument.createElementNS(CMDIConstants.CMD_NAMESPACE, header.getName());
		headerItemNode.setTextContent(header.getValue().toString());
		for (Entry<String, String> attribute : header.getAttributes().entrySet()) {
		    headerItemNode.setAttribute(attribute.getKey(), attribute.getValue());
		}
		headerNode.appendChild(headerItemNode);
	    }
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(metadataDocument, "TransformerException while setting headers in metadata DOM", tEx);
	}
    }

    private void buildComponents(CMDIDocument metadataDocument, Document domDocument) throws MetadataDocumentException {
	try {
	    final String schemaLocation = metadataDocument.getType().getSchemaLocation().toString();
	    final Node componentsNode = XPathAPI.selectSingleNode(domDocument, "/:CMD/:Components");
	    buildMetadataElement(domDocument, componentsNode, metadataDocument, schemaLocation);
	} catch (DOMException mdEx) {
	    throw new MetadataDocumentException(metadataDocument, "DOMException while building components in DOM", mdEx);
	} catch (TransformerException tEx) {
	    throw new MetadataDocumentException(metadataDocument, "TransformerException while building components in DOM", tEx);
	}
    }

    private void buildMetadataElement(Document domDocument, Node parentNode, CMDIMetadataElement metadataElement, String schemaLocation) throws DOMException {
	// Add child node to DOM
	org.w3c.dom.Element elementNode = appendElementNode(domDocument, schemaLocation, parentNode, metadataElement.getType().getSchemaElement());
	// Set value if element
	if (metadataElement instanceof Element) {
	    elementNode.setTextContent(((Element) metadataElement).getValue().toString());
	}
	// Add attributes
	for (Attribute attribute : metadataElement.getAttributes()) {
	    if (attribute.getType() instanceof CMDIAttributeType) {
		Node attrNode = appendAttributeNode(domDocument, elementNode, ((CMDIAttributeType) attribute.getType()).getSchemaElement());
		attrNode.setNodeValue(attribute.getValue().toString());
	    } else {
		logger.info("Found attribute of type other than CMDIAttributeType. Skipping attribute {}", attribute);
	    }
	}
	// Iterate over children if container
	if (metadataElement instanceof CMDIContainerMetadataElement) {
	    for (CMDIMetadataElement child : ((CMDIContainerMetadataElement) metadataElement).getChildren()) {
		buildMetadataElement(domDocument, elementNode, child, schemaLocation);
	    }
	}
    }

    public final Document createDomFromSchema(URI xsdFile, boolean addDummyData) throws FileNotFoundException, XmlException, MalformedURLException, IOException {
	Document workingDocument = domBuilderFactory.newDOMBuilder().newDocument();
	SchemaType schemaType = getFirstSchemaType(xsdFile);
	constructXml(schemaType.getElementProperties()[0], workingDocument, xsdFile.toString(), null, addDummyData);
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

    private Node constructXml(SchemaProperty currentSchemaProperty, Document workingDocument, String nameSpaceUri, org.w3c.dom.Element parentElement, boolean addDummyData) {
	Node returnNode = null;
	SchemaType currentSchemaType = currentSchemaProperty.getType();
	Node currentElement = appendNode(workingDocument, nameSpaceUri, parentElement, currentSchemaProperty);
	returnNode = currentElement;

	for (SchemaProperty schemaProperty : currentSchemaType.getElementProperties()) {
	    BigInteger maxNumberToAdd;
	    if (addDummyData) {
		maxNumberToAdd = schemaProperty.getMaxOccurs();
		BigInteger dummyNumberToAdd = BigInteger.ONE.add(BigInteger.ONE).add(BigInteger.ONE);
		if (maxNumberToAdd == null) {
		    maxNumberToAdd = dummyNumberToAdd;
		} else {
		    if (dummyNumberToAdd.compareTo(maxNumberToAdd) == -1) {
			// limit the number added and make sure it is less than the max number to add
			maxNumberToAdd = dummyNumberToAdd;
		    }
		}
	    } else {
		maxNumberToAdd = schemaProperty.getMinOccurs();
		if (maxNumberToAdd == null) {
		    maxNumberToAdd = BigInteger.ZERO;
		}
	    }
	    if (currentElement instanceof org.w3c.dom.Element) {
		for (BigInteger addNodeCounter = BigInteger.ZERO; addNodeCounter.compareTo(maxNumberToAdd) < 0; addNodeCounter = addNodeCounter.add(BigInteger.ONE)) {
		    constructXml(schemaProperty, workingDocument, nameSpaceUri, (org.w3c.dom.Element) currentElement, addDummyData);
		}
	    }
	}
	return returnNode;
    }

    private Node appendNode(Document workingDocument, String nameSpaceUri, org.w3c.dom.Element parentElement, SchemaProperty schemaProperty) {
	if (schemaProperty.isAttribute()) {
	    return appendAttributeNode(workingDocument, parentElement, schemaProperty);
	} else {
	    return appendElementNode(workingDocument, nameSpaceUri, parentElement, schemaProperty);
	}
    }

    private Attr appendAttributeNode(Document workingDocument, org.w3c.dom.Element parentElement, SchemaProperty schemaProperty) {
	Attr currentAttribute = workingDocument.createAttributeNS(schemaProperty.getName().getNamespaceURI(), schemaProperty.getName().getLocalPart());
	if (schemaProperty.getDefaultText() != null) {
	    currentAttribute.setNodeValue(schemaProperty.getDefaultText());
	}
	parentElement.setAttributeNode(currentAttribute);
	return currentAttribute;
    }

    private org.w3c.dom.Element appendElementNode(Document workingDocument, String nameSpaceUri, Node parentElement, SchemaProperty schemaProperty) {
	org.w3c.dom.Element currentElement = workingDocument.createElementNS(schemaProperty.getName().getNamespaceURI(), schemaProperty.getName().getLocalPart());
	SchemaType currentSchemaType = schemaProperty.getType();
	for (SchemaProperty attributesProperty : currentSchemaType.getAttributeProperties()) {
	    if (attributesProperty.getMinOccurs() != null && !attributesProperty.getMinOccurs().equals(BigInteger.ZERO)) {
		currentElement.setAttribute(attributesProperty.getName().getLocalPart(), attributesProperty.getDefaultText());
	    }
	}
	if (parentElement == null) {
	    // this is probably not the way to set these, however this will do for now (many other methods have been tested and all failed to function correctly)
	    currentElement.setAttribute("CMDVersion", "1.1");
	    currentElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	    currentElement.setAttribute("xsi:schemaLocation", CMDIConstants.CMD_NAMESPACE + " " + nameSpaceUri);
	    workingDocument.appendChild(currentElement);
	} else {
	    parentElement.appendChild(currentElement);
	}
	return currentElement;
    }

    /**
     * Serializes (in memory) and de-serializes XML document causing it to be re-processed
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

    /**
     * @return the EntityResolver used by XmlBeans
     * @see XmlOptions#setEntityResolver(org.xml.sax.EntityResolver) 
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
    }
}
