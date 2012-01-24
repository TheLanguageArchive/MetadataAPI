/*
 * Copyright (C) 2012 The Max Planck Institute for Psycholinguistics
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

import java.io.File;
import java.net.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class URLResolverTest {

    public URLResolverTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of canResolve method, of class URLResolver.
     */
    @Test
    public void testCanResolve() throws Exception {
	URLResolver instance = new URLResolver();
	boolean result;
	
	// File URI
	result = instance.canResolve(null, File.createTempFile("test", null).toURI());
	assertTrue(result);
	// Web URI (http)
	result = instance.canResolve(null, new URI("http://www.google.com"));
	assertTrue(result);
	// Web URI (https)
	result = instance.canResolve(null, new URI("https://www.google.com"));
	assertTrue(result);
	// Relative URI
	result = instance.canResolve(null, new URI("test.xml"));
	assertTrue(result);
	
	// Mailto
	result = instance.canResolve(null, new URI("mailto:cmdi@clarin.eu"));
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
