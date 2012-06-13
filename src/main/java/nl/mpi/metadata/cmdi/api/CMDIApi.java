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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.dom.MetadataDocumentReader;
import nl.mpi.metadata.api.dom.MetadataDocumentWriter;
import nl.mpi.metadata.api.type.MetadataDocumentTypeReader;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.dom.CMDIApiDOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIComponentReader;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentReader;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentWriter;
import nl.mpi.metadata.cmdi.api.dom.CMDIDomBuilder;
import nl.mpi.metadata.cmdi.api.dom.CMDIResourceProxyReader;
import nl.mpi.metadata.cmdi.api.dom.DOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElementFactory;
import nl.mpi.metadata.cmdi.api.model.impl.CMDIMetadataElementFactoryImpl;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileReader;
import nl.mpi.metadata.cmdi.api.validation.DefaultCMDIValidator;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
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
public class CMDIApi implements MetadataAPI<CMDIProfile, CMDIProfileElement, CMDIMetadataElement, CMDIContainerMetadataElement, CMDIDocument> {

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
    private final CMDIProfileContainer profileContainer = new CMDIProfileContainer() {

	@Override
	public synchronized MetadataDocumentTypeReader<CMDIProfile> getProfileReader() {
	    return CMDIApi.this.getProfileReader();
	}
    };
    private final DOMBuilderFactory domBuilderFactory = new CMDIApiDOMBuilderFactory() {

	@Override
	protected EntityResolver getEntityResolver() {
	    return CMDIApi.this.entityResolver;
	}
    };
    /**
     * Service that manipulates DOM representation of CMDI documents
     * TODO: Extract interface and support arbitrary implementations
     */
    private CMDIDomBuilder componentBuilder = new CMDIDomBuilder(domBuilderFactory) {

	@Override
	protected synchronized EntityResolver getEntityResolver() {
	    return CMDIApi.this.entityResolver;
	}
    };

    /**
     * Creates an instance of CMDIApi with
     * a new {@link CMDIEntityResolver}
     * a new {@link DefaultCMDIValidator},
     * a new {@link CMDIMetadataElementFactoryImpl}
     * and a new {@link CMDIDocumentReader}, {@link CMDIDocumentWriter} and {@link CMDIProfileReader} based on those
     */
    public CMDIApi() {
	this(new CMDIEntityResolver());
    }

    /**
     * Creates an instance of CMDIApi with the specified EntityResolver,
     * a new {@link DefaultCMDIValidator},
     * a new {@link CMDIMetadataElementFactoryImpl}
     * and a new {@link CMDIDocumentReader}, {@link CMDIDocumentWriter} and {@link CMDIProfileReader} based on those
     */
    public CMDIApi(EntityResolver entityResolver) {
	this(entityResolver, new DefaultCMDIValidator());
    }

    /**
     * Creates an instance of CMDIApi with the specified EntityResolver and MetadataValidator
     * a new {@link CMDIMetadataElementFactoryImpl}
     * and a new {@link CMDIDocumentReader}, {@link CMDIDocumentWriter} and {@link CMDIProfileReader} based on those
     */
    public CMDIApi(EntityResolver entityResolver, MetadataValidator<CMDIDocument> cmdiValidator) {
	this(entityResolver, cmdiValidator, new CMDIMetadataElementFactoryImpl());
    }

    /**
     * Creates an instance of CMDIApi with the specified EntityResolver and element factory,
     * a new {@link DefaultCMDIValidator},
     * and a new {@link CMDIDocumentReader}, {@link CMDIDocumentWriter} and {@link CMDIProfileReader} based on those
     */
    public CMDIApi(EntityResolver entityResolver, CMDIMetadataElementFactory elementFactory) {
	this(entityResolver, new DefaultCMDIValidator(), elementFactory);
    }

