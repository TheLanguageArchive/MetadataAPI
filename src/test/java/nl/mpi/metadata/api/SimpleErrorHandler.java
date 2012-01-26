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
package nl.mpi.metadata.api;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class SimpleErrorHandler extends DefaultHandler {

    protected List<SAXParseException> warnings = new ArrayList<SAXParseException>();
    protected List<SAXParseException> errors = new ArrayList<SAXParseException>();
    protected List<SAXParseException> fatalErrors = new ArrayList<SAXParseException>();

    @Override
    public void warning(SAXParseException exception) throws SAXException {
	warnings.add(exception);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
	errors.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
	fatalErrors.add(exception);
    }

    /**
     * Get the value of warnings
     *
     * @return the value of warnings
     */
    public List<SAXParseException> getWarnings() {
	return warnings;
    }

    /**
     * Get the value of errors
     *
     * @return the value of errors
     */
    public List<SAXParseException> getErrors() {
	return errors;
    }

    /**
     * Get the value of fatalErrors
     *
     * @return the value of fatalErrors
     */
    public List<SAXParseException> getFatalErrors() {
	return fatalErrors;
    }
}
