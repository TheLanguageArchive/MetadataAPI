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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
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
public class CMDIComponentBuilder {

    private final EntityResolver entityResolver;

    /**
     * Creates CMDIComponentBuilder with no EntityResolver specified
     * @see #CMDIComponentBuilder(org.xml.sax.EntityResolver) 
     */
    public CMDIComponentBuilder() {
	this(null);
    }

    /**
     * Creates CMDIComponentBuilder with a specified EntityResolver
     * @param entityResolver 
     * @see #getEntityResolver() 
     */
    public CMDIComponentBuilder(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }

    public final void readSchema(Document workingDocument, URI xsdFile, boolean addDummyData) throws FileNotFoundException, XmlException, IOException {
	File schemaFile = new File(xsdFile);
	SchemaType schemaType = getFirstSchemaType(schemaFile);
	constructXml(schemaType.getElementProperties()[0], "documentTypes", workingDocument, xsdFile.toString(), null, addDummyData);
    }

    /**
     * @return the EntityResolver used by XmlBeans
     * @see XmlOptions#setEntityResolver(org.xml.sax.EntityResolver) 
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
    }

    private SchemaType getFirstSchemaType(File schemaFile) throws FileNotFoundException, XmlException, IOException {
	InputStream inputStream = new FileInputStream(schemaFile);
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

    private Node constructXml(SchemaProperty currentSchemaProperty, String pathString, Document workingDocument, String nameSpaceUri, Node parentElement, boolean addDummyData) {
	Node returnNode = null;
	// this must be tested against getting the actor description not the actor of an imdi profile instance
	String currentPathString = pathString + "." + currentSchemaProperty.getName().getLocalPart();
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
		constructXml(schemaProperty, currentPathString, workingDocument, nameSpaceUri, currentElement, addDummyData);
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
	Element currentElement = workingDocument.createElementNS("http://www.clarin.eu/cmd/", schemaProperty.getName().getLocalPart());
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
	    currentElement.setAttribute("xsi:schemaLocation", "http://www.clarin.eu/cmd/ " + nameSpaceUri);
	    workingDocument.appendChild(currentElement);
	} else {
	    parentElement.appendChild(currentElement);
	}
	return currentElement;
    }
}
