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

import nl.mpi.metadata.api.validation.MetadataValidator;
import nl.mpi.metadata.cmdi.api.model.CMDIDocument;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MockCMDIValidator implements MetadataValidator<CMDIDocument> {

    private int warnings;
    private int errors;
    private int fatalErrors;

    /**
     * 
     * @param warnings Number of warnings to produce
     * @param errors Number of errors to produce
     * @param fatalErrors Number of fatal errors to produce
     */
    public MockCMDIValidator(int warnings, int errors, int fatalErrors) {
	this.warnings = warnings;
	this.errors = errors;
	this.fatalErrors = fatalErrors;
    }

    public void validateMetadataDocument(CMDIDocument document, ErrorHandler errorHandler) throws SAXException {
	for (int i = 0; i < warnings; i++) {
	    errorHandler.warning(new SAXParseException("Warning " + i, null));
	}
	for (int i = 0; i < errors; i++) {
	    errorHandler.error(new SAXParseException("Error " + i, null));
	}
	for (int i = 0; i < fatalErrors; i++) {
	    errorHandler.fatalError(new SAXParseException("Fatal error " + i, null));
	}
    }
}
