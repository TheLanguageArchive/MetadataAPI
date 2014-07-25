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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.dom.DomBuildingMode;
import nl.mpi.metadata.api.dom.MetadataDocumentReader;
import nl.mpi.metadata.api.dom.MetadataDocumentWriter;
import nl.mpi.metadata.api.model.MetadataElementAttributeContainer;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.dom.CMDIApiDOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIComponentReader;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentReader;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentWriter;
import nl.mpi.metadata.cmdi.api.dom.CMDIDomBuilder;
import nl.mpi.metadata.cmdi.api.dom.CMDIResourceProxyReader;
import nl.mpi.metadata.cmdi.api.dom.DOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.model.Attribute;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIMetadataElementFactoryImpl;
import nl.mpi.metadata.cmdi.api.model.impl.DisplayValueStrategy;
import nl.mpi.metadata.cmdi.api.type.CMDIAttributeType;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.impl.CMDIProfileContainerImpl;
import nl.mpi.metadata.cmdi.api.type.impl.CMDIProfileReader;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * CMDI implementation of the MetadataAPI
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApi implements MetadataAPI<CMDIProfile, CMDIProfileElement, CMDIAttributeType, Attribute, CMDIContainerMetadataElement, CMDIDocument> {

    private final static Logger logger = LoggerFactory.getLogger(CMDIApi.class);
    /**
     * SAX entity resolver for custom resolving of resources while parsing
     */
    private final EntityResolver entityResolver;
    /**
     * Service that deserializes an existing CMDI document on disk into a CMDIDocumentImpl object
     */
    private final MetadataDocumentReader<CMDIDocument> documentReader;
    /**
     * Service that writes a CMDI document to an XML result
     */
    private final MetadataDocumentWriter<CMDIDocument> documentWriter;
    /**
     * Service that reads an existing CMDI profile
     */
    private final MetadataDocumentTypeReader<CMDIProfile> profileReader;
    /**
     * Service that validates an existing CMDI Document instance on disk
     */
    private final MetadataValidator<CMDIDocument> cmdiValidator;
    /**
     * Service that caches CMDIProfile's and ensures that only one copy of each profile is opened.
     */
    private final CMDIMetadataElementFactory metadataElementFactory;
    private final CMDIProfileContainer profileContainer;
    private final DOMBuilderFactory domBuilderFactory;
    /**
     * Service that manipulates DOM representation of CMDI documents
     * TODO: Extract interface and support arbitrary implementations
     */
    private final CMDIDomBuilder componentBuilder;

    /**
     * Creates an instance of CMDIApi with
     * a new {@link CMDIEntityResolver}
     * a new {@link DefaultCMDIValidator},
     * a new {@link CMDIMetadataElementFactoryImpl}
     * and a new {@link CMDIDocumentReader}, {@link CMDIDocumentWriter} and {@link CMDIProfileReader} based on those
     */
    public CMDIApi() {
	this(new CMDIEntityResolver(), new DefaultCMDIValidator(), new CMDIMetadataElementFactoryImpl());
    }

    public CMDIApi(EntityResolver entityResolver, MetadataValidator<CMDIDocument> cmdiValidator, CMDIMetadataElementFactory elementFactory) {
	this.entityResolver = entityResolver;
	this.cmdiValidator = cmdiValidator;
	this.metadataElementFactory = elementFactory;

	this.domBuilderFactory = new CMDIApiDOMBuilderFactory(entityResolver);
	this.componentBuilder = new CMDIDomBuilder(entityResolver, domBuilderFactory);
	this.documentWriter = new CMDIDocumentWriter(componentBuilder);
	
	this.profileReader = new CMDIProfileReader(entityResolver, domBuilderFactory);
	this.profileContainer = new CMDIProfileContainerImpl(profileReader);
	this.documentReader = new CMDIDocumentReader(profileContainer, new CMDIComponentReader(elementFactory), new CMDIResourceProxyReader());
    }

    /**
     *
     * @param documentReader the MetadataDocumentReader to use
     * @param documentWriter the MetadataDocumentWriter to use
     * @param profileReader the MetadataProfileReader to use
     * @param cmdiValidator the MetadataValidator to use
     * @param entityResolver the EntityResolver to use
     * @param elementFactory the CMDIMetadataElementFactory to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataDocumentWriter<CMDIDocument> documentWriter, MetadataDocumentTypeReader<CMDIProfile> profileReader, MetadataValidator<CMDIDocument> cmdiValidator, EntityResolver entityResolver, CMDIMetadataElementFactory elementFactory) {
	this.entityResolver = entityResolver;
	this.cmdiValidator = cmdiValidator;
	this.metadataElementFactory = elementFactory;
	this.documentWriter = documentWriter;	
	this.profileReader = profileReader;
	this.documentReader = documentReader;
	
	this.domBuilderFactory = new CMDIApiDOMBuilderFactory(entityResolver);
	this.componentBuilder = new CMDIDomBuilder(entityResolver, domBuilderFactory);
	this.profileContainer = new CMDIProfileContainerImpl(profileReader);
    }

    @Override
    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
	getCmdiValidator().validateMetadataDocument(document, errorHandler);
    }

    /**
     * Reads the metadata document at the specified URL
     *
     * @param url location to read the document from (will be read by means of {@link URL#openStream() })
     * @return a CMDI document representing the contents at the specified URL
     * @throws IOException in case of a reading error
     * @throws MetadataException in case of a parsing or content error
     */
    @Override
    public CMDIDocument getMetadataDocument(URL url) throws IOException, MetadataException {
	logger.debug("Opening stream for {}", url);
        final InputStream documentStream;
        URLConnection openConnection = url.openConnection();
        if(openConnection instanceof HttpURLConnection) {
            HttpURLConnection openUrlConnection = (HttpURLConnection)openConnection;
            /**
             * Following redirects might not work in all situations, especially when
             * switching protocols http <--> https for example.
             */
            openUrlConnection.setInstanceFollowRedirects(true);
            logger.debug("Http Url Connection following redirects");
            documentStream = openUrlConnection.getInputStream();
            int responseCode = openUrlConnection.getResponseCode();
            if(responseCode != 200) {
                if(responseCode >= 300 && responseCode < 400) {
                    String location = openUrlConnection.getHeaderField("Location");
                    if(location != null) {
                        String originalProtocol = url.getProtocol();
                        String redirectedProtocol = (new URL(location)).getProtocol();
                        if(originalProtocol.compareTo(redirectedProtocol) != 0) {
                            throw new IOException("Unexpected HTTP response code, protocol switch ["+originalProtocol+" --> "+redirectedProtocol+"] prevented following redirects.");
                        }
                    }
                } 
                throw new IOException("Unexpected HTTP response code "+openUrlConnection.getResponseCode()+" != 200. Message = ["+openUrlConnection.getResponseMessage()+"]");
            }            
        } else {
            documentStream = openConnection.getInputStream();
        }
	try {
	    return getMetadataDocument(url, documentStream);
	} finally {
	    logger.debug("Closing stream for {}", url);
	    documentStream.close();
	}
    }

    /**
     * Loads the metadata document from the provided input stream. The caller is responsible for closing the input stream afterwards!
     *
     * @param url URL of the document. This will not be used to locate the contents but will be passed on to the DOM builder (retrieved from
     * {@link #domBuilderFactory} as the systemId while parsing. It will not be closed in this method!
     * @param documentStream stream from which the metadata document will be read
     * @return a CMDI document representing the contents read from the provided stream
     * @throws IOException in case of a reading error
     * @throws MetadataException in case of a parsing or content error
     * @see DocumentBuilder#parse(java.io.InputStream, java.lang.String)
     */
    @Override
    public CMDIDocument getMetadataDocument(URL url, InputStream documentStream) throws IOException, MetadataException {
	try {
	    logger.debug("Reading DOM for {}", url);
	    Document document = domBuilderFactory.newDOMBuilder().parse(documentStream, url.toExternalForm());
	    logger.debug("Reading contents of {}", url);
	    return getDocumentReader().read(document, url.toURI());
	} catch (SAXException saxEx) {
	    throw new MetadataException("SAXException while building document from " + url, saxEx);
	} catch (URISyntaxException usEx) {
	    // This should not happen, since at this point the stream has already been openend!
	    throw new RuntimeException("URISyntaxException while building document from " + url, usEx);
	}
    }

    @Override
    public CMDIDocument createMetadataDocument(CMDIProfile type) throws MetadataException, MetadataTypeException {
	return createMetadataDocument(type, DomBuildingMode.MANDATORY);
    }

    @Override
    public CMDIDocument createMetadataDocument(CMDIProfile type, DomBuildingMode buildingMode) throws MetadataException, MetadataTypeException {
	// Create new DOM instance
	Document document;

	try {
	    document = componentBuilder.createDomFromSchema(type.getSchemaLocation(), buildingMode);
	    // TODO: Handle errors properly
	} catch (FileNotFoundException ex) {
	    throw new MetadataTypeException(type, ex);
	} catch (XmlException ex) {
	    throw new MetadataTypeException(type, ex);
	} catch (IOException ex) {
	    throw new MetadataTypeException(type, ex);
	}
	try {
	    // Read from reloaded copy of document. No URI available at this point.
	    return documentReader.read(document, null);
	} catch (IOException ex) {
	    throw new MetadataException(
		    "I/O exception while reading newly created metadata document. "
		    + "Most likely the profile schema is not readable. See the inner exception for details.", ex);
	}
    }

    @Override
    public CMDIProfile getMetadataDocumentType(URI uri) throws IOException, MetadataException {
	return getProfileContainer().getProfile(uri);
    }

    @Override
    public void writeMetadataDocument(CMDIDocument document, StreamResult target) throws IOException, MetadataException, TransformerException {
	getDocumentWriter().write(document, target);
    }

    /**
     * Creates a new element of the specified type and adds it to the specified container
     *
     * @param container element container to add the new element to
     * @param elementType type of the new element to be added
     * @param strategy
     * @return newly created element, null if not added
     * @throws MetadataElementException if specified types are not compatible, or if an error occurs while registering the child with the
     * container
     */
    @Override
    public CMDIMetadataElement insertMetadataElement(CMDIContainerMetadataElement container, CMDIProfileElement elementType) throws MetadataException {
	if (!container.getType().canContainType(elementType)) {
	    throw new MetadataElementException(container, String.format("Element type %1$s cannot be contained by provided container type %2$s", elementType, container.getType()));
	}
	CMDIMetadataElement newMetadataElement = 
                metadataElementFactory.createNewMetadataElement(container, elementType);
	if (container.addChildElement(newMetadataElement)) {
	    return newMetadataElement;
	} else {
	    return null;
	}
    }

    @Override
    public Attribute insertAttribute(MetadataElementAttributeContainer<Attribute> container, CMDIAttributeType attributeType) throws MetadataException {
	if (!(container instanceof CMDIMetadataElement)) {
	    throw new MetadataException(String.format("Cannot handle container of type %1$s" + container.getClass()));
	}

	CMDIMetadataElement containerElement = (CMDIMetadataElement) container;
	if (!containerElement.getType().getAttributes().contains(attributeType)) {
	    throw new MetadataElementException(containerElement, String.format("Attribute type %1$s cannot be contained by provided element container type %2$s", attributeType, containerElement.getType()));
	}

	Attribute attribute = metadataElementFactory.createAttribute(containerElement, attributeType);
	if (container.addAttribute(attribute)) {
	    return attribute;
	} else {
	    return null;
	}
    }

    //<editor-fold defaultstate="collapsed" desc="Getters and setters for services">
    /**
     * Gets the CMDI Document reader used
     *
     * @return the CMDI Document reader used
     */
    public MetadataDocumentReader<CMDIDocument> getDocumentReader() {
	return documentReader;
    }

    /**
     * Gets the CMDI Document writer used
     *
     * @return the CMDI Document writer used
     */
    public MetadataDocumentWriter<CMDIDocument> getDocumentWriter() {
	return documentWriter;
    }

    /**
     * @return the profileReader
     */
    public MetadataDocumentTypeReader<CMDIProfile> getProfileReader() {
	return profileReader;
    }

    /**
     * Gets the CMDI Validator being used
     *
     * @return the CMDI Validator being used
     */
    public MetadataValidator<CMDIDocument> getCmdiValidator() {
	return cmdiValidator;
    }

    /**
     * Gets the SAX EntityResolver being used
     *
     * @return the SAX EntityResolver being used
     */
    protected EntityResolver getEntityResolver() {
	return entityResolver;
    }

    /**
     * @return the profileContainer used in this API instance
     */
    public CMDIProfileContainer getProfileContainer() {
	return profileContainer;
    }
    //</editor-fold>
}
