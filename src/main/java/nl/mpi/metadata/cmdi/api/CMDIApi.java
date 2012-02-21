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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataAPI;
import nl.mpi.metadata.api.MetadataDocumentException;
import nl.mpi.metadata.api.MetadataDocumentReader;
import nl.mpi.metadata.api.MetadataElementException;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.MetadataTypeException;
import nl.mpi.metadata.api.model.ContainedMetadataElement;
import nl.mpi.metadata.api.model.MetadataContainer;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.model.CMDIContainerMetadataElement;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.CMDIMetadataElement;
import nl.mpi.metadata.cmdi.api.model.Component;
import nl.mpi.metadata.cmdi.api.model.Element;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileContainer;
import nl.mpi.metadata.cmdi.api.type.CMDIProfileElement;
import nl.mpi.metadata.cmdi.api.type.ComponentType;
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
     * Factory that instantiates {@link Component Components} and {@link Element Elements}
     */
    private CMDIMetadataElementFactory elementFactory;
    /**
     * Service that manipulates DOM representation of CMDI documents
     * TODO: Extract interface and support arbitrary implementations
     */
    private CMDIDomBuilder componentBuilder = new CMDIDomBuilder() {

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
    private DOMBuilderFactory domBuilderFactory = new CMDIApiDOMBuilderFactory() {

	@Override
	protected EntityResolver getEntityResolver() {
	    return CMDIApi.this.entityResolver;
	}
    };

    /**
     * Creates an instance of CMDIApi with a {@link CMDIDocumentReader} and a {@link DefaultCMDIValidator}
     * @see CMDIDocumentReader
     */
    public CMDIApi() {
	this(null);
	this.documentReader = new CMDIDocumentReader(profileContainer, new CMDIComponentReader(elementFactory));
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader and a {@link DefaultCMDIValidator}
     * @param documentReader the MetadataDocumentReader to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader) {
	this(documentReader, new DefaultCMDIValidator());
    }

    /**
     * Creates an instance of CMDIApi with the specified MetadataDocumentReader and MetadataValidator for CMDIDocuments
     * @param documentReader the MetadataDocumentReader to use
     * @param cmdiValidator the MetadataValidator to use
     */
    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator) {
	this(documentReader, cmdiValidator, new CMDIEntityResolver());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator, EntityResolver entityResolver) {
	this(documentReader, cmdiValidator, entityResolver, new CMDIMetadataElementFactory());
    }

    public CMDIApi(MetadataDocumentReader<CMDIDocument> documentReader, MetadataValidator<CMDIDocument> cmdiValidator, EntityResolver entityResolver, CMDIMetadataElementFactory elementFactory) {
	this.documentReader = documentReader;
	this.cmdiValidator = cmdiValidator;
	this.entityResolver = entityResolver;
	this.elementFactory = elementFactory;
    }

    /**
     * Creates a new metadata element as a child of a specified parent. This method will both instantatie a new element of the 
     * specified type, and register it with its parent as a new child.
     * @param parent container to add element to
     * @param type type of element to create
     * @return newly created element; null if it was not created
     * @throws MetadataDocumentException if specified type cannot, by its type, be contained in specified parent
     */
    public MetadataElement createMetadataElement(CMDIContainerMetadataElement parent, CMDIProfileElement type) throws MetadataElementException {
	final ComponentType parentType = parent.getType();
	if (parentType.canContainType(type)) {
	    CMDIMetadataElement newChild = elementFactory.createNewMetadataElement(parent, type);
	    if (parent.addChildElement(newChild)) {
		return newChild;
	    } else {
		return null;
	    }
	} else {
	    throw new MetadataElementException(parent,
		    String.format("Elements of type %1$s cannot be added to components of type %2$s", type, parentType));
	}
    }

    /**
     * Removes a specified metadata element from its parent.
     * @param element metadata element to remove from parent
     * @return whether the element was removed from its parent
     * @throws MetadataElementException if the element to be removed does not implement ContainedMetadataElement, and thus has not
     * retrievable parent to remove it from
     */
    public boolean removeElement(CMDIMetadataElement element) throws MetadataElementException {
	if (element instanceof ContainedMetadataElement) {
	    MetadataContainer<CMDIMetadataElement> parent = ((ContainedMetadataElement<CMDIMetadataElement>) element).getParent();
	    return parent.removeChildElement(element);
	} else {
	    throw new MetadataElementException(element, "Attempt to remove element that is not of type ContainedMetadataElement: cannot retrieve parent.");
	}
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
	final DocumentBuilder documentBuilder = domBuilderFactory.newDOMBuilder();
	Document document = documentBuilder.newDocument();

	try {
	    componentBuilder.createDomFromSchema(document, type.getSchemaLocation(), true);
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
	    return documentReader.read(reloadDom(documentBuilder, document), null);
	} catch (IOException ex) {
	    throw new MetadataException(
		    "I/O exception while reading newly created metadata document. "
		    + "Most likely the profile schema is not readable. See the inner exception for details.", ex);
	}
    }

    /**
     * Serializes (in memory) and de-serializes XML document causing it to be re-processed
     * @param builder document builder to use
     * @param document document to reload
     * @return a reloaded copy of the provided document
     */
    private Document reloadDom(DocumentBuilder builder, Document document) {
	try {
	    // Create memory output stream
	    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    final StreamResult xmlOutput = new StreamResult(outputStream);

	    // Serialize document to byte array stream
	    final Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.transform(new DOMSource(document), xmlOutput);

	    // Parse document from in-memory byte array
	    final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
	    return builder.parse(inputStream);
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

    /**
     * @return the profileContainer used in this API instance
     */
    public CMDIProfileContainer getProfileContainer() {
	return profileContainer;
    }
    //</editor-fold>
}
