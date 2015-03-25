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
package nl.mpi.metadata.cmdi.api.validation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import nl.mpi.metadata.api.util.DefaultResourceResolver;
import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class DefaultCMDIValidator implements MetadataValidator<CMDIDocument> {

    private final static Logger logger = LoggerFactory.getLogger(DefaultCMDIValidator.class);
    private LSResourceResolver resourceResolver;

    /**
     * Construct with DefaultResourceResolver
     *
     * @see DefaultResourceResolver
     */
    public DefaultCMDIValidator() {
        this(new DefaultResourceResolver());
    }

    public DefaultCMDIValidator(LSResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
        logger.debug("Metadata validator instantiated with resourceResolver {}", resourceResolver);
    }

    @Override
    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
        try {
            final Validator validator = createValidator(document.getType().getSchemaLocation().toURL());
            validator.setErrorHandler(errorHandler);

            logger.trace("Validator details: {}", validator);

            // TODO: if file location is null, write to temporary file
            StreamSource xmlFile = new StreamSource(new File(document.getFileLocation()));
            validator.validate(xmlFile);
        } catch (IOException ioEx) {
            throw new RuntimeException("I/O error while validating CMDI document", ioEx);
        }
    }

    protected Validator createValidator(URL schemaFile) throws SAXException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(getResourceResolver());

        logger.debug("Creating schema for {} with {}", schemaFile, schemaFactory);
        final Schema schema = schemaFactory.newSchema(schemaFile);
        final Validator validator = schema.newValidator();

        logger.debug("Validator of type '{}' created", validator.getClass());
        return validator;
    }

    protected void configureSchemaFactory(SchemaFactory schemaFactory) {
        schemaFactory.setResourceResolver(getResourceResolver());
    }

    protected void configureValidator(Validator validator) {
        validator.setResourceResolver(getResourceResolver());
    }

    /**
     * Get the value of resourceResolver
     *
     * @return the value of resourceResolver
     */
    public LSResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * Set the value of resourceResolver
     *
     * @param resourceResolver new value of resourceResolver
     */
    public void setResourceResolver(LSResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }
}
