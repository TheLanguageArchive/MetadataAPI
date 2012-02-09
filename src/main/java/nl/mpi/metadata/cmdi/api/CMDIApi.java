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
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * CMDI implementation of the MetadataAPI
 * 
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApi implements MetadataAPI<CMDIProfile, CMDIMetadataElement, CMDIContainerMetadataElement, CMDIDocument> {

    /**
     * SAX entity resolver for custom resolving of resources while parsing
     */
    private EntityResolver entityResolver;
    /**
     * Service that reads deserializes an existing CMDI document on disk into a CMDIDocument object
     */
    private MetadataDocumentReader<CMDIDocument> documentReader;
    /**
     * Service that validates an existing CMDI Document instance on disk
     */
    private MetadataValidator<CMDIDocument> cmdiValidator;
    /**
     * Service that manipulates DOM representation of CMDI documents
     * TODO: Extract interface and support arbitrary implementations
     */
    private CMDIComponentBuilder componentBuilder = new CMDIComponentBuilder() {

	@Override
	protected synchronized EntityResolver getEntityResolver() {
	    return CMDIApi.this.entityResolver;
	}
    };
    /**
     * Service that caches CMDIProfile's and ensures that only one copy of each profile is opened.
     */
    private CMDIProfileContainer profileContainer = new CMDIProfileContainer() {

	@Override
	public synchronized EntityResolver getEntityResolver() {
	    return CMDIApi.this.entityResolver;
	}
    };

    /**
     * Creates an instance of CMDIApi with a {@link CMDIDocumentReader} and a {@link DefaultCMDIValidator}
     * @see CMDIDocumentReader
     */
    public CMDIApi() {
	this.documentReader = new CMDIDocumentReader(profileContainer);
	this.cmdiValidator = new DefaultCMDIValidator();
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader and a {@link DefaultCMDIValidator}
     * @param documentReader the MetadataDocumentReader to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader) {
	this.documentReader = documentReader;
	this.cmdiValidator = new DefaultCMDIValidator();
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader and MetadataValidator for CMDIDocuments
     * @param documentReader the MetadataDocumentReader to use
     * @param cmdiValidator the MetadataValidator to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator) {
	this.documentReader = documentReader;
	this.cmdiValidator = cmdiValidator;
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

    public CMDIDocument getMetadataDocument(URL url) throws IOException, MetadataDocumentException {
	InputStream documentStream = url.openStream();
	try {
	    Document document = newDOMBuilder().parse(documentStream, url.toExternalForm());
	    return getDocumentReader().read(document);
	} catch (SAXException saxEx) {
	    throw new MetadataDocumentException(null, "SAXException while building document from " + url, saxEx);
	} finally {
	    documentStream.close();
	}
    }

    public CMDIDocument createMetadataDocument(CMDIProfile type) throws MetadataDocumentException {
	// Create new DOM instance
	final DocumentBuilder documentBuilder = newDOMBuilder();
	final Document document = documentBuilder.newDocument();

	try {
	    componentBuilder.readSchema(document, type.getSchemaLocation(), true);
	    // TODO: Handle errors properly
	} catch (FileNotFoundException ex) {
	    throw new MetadataDocumentException(ex);
	} catch (XmlException ex) {
	    throw new MetadataDocumentException(ex);
	} catch (IOException ex) {
	    throw new MetadataDocumentException(ex);
	}

	try {
	    return documentReader.read(document);
	} catch (IOException ex) {
	    throw new MetadataDocumentException(
		    "I/O exception while reading newly created metadata document. "
		    + "Most likely the profile schema is not readable. See the inner exception for details.", ex);
	}
    }

    /**
     * Creates a fresh instance of DocumentBuilder. A new factory is requested from the DocumentBuilderFactory, which then gets
     * configured by {@code configureDocumentBuilderFactory(DocumentBuilderFactory)} . On this factory {@code newDocumentBuilder()} is called. The resulting
     * builder gets configured by {@code configureDocumentBuilder(DocumentBuilder)} and is then returned.
     * @return a newly instantiated and configured DocumentBuilder from the DocumentBuilderFactory
     * @see DocumentBuilderFactory
     * @see #configureDocumentBuilderFactory(javax.xml.parsers.DocumentBuilderFactory) 
     * @see #configureDocumentBuilder(javax.xml.parsers.DocumentBuilder) 
     */
    protected DocumentBuilder newDOMBuilder() {
	try {
	    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    configureDocumentBuilderFactory(factory);
	    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
	    //configureDocumentBuilder(documentBuilder);
	    return documentBuilder;
	} catch (ParserConfigurationException pcEx) {
	    throw new RuntimeException(pcEx);
	}
    }

    /**
     * Configures a newly instantiated document factory. This gets called from {@code newDOMBuilder()} in this implementation
     * @param factory a new instance of DocumentFactory
     * @see #newDOMBuilder() 
     */
    protected void configureDocumentBuilderFactory(final DocumentBuilderFactory factory) {
	factory.setNamespaceAware(true);
	factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
    }

    /**
     * Configures a newly instantiated document builder. This gets called from {@code newDOMBuilder()} in this implementation
     * @param builder a new instance of DocumentBuilder
     * @see #newDOMBuilder() 
     */
    protected void configureDocumentBuilder(final DocumentBuilder builder) {
	if (getEntityResolver() != null) {
	    builder.setEntityResolver(getEntityResolver());
	}
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and setters">
    /**
     * Gets the CMDI Docuent reader used
     * @return the CMDI Docuent reader used
     */
    public MetadataDocumentReader<CMDIDocument> getDocumentReader() {
	return documentReader;
    }

    /**
     * Sets the CMDI Docuent reader to use
     * @param documentReader the CMDI Document reader to use
     */
    public void setDocumentReader(MetadataDocumentReader<CMDIDocument> documentReader) {
	this.documentReader = documentReader;
    }

    /**
     * Gets the CMDI Validator being used
     * @return the CMDI Validator being used
     */
    public MetadataValidator<CMDIDocument> getCmdiValidator() {
	return cmdiValidator;
    }

    /**
     * Sets the CMDI Validator to use
     * @param cmdiValidator the CMDI Validator to use
     */
    public void setCmdiValidator(MetadataValidator<CMDIDocument> cmdiValidator) {
	this.cmdiValidator = cmdiValidator;
    }

    /**
     * Gets the SAX EntityResolver being used
     * @return the SAX EntityResolver being used
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
    }

    /**
     * Sets the SAX EntityResolver to use
     * @param entityResolver the SAX EntityResolver to use
     */
    public void setEntityResolver(EntityResolver entityResolver) {
	this.entityResolver = entityResolver;
    }
    //</editor-fold>
}
