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
package nl.mpi.metadata.identifierresolver;

import java.net.URI;
import nl.mpi.metadata.api.model.MetadataDocument;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import static org.jmock.Expectations.returnValue;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class URLResolverTest {

    private Mockery context = new JUnit4Mockery();

    /**
     * Test of canResolve method, of class URLResolver.
     */
    @Test
    public void testCanResolve() throws Exception {
	URLResolver instance = new URLResolver();
	boolean result;

	final MetadataDocument document = context.mock(MetadataDocument.class);
	context.checking(new Expectations() {
	    {
		allowing(document).getFileLocation();
		will(returnValue(URI.create("file://Users/me/mydocument.cmdi")));
	    }
	});

	// File URI
	result = instance.canResolve(document, new URI("file://mydocument.txt"));
	assertTrue(result);
	// Web URI (http)
	result = instance.canResolve(document, new URI("http://www.google.com"));
	assertTrue(result);
	// Web URI (https)
	result = instance.canResolve(document, new URI("https://www.google.com"));
	assertTrue(result);
	// Relative URI
	result = instance.canResolve(document, new URI("test.xml"));
	assertTrue(result);

	// Handle
	result = instance.canResolve(document, new URI("hdl:123-456"));
	assertFalse(result);
	// Mailto
	result = instance.canResolve(document, new URI("mailto:cmdi@clarin.eu"));
	assertFalse(result);
    }

    @Test
    public void testCanResolveRelativeParent() throws Exception {
	URLResolver instance = new URLResolver();
	boolean result;
	final MetadataDocument document = context.mock(MetadataDocument.class);
	context.checking(new Expectations() {
	    {
		allowing(document).getFileLocation();
		will(returnValue(URI.create("mydocument.cmdi")));
	    }
	});// Relative URI
	result = instance.canResolve(document, new URI("test.xml"));
	assertFalse(result);
    }

    @Test
    public void testCanResolveNoParent() throws Exception {
	URLResolver instance = new URLResolver();
	boolean result;
	// Absolute file URI, no parent
	result = instance.canResolve(null, new URI("file://mydocument.txt"));
	assertTrue(result);
	// Relative file URI, no parent
	result = instance.canResolve(null, new URI("mydocument.txt"));
	assertFalse(result);
    }

    /**
     * Test of resolveIdentifier method, of class URLResolver.
     */
    @Test
    public void testResolveIdentifier() {
	//TODO: Implement as soon as MetadataDocument has been implemented
//	System.out.println("resolveIdentifier");
//	MetadataDocument document = null;
//	URI identifier = null;
//	URLResolver instance = new URLResolver();
//	URI expResult = null;
//	URI result = instance.resolveIdentifier(document, identifier);
//	assertEquals(expResult, result);
//	// TODO review the generated test code and remove the default call to fail.
//	fail("The test case is a prototype.");
    }
}
