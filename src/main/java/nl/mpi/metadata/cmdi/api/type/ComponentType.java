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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import nl.mpi.metadata.api.type.MetadataContainerElementType;
import nl.mpi.metadata.api.type.MetadataElementAttributeType;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategory;
import nl.mpi.metadata.cmdi.api.type.datacategory.DataCategoryType;
import nl.mpi.metadata.cmdi.type.schema.CMDComponentSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CMDI component definition, defined by http://www.clarin.eu/cmd/general-component-schema.xsd
 * 
 * For example components, see http://www.clarin.eu/cmd/example/
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class ComponentType implements MetadataContainerElementType, DataCategoryType {

    private static Logger logger = LoggerFactory.getLogger(ComponentType.class);

    public ComponentType(URL schemaUrl) {
	CMDComponentSpec spec = unmarshal(CMDComponentSpec.class, schemaUrl, null);
    }

    public Collection<MetadataElementType> getContainableTypes() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canContainType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMinOccurencesOfType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaxOccurencesOfType(MetadataElementType type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDescription() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<MetadataElementAttributeType> getAttributes() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public DataCategory getDataCategory() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Helper method that logs errors and returns null if unmarshal failed
     */
    public static <T> T unmarshal(Class<T> docClass, URL url, Schema schema) {
	T result = null;
	try {
	    result = unmarshal(docClass, url.openStream(), schema);
	} catch (JAXBException e) {
	    logger.error("Cannot unmarshal xml file: " + url, e);
	} catch (IOException e) {
	    logger.error("Cannot retrieve content from file: " + url, e);
	}
	return result;
    }

    /**
     * 
     * @param docClass
     * @param inputStream
     * @param schema to validate against, can be null for no validation.
     * @return
     * @throws JAXBException
     */
    public static <T> T unmarshal(Class<T> docClass, InputStream inputStream, Schema schema) throws JAXBException {
	String packageName = docClass.getPackage().getName();
	JAXBContext jc = JAXBContext.newInstance(packageName);
	Unmarshaller u = jc.createUnmarshaller();

	if (schema != null) {
	    u.setSchema(schema);
	}
	Object unmarshal = u.unmarshal(inputStream);
	T doc = (T) unmarshal;
	return doc;
    }
}
