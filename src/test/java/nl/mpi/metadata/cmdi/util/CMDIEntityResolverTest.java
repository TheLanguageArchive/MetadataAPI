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
package nl.mpi.metadata.cmdi.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class CMDIEntityResolverTest {

    private CMDIEntityResolver entityResolver;

    @Before
    public void setUp() {
	entityResolver = new CMDIEntityResolver();
    }

    @After
    public void tearDown() {
	entityResolver = null;
    }

    /**
     * Test of resolveEntity method, of class CMDIEntityResolver.
     * Tests resolving http://www.w3.org/2001/xml.xsd, which should come from a local resource mirror
     */
    @Test
    public void testResolveEntityXmlXsd() throws Exception {
	InputSource resolveEntity = entityResolver.resolveEntity(null, "http://www.w3.org/2001/xml.xsd");
	// w3org xml.xsd should be resolved from resources in JAR
	InputStream is = resolveEntity.getByteStream();
	assertNotNull(is);
	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	// Check first two lines to see if it matches xml.xsd
	assertEquals("<?xml version='1.0'?>", reader.readLine());
	assertEquals("<xs:schema targetNamespace=\"http://www.w3.org/XML/1998/namespace\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xml:lang=\"en\">", reader.readLine());
	reader.close();
    }

    /**
     * Test of resolveEntity method, of class CMDIEntityResolver.
     * Tests resolving google.com, which should simply return a reference by systemId
     */
    @Test
    public void testResolveEntity() throws Exception {
	InputSource resolveEntity = entityResolver.resolveEntity(null, "http://www.google.com");
	assertEquals("http://www.google.com", resolveEntity.getSystemId());
    }
}
