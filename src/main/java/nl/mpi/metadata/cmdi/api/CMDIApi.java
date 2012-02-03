/*
 * Copyright (C) 2011 Max Planck Institute for Psycholinguistics
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.MetadataElementType;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * CMDI implementation of the @see MetadataAPI
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApi implements MetadataAPI<CMDIProfile, CMDIMetadataElement, CMDIContainerMetadataElement, CMDIDocument> {

    private MetadataDocumentReader<CMDIDocument> documentReader;
    private MetadataValidator<CMDIDocument> cmdiValidator;
    private EntityResolver entityResolver;

    public CMDIApi() {
	this(new CMDIDocumentReader());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader) {
	this(documentReader, new DefaultCMDIValidator());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator) {
	this.documentReader = documentReader;
	this.cmdiValidator = cmdiValidator;
    }

    public CMDIDocument getMetadataDocument(URL url) throws IOException {
	InputStream documentStream = url.openStream();
	try {
	    return getDocumentReader().read(documentStream);
	} finally {
	    documentStream.close();
	}
    }

    public CMDIDocument createMetadataDocument(CMDIProfile type) throws MetadataDocumentException {
	// Create new DOM instance
	final DocumentBuilder documentBuilder = getDocumentBuilder();
	final Document document = documentBuilder.newDocument();

	CMDIComponentBuilder componentBuilder = new CMDIComponentBuilder(entityResolver);
	try {
	    componentBuilder.readSchema(document, type.getSchemaLocation(), true);
	    // TODO: Handle errors properly
	} catch (FileNotFoundException ex) {
	    throw new MetadataDocumentException(null, ex);
	} catch (XmlException ex) {
	    throw new MetadataDocumentException(null, ex);
	} catch (IOException ex) {
	    throw new MetadataDocumentException(null, ex);
	}


	return new CMDIDocument(document, type);
    }

    public MetadataElement createMetadataElement(CMDIContainerMetadataElement parent, MetadataElementType type) {
	// Take the type of the parent
	// Check if child type is allowed
	// Add to DOM
	// Instantiate
	// Add to parent
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public MetadataElement removeElement(CMDIMetadataElement element) throws MetadataDocumentException {
	// Find parent
	// Remove from DOM
	// Remove as child in parent
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
	getCmdiValidator().validateMetadataDocument(document, errorHandler);
    }

    protected DocumentBuilder getDocumentBuilder() {
	try {
	    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    configureDocumentBuilderFactory(factory);
	    return factory.newDocumentBuilder();
	} catch (ParserConfigurationException pcEx) {
	    throw new RuntimeException(pcEx);
	}
    }

    protected void configureDocumentBuilderFactory(final DocumentBuilderFactory factory) {
	factory.setNamespaceAware(true);
	factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    }

    protected void configureDocumentBuilder(final DocumentBuilder builder) {
	if (getEntityResolver() != null) {
	    builder.setEntityResolver(getEntityResolver());
	}
    }

    /**
     * Get the value of documentReader
     *
     * @return the value of documentReader
     */
    public MetadataDocumentReader<CMDIDocument> getDocumentReader() {
	return documentReader;
    }

    public void setDocumentReader(MetadataDocumentReader<CMDIDocument> documentReader) {
	this.documentReader = documentReader;
    }

    /**
     * @return the cmdiValidator
     */
    public MetadataValidator<CMDIDocument> getCmdiValidator() {
	return cmdiValidator;
    }

    /**
     * @param cmdiValidator the cmdiValidator to set
     */
    public void setCmdiValidator(MetadataValidator<CMDIDocument> cmdiValidator) {
	this.cmdiValidator = cmdiValidator;
    }

    /**
     * @param entityResolver the entityResolver to set
     */
    public void setEntityResolver(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }

    /**
     * @return the entityResolver
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
    }
}
