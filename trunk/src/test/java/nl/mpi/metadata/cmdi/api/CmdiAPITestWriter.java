/*
 * Copyright (C) 2014 Max Planck Institute for Psycholinguistics
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

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import nl.mpi.metadata.api.MetadataException;
import nl.mpi.metadata.api.dom.MetadataDocumentWriter;
import nl.mpi.metadata.cmdi.api.dom.CMDIApiDOMBuilderFactory;
import nl.mpi.metadata.cmdi.api.dom.CMDIDocumentWriter;
import nl.mpi.metadata.cmdi.api.dom.CMDIDomBuilder;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import nl.mpi.metadata.cmdi.api.model.DataResourceProxy;
import nl.mpi.metadata.cmdi.api.model.ResourceProxy;
import nl.mpi.metadata.cmdi.api.type.CMDIProfile;
import nl.mpi.metadata.cmdi.util.CMDIEntityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author twagoo
 */
public class CmdiAPITestWriter {

    private final static Logger logger = LoggerFactory.getLogger(CmdiAPITestWriter.class);
    private static final String PROFILE_URI = "http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1345561703682/xsd";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            final CMDIApi api = new CMDIApi();
            configureApi(api);

            logger.info("Loading profile... {}", PROFILE_URI);
            final CMDIProfile profile = api.getMetadataDocumentType(URI.create(PROFILE_URI));

            logger.info("Creating document");
            final CMDIDocument document = api.createMetadataDocument(profile);

            // add some stuff
            document.addDocumentResourceProxy(new DataResourceProxy("res1", URI.create("http://resource/1.txt"), new URL("file:/resources/1.txt"), "text/plain"));

            final StringWriter stringWriter = new StringWriter();
            api.writeMetadataDocument(document, new StreamResult(stringWriter));
            logger.info("Document:\n{}\n", stringWriter.toString());

        } catch (IOException ex) {
            logger.error("Could not read or write", ex);
        } catch (MetadataException ex) {
            logger.error("Could not process metadata", ex);
        } catch (TransformerException ex) {
            logger.error("Could not serialize metadata file");
        }
    }

    private static void configureApi(final CMDIApi api) {
        final CMDIDocumentWriter documentWriter = (CMDIDocumentWriter) api.getDocumentWriter();
        final Properties properties = new Properties();
        properties.setProperty(OutputKeys.INDENT, "yes");
        documentWriter.setOutputProperties(properties);
    }
}
