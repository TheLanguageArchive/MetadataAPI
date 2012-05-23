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

import java.net.URI;
import java.util.Collection;
import java.util.List;
import nl.mpi.metadata.api.events.MetadataDocumentListener;
import nl.mpi.metadata.api.events.MetadataElementListener;
import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import nl.mpi.metadata.api.type.ContainedMetadataElementType;
import nl.mpi.metadata.api.type.MetadataDocumentType;
import nl.mpi.metadata.api.type.MetadataElementType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class MetadataElementExceptionTest {

    private MetadataDocument document;
    private MetadataElement element;
    private Throwable cause;
    private MetadataElementException exception;
    private final static String MESSAGE = "My exception message";

    @Before
    public void setUp() {

	document = new MetadataDocument() {

	    public MetadataDocumentType getType() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public URI getFileLocation() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public Collection getHeaderInformation() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void addMetadataDocumentListener(MetadataDocumentListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void removeMetadataDocumentListener(MetadataDocumentListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public MetadataElement getChildElement(String path) throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public List getChildren() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public boolean addChildElement(MetadataElement element) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public boolean removeChildElement(MetadataElement element) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public Collection getDocumentReferences() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public String getName() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public MetadataDocument getMetadataDocument() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void addMetadataElementListener(MetadataElementListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void removeMetadataElementListener(MetadataElementListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public String getDisplayValue() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public boolean canAddInstanceOfType(ContainedMetadataElementType type) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void setFileLocation(URI uri) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public String getPathString() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }
	};

	element = new MetadataElement() {

	    public String getName() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public MetadataElementType getType() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public MetadataDocument getMetadataDocument() {
		return document;
	    }

	    public void addMetadataElementListener(MetadataElementListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void removeMetadataElementListener(MetadataElementListener listener) {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public String getDisplayValue() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public String getPathString() {
		throw new UnsupportedOperationException("Not supported yet.");
	    }
	};

	cause = new Exception();
	exception = new MetadataElementException(element, MESSAGE, cause);
    }

    @Test
    public void testGetElement() {
	assertEquals(element, exception.getElement());
    }

    @Test
    public void testGetDocument() {
	assertEquals(document, exception.getDocument());
    }

    @Test
    public void testGetMessage() {
	assertEquals(MESSAGE, exception.getMessage());
    }

    @Test
    public void testGetCause() {
	assertEquals(cause, exception.getCause());
    }
}
