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

import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIValidationRunner {
    
    private final static Logger logger = LoggerFactory.getLogger(CMDIValidationRunner.class);
//    private final static URL DOCUMENT = CMDIValidationRunner.class.getResource("/cmdi/orphanCollection.cmdi");
    private final static URL DOCUMENT = CMDIValidationRunner.class.getResource("/cmdi/SmallTestProfile-instance-invalid.cmdi"); //invalid CMDI

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        final CMDIApi api = new CMDIApi();
        
        logger.info("Reading document {}", DOCUMENT);
        final CMDIDocument document = api.getMetadataDocument(DOCUMENT);
        
        logger.info("Validating document");
        
        final AtomicInteger count = new AtomicInteger(0);
        
        api.validateMetadataDocument(document, new ErrorHandler() {
            
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                count.incrementAndGet();
                logger.warn("line {}: {}", exception.getLineNumber(), exception.getMessage());
            }
            
            @Override
            public void error(SAXParseException exception) throws SAXException {
                count.incrementAndGet();
                logger.error("line {}: {}", exception.getLineNumber(), exception.getMessage());
            }
            
            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                count.incrementAndGet();
                logger.error("line {}: FATAL - {}", exception.getLineNumber(), exception.getMessage());
            }
            
        });
        
        logger.info("Validation complete. Found {} warnings or errors", count);
    }
    
}
