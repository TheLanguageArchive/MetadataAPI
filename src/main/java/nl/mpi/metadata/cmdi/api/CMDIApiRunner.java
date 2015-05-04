/*
 * Copyright (C) 2015 Max Planck Institute for Psycholinguistics
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

import java.io.File;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIApiRunner {

    private final static Logger logger = LoggerFactory.getLogger(CMDIApiRunner.class);

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            if (args[0].equals("-v")) {
                validate(args[1]);
                System.exit(0);
            }
        }
        System.err.println("Options: \n"
                + " -v <filename>           validate file");
        System.exit(1);
    }

    private static void validate(String file) throws Exception {
        logger.info("------ Instantiating API...");
        final CMDIApi api = new CMDIApi();
        logger.info("------ Opening metadata document");
        final CMDIDocument document = api.getMetadataDocument(new File(file).toURI().toURL());
        logger.info("------ Validating metadata document");
        api.validateMetadataDocument(document, new DefaultHandler2() {

            @Override
            public void warning(SAXParseException e) throws SAXException {
                logger.warn("SAX warning", e);
            }
            @Override
            public void error(SAXParseException e) throws SAXException {
                logger.error("SAX error", e);
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                logger.error("SAX fatal error", e);
            }

        });
        logger.info("------ Validation finished");
    }

}
