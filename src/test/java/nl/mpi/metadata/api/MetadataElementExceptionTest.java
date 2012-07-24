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

import nl.mpi.metadata.api.model.MetadataDocument;
import nl.mpi.metadata.api.model.MetadataElement;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
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
    private final Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() {

	document = context.mock(MetadataDocument.class);
	element = context.mock(MetadataElement.class);

	context.checking(new Expectations() {

	    {
		oneOf(element).getMetadataDocument();
		will(returnValue(document));
	    }
	});
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
