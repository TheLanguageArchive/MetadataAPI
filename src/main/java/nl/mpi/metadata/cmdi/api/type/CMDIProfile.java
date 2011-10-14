/*
 * Copyright (C) 2011 The Max Planck Institute for Psycholinguistics
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.EntityResolver;

/**
 * This class represents a CMDI profile, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For an example profile, see http://www.clarin.eu/cmd/example/example-profile-instance.xml
 * For an example profile schema file, see http://www.clarin.eu/cmd/example/example-md-schema.xsd
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfile extends ComponentType implements MetadataDocumentType {

    public static final String CMD_NAMESPACE = "http://www.clarin.eu/cmd/";
    public final static QName CMD_TYPE_NAME = new QName(CMD_NAMESPACE, "CMD");
    public final static QName COMPONENTS_TYPE_NAME = new QName(CMD_NAMESPACE, "Components");
    public final static QName HEADER_TYPE_NAME = new QName(CMD_NAMESPACE, "Header");
    private URI schemaLocation;
    private EntityResolver entityResolver;

    public CMDIProfile(URI schemaLocation) throws IOException, CMDITypeException {
	this(schemaLocation, new CMDIEntityResolver());
    }

    public CMDIProfile(URI schemaLocation, EntityResolver entityResolver) throws IOException, CMDITypeException {
	super(null, null);
	this.schemaLocation = schemaLocation;
	this.entityResolver = entityResolver;
	setSchemaElement(loadSchema());
	readSchema();
    }

    public URI getSchemaLocation() {
	return schemaLocation;
    }

    /**
     * Loads the schema file, i.e. finds the root component element 
     * 
     * @return
     * @throws IOException
     * @throws CMDITypeException 
     */
    private SchemaProperty loadSchema() throws IOException, CMDITypeException {
	URL schemaUrl = schemaLocation.toURL();
	InputStream inputStream = schemaUrl.openStream();
	try {
	    XmlOptions xmlOptions = new XmlOptions();
	    xmlOptions.setCharacterEncoding("UTF-8");
	    if (entityResolver != null) {
		xmlOptions.setEntityResolver(entityResolver);
	    }

	    // Compile schema
	    SchemaTypeSystem sts = XmlBeans.compileXsd(new XmlObject[]{XmlObject.Factory.parse(inputStream, xmlOptions)}, XmlBeans.getBuiltinTypeSystem(), xmlOptions);
	    // Find document root element type (CMD)
	    SchemaType cmdType = findCmdType(sts);
	    // Find components root element type (CMD/Component)
	    SchemaProperty componentsElement = findComponentsElement(cmdType);
	    // Find child, there should be only one
	    return findRootComponentElement(componentsElement);

	} catch (XmlException ex) {
	    throw new CMDITypeException("XML exception while loading schema " + schemaUrl, ex);
	} finally {
	    inputStream.close();
	}
    }

    private SchemaType findCmdType(SchemaTypeSystem sts) throws CMDITypeException {
	// Get CMD root element
	SchemaType[] documentTypes = sts.documentTypes();
	if (documentTypes.length != 1) {
	    throw new CMDITypeException("DocumentTypes count for profile schema should be exactly 1, found " + documentTypes.length);
	}
	SchemaType cmdType = documentTypes[0].getElementProperty(CMD_TYPE_NAME).getType();
	if (cmdType == null) {
	    throw new CMDITypeException("Element CMD not found in profile schema");
	}
	return cmdType;
    }

    private SchemaProperty findComponentsElement(SchemaType cmdType) throws CMDITypeException {
	SchemaProperty componentsType = cmdType.getElementProperty(COMPONENTS_TYPE_NAME);
	if (componentsType == null) {
	    throw new CMDITypeException("Element Components not found in profile schema");
	}
	return componentsType;
    }

    private SchemaProperty findRootComponentElement(SchemaProperty componentsElement) throws CMDITypeException {
	SchemaProperty[] componentsChildren = componentsElement.getType().getElementProperties();
	if (componentsChildren.length != 1) {
	    throw new CMDITypeException("Expecting 1 root component for profile, found " + componentsChildren.length);
	}
	return componentsChildren[0];
    }
}
