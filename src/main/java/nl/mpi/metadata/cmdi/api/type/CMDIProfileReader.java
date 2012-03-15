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
package nl.mpi.metadata.cmdi.api.type;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.xml.namespace.QName;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.cmdi.api.CMDIConstants;
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
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIProfileReader implements MetadataDocumentTypeReader<CMDIProfile> {

    public final static QName CMD_TYPE_NAME = new QName(CMDIConstants.CMD_NAMESPACE, "CMD");
    public final static QName COMPONENTS_TYPE_NAME = new QName(CMDIConstants.CMD_NAMESPACE, "Components");
    public final static QName HEADER_TYPE_NAME = new QName(CMDIConstants.CMD_NAMESPACE, "Header");
    private EntityResolver entityResolver;

    /**
     * Constructs new CMDIProfileReader with a {@link CMDIEntityResolver}
     * @see CMDIEntityResolver
     */
    public CMDIProfileReader() {
	this(new CMDIEntityResolver());
    }

    /**
     * Constructs a CMDIProfileReader with the specified EntityResolver
     * @param entityResolver Entity resolver to be used while parsing the schema file
     */
    public CMDIProfileReader(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }

    public CMDIProfile read(URI uri) throws IOException, CMDITypeException {
	// Find the schema element
	SchemaProperty schemaElement = loadSchema(uri);
	// Determine root path
	StringBuilder rootPath = new StringBuilder("/:CMD/:Components/:").append(schemaElement.getName().getLocalPart());
	// Instantiate profile
	CMDIProfile profile = new CMDIProfile(uri, schemaElement, rootPath);

	// Read all of the schema
	profile.readSchema();

	return profile;
    }

    /**
     * Loads the schema file, i.e. finds the root component element 
     * 
     * @return
     * @throws IOException
     * @throws CMDITypeException 
     */
    private SchemaProperty loadSchema(URI uri) throws IOException, CMDITypeException {
	InputStream inputStream = CMDIEntityResolver.getInputStreamForURI(entityResolver, uri);
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
	    throw new CMDITypeException(null, "XML exception while loading schema " + uri, ex);
	} finally {
	    inputStream.close();
	}
    }

    private static SchemaType findCmdType(SchemaTypeSystem sts) throws CMDITypeException {
	// Get CMD root element
	SchemaType[] documentTypes = sts.documentTypes();
	if (documentTypes.length != 1) {
	    throw new CMDITypeException(null, "DocumentTypes count for profile schema should be exactly 1, found " + documentTypes.length);
	}
	SchemaType cmdType = documentTypes[0].getElementProperty(CMD_TYPE_NAME).getType();
	if (cmdType == null) {
	    throw new CMDITypeException(null, "Element CMD not found in profile schema");
	}
	return cmdType;
    }

    private static SchemaProperty findComponentsElement(SchemaType cmdType) throws CMDITypeException {
	SchemaProperty componentsType = cmdType.getElementProperty(COMPONENTS_TYPE_NAME);
	if (componentsType == null) {
	    throw new CMDITypeException(null, "Element Components not found in profile schema");
	}
	return componentsType;
    }

    private static SchemaProperty findRootComponentElement(SchemaProperty componentsElement) throws CMDITypeException {
	SchemaProperty[] componentsChildren = componentsElement.getType().getElementProperties();
	if (componentsChildren.length != 1) {
	    throw new CMDITypeException(null, "Expecting 1 root component for profile, found " + componentsChildren.length);
	}
	return componentsChildren[0];
    }
}
