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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import nl.mpi.metadata.api.dom.MetadataDOMBuilder;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;

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

    public Document writeToDom(CMDIDocument document) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public final Document createDomFromSchema(URI xsdFile, boolean addDummyData) throws FileNotFoundException, XmlException, MalformedURLException, IOException {
	Document workingDocument = domBuilderFactory.newDOMBuilder().newDocument();
	SchemaType schemaType = getFirstSchemaType(xsdFile);
	constructXml(schemaType.getElementProperties()[0], workingDocument, xsdFile.toString(), null, addDummyData);
	return workingDocument;
    }

    /**
     * @return the EntityResolver used by XmlBeans
     * @see XmlOptions#setEntityResolver(org.xml.sax.EntityResolver) 
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
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

    private Node constructXml(SchemaProperty currentSchemaProperty, Document workingDocument, String nameSpaceUri, Node parentElement, boolean addDummyData) {
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
	    for (BigInteger addNodeCounter = BigInteger.ZERO; addNodeCounter.compareTo(maxNumberToAdd) < 0; addNodeCounter = addNodeCounter.add(BigInteger.ONE)) {
		constructXml(schemaProperty, workingDocument, nameSpaceUri, currentElement, addDummyData);
	    }
	}
	return returnNode;
    }

    private Node appendNode(Document workingDocument, String nameSpaceUri, Node parentElement, SchemaProperty schemaProperty) {
	if (schemaProperty.isAttribute()) {
	    return appendAttributeNode(workingDocument, (Element) parentElement, schemaProperty);
	} else {
	    return appendElementNode(workingDocument, nameSpaceUri, parentElement, schemaProperty);
	}
    }

    private Attr appendAttributeNode(Document workingDocument, Element parentElement, SchemaProperty schemaProperty) {
	Attr currentAttribute = workingDocument.createAttributeNS(schemaProperty.getName().getNamespaceURI(), schemaProperty.getName().getLocalPart());
	if (schemaProperty.getDefaultText() != null) {
	    currentAttribute.setNodeValue(schemaProperty.getDefaultText());
	}
	parentElement.setAttributeNode(currentAttribute);
	return currentAttribute;
    }

    private Element appendElementNode(Document workingDocument, String nameSpaceUri, Node parentElement, SchemaProperty schemaProperty) {
	Element currentElement = workingDocument.createElementNS(schemaProperty.getName().getNamespaceURI(), schemaProperty.getName().getLocalPart());
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
}