    public CMDIApi(EntityResolver entityResolver, MetadataValidator<CMDIDocument> cmdiValidator, CMDIMetadataElementFactory elementFactory) {
	this.entityResolver = entityResolver;
	this.cmdiValidator = cmdiValidator;
	this.metadataElementFactory = elementFactory;
	this.documentReader = new CMDIDocumentReader(profileContainer, new CMDIComponentReader(elementFactory), new CMDIResourceProxyReader());
	this.profileReader = new CMDIProfileReader(entityResolver, domBuilderFactory);
	this.documentWriter = new CMDIDocumentWriter(componentBuilder);
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader, MetadataDocumentWriter and MetadataDocumentTypeReader and a
     * {@link DefaultCMDIValidator}
     *
     * @param documentReader the MetadataDocumentReader to use
     * @param documentWriter the MetadataDocumentWriter to use
     * @param profileReader the MetadataProfileReader to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataDocumentWriter<CMDIDocument> documentWriter, MetadataDocumentTypeReader<CMDIProfile> profileReader) {
	this(documentReader, documentWriter, profileReader, new DefaultCMDIValidator());
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader, MetadataDocumentWriter, MetadataDocumentTypeReader and
     * MetadataValidator, and a new {@link CMDIMetadataElementFactoryImpl} and {@link CMDIEntityResolver} for CMDIDocuments
     *
     * @param documentReader the MetadataDocumentReader to use
     * @param documentWriter the MetadataDocumentWriter to use
     * @param profileReader the MetadataProfileReader to use
     * @param cmdiValidator the MetadataValidator to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataDocumentWriter<CMDIDocument> documentWriter, MetadataDocumentTypeReader<CMDIProfile> profileReader, MetadataValidator<CMDIDocument> cmdiValidator) {
	this(documentReader, documentWriter, profileReader, cmdiValidator, new CMDIEntityResolver());
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader, MetadataDocumentWriter, MetadataDocumentTypeReader,
     * MetadataValidator and EntityResolver, and a new {@link CMDIMetadataElementFactoryImpl}
     *
     * @param documentReader the MetadataDocumentReader to use
     * @param documentWriter the MetadataDocumentWriter to use
     * @param profileReader the MetadataProfileReader to use
     * @param cmdiValidator the MetadataValidator to use
     * @param entityResolver the EntityResolver to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataDocumentWriter<CMDIDocument> documentWriter, MetadataDocumentTypeReader<CMDIProfile> profileReader, MetadataValidator<CMDIDocument> cmdiValidator, EntityResolver entityResolver) {
	this(documentReader, documentWriter, profileReader, cmdiValidator, entityResolver, new CMDIMetadataElementFactoryImpl());
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
	this.documentReader = documentReader;
	this.documentWriter = documentWriter;
	this.profileReader = profileReader;
	this.cmdiValidator = cmdiValidator;
	this.entityResolver = entityResolver;
	this.metadataElementFactory = elementFactory;
    }

    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
	getCmdiValidator().validateMetadataDocument(document, errorHandler);
    }

    public CMDIDocument getMetadataDocument(URL url) throws IOException, MetadataException {
	InputStream documentStream = url.openStream();
	try {
	    Document document = domBuilderFactory.newDOMBuilder().parse(documentStream, url.toExternalForm());
	    return getDocumentReader().read(document, url.toURI());
	} catch (SAXException saxEx) {
	    throw new MetadataException("SAXException while building document from " + url, saxEx);
	} catch (URISyntaxException usEx) {
	    // This should not happen, since at this point the stream has already been openend!
	    throw new RuntimeException("URISyntaxException while building document from " + url, usEx);
	} finally {
	    documentStream.close();
	}
    }

    public CMDIDocument createMetadataDocument(CMDIProfile type) throws MetadataException, MetadataTypeException {
	// Create new DOM instance
	Document document;

	try {
	    document = componentBuilder.createDomFromSchema(type.getSchemaLocation(), false);
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

    public CMDIProfile getMetadataDocumentType(URI uri) throws IOException, MetadataException {
	return getProfileContainer().getProfile(uri);
    }

    public void writeMetadataDocument(CMDIDocument document, StreamResult target) throws IOException, MetadataException, TransformerException {
	getDocumentWriter().write(document, target);
    }

    /**
     * Creates a new element of the specified type and adds it to the specified container
     *
     * @param container element container to add the new element to
     * @param elementType type of the new element to be added
     * @return newly created element, null if not added
     * @throws MetadataElementException if specified types are not compatible, or if an error occurs while registering the child with the
     * container
     */
    public CMDIMetadataElement insertMetadataElement(CMDIContainerMetadataElement container, CMDIProfileElement elementType) throws MetadataException {
	if (!container.getType().canContainType(elementType)) {
	    throw new MetadataElementException(container, String.format("Element type %1$s cannot be contained by provided container type %2$s", elementType, container.getType()));
	}
	CMDIMetadataElement newMetadataElement = metadataElementFactory.createNewMetadataElement(container, elementType);
	if (container.addChildElement(newMetadataElement)) {
	    return newMetadataElement;
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
