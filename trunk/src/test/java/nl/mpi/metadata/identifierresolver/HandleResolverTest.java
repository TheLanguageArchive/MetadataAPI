/*
 * Copyright (C) 2013 Max Planck Institute for Psycholinguistics
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
import java.net.URL;
import nl.mpi.metadata.api.model.MetadataDocument;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Twan Goosen <twan.goosen@mpi.nl>
 */
public class HandleResolverTest {

    private Mockery context = new JUnit4Mockery();

    /**
     * Test of canResolve method, of class HandleResolver.
     */
    @Test
    public void testCanResolve() {
	HandleResolver instance = new HandleResolver();
	MetadataDocument document = context.mock(MetadataDocument.class);

	boolean result;
	result = instance.canResolve(document, URI.create("hdl:123-456"));
	assertTrue(result);
	result = instance.canResolve(document, URI.create("http://myserver/myfile.txt"));
	assertFalse(result);
	result = instance.canResolve(document, URI.create("http://hdl.handle.net/123-456"));
	assertFalse(result);
    }

    /**
     * Test of resolveIdentifier method, of class HandleResolver.
     */
    @Test
    public void testResolveIdentifier() throws Exception {
	HandleResolver instance = new HandleResolver();
	URL result = instance.resolveIdentifier(context.mock(MetadataDocument.class), URI.create("hdl:123-456"));
	assertEquals(new URL("http://hdl.handle.net/123-456"), result);
    }

    /**
     * Test of resolveIdentifier method, of class HandleResolver.
     */
    @Test(expected = IdentifierResolutionException.class)
    public void testResolveIdentifierIllegalInput() throws Exception {
	HandleResolver instance = new HandleResolver();
	instance.resolveIdentifier(context.mock(MetadataDocument.class), URI.create("http://www.mpi.nl"));
    }
}
